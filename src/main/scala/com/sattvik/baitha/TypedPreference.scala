/*
 * Copyright © 2011 Sattvik Software & Technology Resources, Ltd. Co.
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
import android.content.SharedPreferences.Editor

/** Allows creation of typed preferences that can be used with the
  * `EnhancedPreferences` class.
  *
  * @author Daniel Solano Gómez */
object TypedPreference {
  /** Creates a new typed preference object of the given type with the given
    * key.  Note that preferences created with this function will not
    * override the default default value.  If you need a different default
    * value, construct the object directly.
    *
    * @throws IllegalArgumentException if the type isn't natively supported
    */
  def apply[A : Manifest](key: String): TypedPreference[A] = {
    val factory = typeMap.get(manifest[A])
    val option = factory map {_(key).asInstanceOf[TypedPreference[A]]}
    option.getOrElse {
      throw new IllegalArgumentException(
        "'%s' is not a natively supported preference type".format(manifest[A]))
    }
  }

  /** Map to get a new preference object of the correct type. */
  private val typeMap = Map[Manifest[_], String => TypedPreference[_]](
    manifest[Boolean] -> {BooleanPreference.apply(_)},
    manifest[Float] -> {FloatPreference.apply(_)},
    manifest[Int] -> {IntPreference.apply(_)},
    manifest[Long] -> {LongPreference.apply(_)},
    manifest[String] -> {StringPreference.apply(_)})

  /** Trait for preference that require a default value. */
  trait Default[A] {
    def default: A
  }

  /** An implementation of the typed preferences for a `Boolean` type. */
  case class BooleanPreference(key: String, default: Boolean = false)
      extends TypedPreference[Boolean] with Default[Boolean] {
    def getFrom(preferences: SharedPreferences) = {
      preferences.getBoolean(key, default)
    }

    def putInto(editor: Editor, value: Boolean) {editor.putBoolean(key, value)}
  }

  /** An implementation of the typed preferences for a `Float` type. */
  case class FloatPreference(key: String, default: Float = 0.0f)
      extends TypedPreference[Float] with Default[Float] {
    def getFrom(preferences: SharedPreferences) = {
      preferences.getFloat(key, default)
    }

    def putInto(editor: Editor, value: Float) {editor.putFloat(key, value)}
  }

  /** An implementation of the typed preferences for an `Int` type. */
  case class IntPreference(key: String, default: Int = 0)
      extends TypedPreference[Int] with Default[Int] {
    def getFrom(preferences: SharedPreferences) = {
      preferences.getInt(key, default)
    }

    def putInto(editor: Editor, value: Int) {editor.putInt(key, value)}
  }

  /** An implementation of the typed preferences for a `Long` type. */
  case class LongPreference(key: String, default: Long = 0L)
      extends TypedPreference[Long] with Default[Long] {
    def getFrom(preferences: SharedPreferences) = {
      preferences.getLong(key, default)
    }

    def putInto(editor: Editor, value: Long) {editor.putLong(key, value)}
  }

  /** An implementation of the typed preferences for a `String` type. */
  case class StringPreference(key: String, default: String = "")
      extends TypedPreference[String] with Default[String] {
    def getFrom(preferences: SharedPreferences) = {
      preferences.getString(key, default)
    }

    def putInto(editor: Editor, value: String) {editor.putString(key, value)}
  }

  /** The type of a preference value. */
  case class Value[A] private[TypedPreference](
    preference: TypedPreference[A],
    value: A)
}

/** A typed preference is designed to be used with the `EnhancedPreferences`
  * class to make working with Android preferences easier and more fun.
  *
  * While there is built-in functionality for most of the standard Android
  * preference types, you can extend this trait to create more complex
  * preference types built from simpler pieces.  The main thing to keep in
  * mind is that you should ensure that a preference with the name in `key`
  * exists, otherwise your preference will be considered to be missing.
  *
  * @author Daniel Solano Gómez */
trait TypedPreference[A] {
  /** The name of the preference. */
  def key: String

  /** Extracts the preference from the preferences. */
  def getFrom(preferences: SharedPreferences): A

  /** Inserts the preference into the preferences. */
  def putInto(editor: SharedPreferences.Editor, value: A)

  /** Creates a new preference value object that can be used with the
    * enhanced editor's `put` method. */
  def apply(value: A) = TypedPreference.Value(this, value)

  /** A handy extractor to get the preference value out from the
    * preferences. */
  def unapply(preferences: EnhancedPreferences): Option[A] = {
    preferences.getOption(this)
  }
}
