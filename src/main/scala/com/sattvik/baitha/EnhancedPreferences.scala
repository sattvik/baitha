/*
 * Copyright © 2011-2012 Sattvik Software & Technology Resources, Ltd. Co.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.sattvik.baitha

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.sattvik.baitha.EnhancedPreferences.EnhancedEditor

/** Provides additional functionality to Android's SharedPreferences to make
  * working with Android preferences easier and more fun.
  *
  * @author Daniel Solano Gómez */
class EnhancedPreferences(wrapped: SharedPreferences) {
  /** Checks whether or not the preference contains the given preference.
    *
    * @param preference the preference to check
    *
    * @return true if the preference exists in the preferences, otherwise false
    */
  def contains(preference: TypedPreference[_]) = {
    wrapped.contains(preference.key)
  }

  /** Retrieves a value from the preference.
    *
    * @param preference the preference to retrieve
    *
    * @return the preference value if it exists and is of the correct type.
    * If the preference is not in the preferences, the result is
    * implementation-defined.
    *
    * @throws ClassCastException if the value is of the wrong type for the
    * preference
    */
  def get[A](preference: TypedPreference[A]): A = {
    preference.getFrom(wrapped)
  }

  /** Retrieves a value from the preference as an option.
    *
    * @param preference the preference to retrieve
    *
    * @return the preference value in an options if it exists and is of the
    * correct type.  If the preference is not in the preferences, returns
    * `None`.
    *
    * @throws ClassCastException if the value is of the wrong type for the
    * preference
    */
  def getOption[A](preference: TypedPreference[A]): Option[A] = {
    if (contains(preference)) Some(get(preference)) else None
  }

  /** Executes the body allowing the code to make changes to the underlying
    * preferences.  Unless `abort()` is called within the body, the preferences
    * will be committed after the body has executed.
    *
    * @param a set of statements that may use the supplied editor to make
    * changes to the preferences.
    *
    * @return true if the preferences were successfully saved to persistent
    * storage
    */
  def withEditor(body: EnhancedEditor => Unit): Boolean = {
    val wrappedEditor = wrapped.edit()
    val editor = new EnhancedEditor(wrappedEditor)
    body(editor)
    if (editor.aborted) false else wrappedEditor.commit()
  }

  /** Registers a function that will be called whenever one or more
    * preferences change.  You should use the returned token to unregister the
    * function later using `unregisterOnChange`.
    *
    * @param interestingPreferences preferences for which the callback
    * function should be executed
    * @param fn the function that will be called when one of the
    * `interestingPreferences` has changed.  This function takes two
    * arguments, the preference that was changed and its new value in an
    * option.  If the preference was removed, the value will be `None`.
    *
    * @return a token that can be used to unregister the listener using
    * `unregisterOnChange`
    */
  def onChange(
    interestingPreferences: TypedPreference[_]*
  )(
    fn: (TypedPreference[_], Option[_]) => Unit
  ): Token = {
    val listener = new OnSharedPreferenceChangeListener {
      def onSharedPreferenceChanged(p1: SharedPreferences, key: String) {
        interestingPreferences find(_.key == key) foreach { tpref =>
          fn(tpref,getOption(tpref))
        }
      }
    }
    wrapped.registerOnSharedPreferenceChangeListener(listener)
    listener
  }

  /** Unregisters a listener using the token returned by `onChange`. */
  def unregisterOnChange(token: Token) {
    wrapped.unregisterOnSharedPreferenceChangeListener(token)
  }

  type Token = SharedPreferences.OnSharedPreferenceChangeListener
}

/** Companion object for the `EnhancedPreferences` class that adds an
  * implicit conversion to the enhanced preferences type as well as the
  * definition of the enhanced editor.
  *
  * @author Daniel Solano Gómez */
object EnhancedPreferences {
  /** This class is the enhanced complement to `SharedPreferences.Editor`.
    * It allows the use of `TypedPreference` objects to modify ANdroid
    * preferences. */
  class EnhancedEditor private[EnhancedPreferences](editor: SharedPreferences.Editor) {
    /** Whether or not the transaction should be aborted. */
    private[EnhancedPreferences] var aborted = false

    /** Sets the preference to the given value.
      *
      * @param preference the preference to set
      * @param value the new value for the preference
      */
    def put[A](preference: TypedPreference[A], value: A) {
      preference.putInto(editor, value)
    }

    /** Either sets or removes the preference using the given option.  If the
      * value is defined, then the preference will be set with the given
      * value.  If the value is `None`, the preference will be removed.
      *
      * @param preference the preference to set
      * @param value the possible value for the preference
      */
    def put[A](preference: TypedPreference[A], value: Option[A]) {
      if (value.isDefined) put(preference, value.get) else remove(preference)
    }

    /** Allows use of a value generated from a preference to set the value of
      * the preference.
      *
      * {{{
      * val IntPref = TypedPreference[Int]("IntPref")
      * val prefVal = IntPref(42)
      *
      * preferences.withEditor { editor =>
      *   editor.put(prefVal)
      * }
      * }}}
      *
      * @param value the new preference and value to set
      */
    def put[A](preferenceValue: TypedPreference.Value[A]) {
      put(preferenceValue.preference, preferenceValue.value)
    }

    /** Removes the given preference from the preferences.
      *
      * @param preference the preference to remove */
    def remove(preference: TypedPreference[_]) {
      editor.remove(preference.key)
    }

    /** Removes all preferences. */
    def clear() {editor.clear()}

    /** If called, then the preferences will not be committed and all changes
      * will be lost. */
    def abort() {aborted = true}
  }

  /** Implicit conversion from the standard preferences to the enhanced
    * preferences. */
  implicit def enhancePreferences(
    preferences: SharedPreferences
  ): EnhancedPreferences = {
    new EnhancedPreferences(preferences)
  }
}
