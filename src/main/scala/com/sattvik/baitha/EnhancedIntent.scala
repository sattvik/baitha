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

import Predef.{augmentString => _, wrapString => _, _}
import java.io.Serializable
import java.util.ArrayList
import android.content.Intent
import android.os.{Parcelable, Bundle}
import scala.language.implicitConversions

import extra._

/** Wraps an intent in order to provide an interface that makes it easy to
  * work with `TypedExtra` objects.
  *
  * == Working with `TypedExtra` objects ==
  *
  * In order to make use of this class, you will need to have some instances
  * of `TypedExtra`, generally created as singleton objects, as follows:
  *
  * {{{
  * object FooExtra extends BooleanExtra("com.example.Foo", false)
  * object BarExtra extends StringExtra("com.example.Bar")
  * }}}
  *
  * In order to make use of these, you should convert an intent to an
  * `EnhancedIntent`, usually by using the implicit conversions in the
  * companion object.
  *
  * {{{
  * import com.sattvik.baitha.EnhancedIntent._
  *
  * val intent: Intent = …
  *
  * // check to see if the extra is in the intent
  * intent.hasExtra(FooExtra)
  *
  * // remove an extra from the intent
  * intent.removeExtra(BarExtra)
  * }}}
  *
  *
  * === Getting extras out of an intent ===
  *
  * There are two new ways to get a value out of an intent.  First,
  * you can just get the value, getting whatever the default may be if the
  * extra is not in the intent:
  *
  * {{{
  * val foo: Boolean = intent.getExtra(FooExtra)
  * val foo: String = intent.getExtra(BarExtra)
  * }}}
  *
  * You can also use an option, so that if there is no value associated with
  * the extra in the intent, you will get `None` as a result.  Presuming that
  * `intent` has no FooExtra and a BarExtra with the value `"Scala"`:
  *
  * {{{
  * intent.getExtraOption(FooExtra) // returns None
  * intent.getExtraOption(BarExtra) // returns Some("Scala")
  * }}}
  *
  * === Putting extras into an intent ===
  *
  * There are three new ways to put a value into an intent:
  *
  * 1. Putting the value directly:
  * {{{
  * intent.putExtra(FooExtra, false)
  * intent.putExtra(BarExtra, "Baitha")
  * }}}
  *
  * 2. Using an option value, where a `None` is effectively a no-op
  * {{{
  * intent.putExtra(FooExtra, Some(false))
  * intent.putExtra(BarExtra, None) // does nothing
  * }}}
  *
  * 3. Using an applied value to an extra:
  * {{{
  * val myFoo = FooExtra(true)
  * val myBar = BarExtra("Extra Bar!")
  *
  * intent.putExtra(myFoo)
  * intent.putExtra(myBar)
  * }}}
  *
  *
  * == Working with maps ==
  *
  * It is possible to use a map of names to values directly with the
  * `putExtras` method.  Please consult the documentation for the method for
  * more information about its limitations.
  *
  * {{{
  * val extras = Map("Foo" -> false, "Bar" -> "Scala")
  * intent.putExtras(extras)
  * }}}
  *
  * @author Daniel Solano Gómez */
class EnhancedIntent(intent: Intent) {
  /** Returns true if there is a value associated with the given extra. */
  def hasExtra(extra: TypedExtra[_]): Boolean = intent.hasExtra(extra.name)

  /** Retrieves extended data from the intent.  If there is no data with that
    * name in the intent, the returned value is dependent on the type of the
    * extra.  Value types will return a default value, and reference types will
    * return null.
    *
    * @param extra the extra to retrieve from the intent
    *
    * @return the value for the extra */
  def getExtra[A](extra: TypedExtra[A]): A = extra.getFrom(intent)

  /** Retrieves extended data from the intent.  If there is no data with that
    * name in the intent, returns `None`.
    *
    * @param extra the extra to retrieve from the intent
    *
    * @return the value for the extra as an option */
  def getExtraOption[A](extra: TypedExtra[A]): Option[A] = {
    if (hasExtra(extra)) Some(getExtra(extra)) else None
  }

  /** Puts the value into the intent as an extra.
    *
    * @param value the value to put into the intent
    *
    * @return the intent for chained method calls */
  def putExtra[A](value: TypedExtra[A]#Value): EnhancedIntent = {
    value.extra.putInto(intent, value.value)
    this
  }

  /** Puts the value into the intent as an extra.
    *
    * @param extra the extra as which the value should be put into the intent
    * @param value the value to put into the intent
    *
    * @return the intent for chained method calls */
  def putExtra[A](extra: TypedExtra[A], value: A): EnhancedIntent = {
    extra.putInto(intent, value)
    this
  }

  /** Puts the value, if defined, into the intent as an extra.  Naturally,
    * if the value is `None`, does nothing.
    *
    * @param extra the extra as which the value should be put into the intent
    * @param value the value to put into the intent, or `None`, in which case
    * nothing happens
    *
    * @return the intent for chained method calls */
  def putExtra[A](extra: TypedExtra[A], value: Option[A]): EnhancedIntent = {
    if (value.isDefined) {
      putExtra(extra, value.get)
    }
    this
  }

  /** Puts an entire map of extras into the intent.  The map should consist
    * of string names to values.  The values must be of a supported extra
    * type.  Note that due to type erasure, empty `ArrayList` extras are not
    * supported.
    *
    * @param extras a map of string extra names to the their values
    *
    * @return the intent for chained method calls */
  def putExtras(extras: Map[String, Any])(
    implicit sdkVersions: SdkVersions = SdkVersions
  ): EnhancedIntent = {
    extras foreach {
      case (name, value) =>
        value match {
          // prefer string over CharSequence
          case v: String            => intent.putExtra(name, v)
          case v: Array[Boolean]    => intent.putExtra(name, v)
          case v: Array[Byte]       => intent.putExtra(name, v)
          case v: Array[Char]       => intent.putExtra(name, v)
          case v: Array[Double]     => intent.putExtra(name, v)
          case v: Array[Float]      => intent.putExtra(name, v)
          case v: Array[Int]        => intent.putExtra(name, v)
          case v: Array[Long]       => intent.putExtra(name, v)
          case v: Array[Parcelable] => intent.putExtra(name, v)
          case v: Array[Short]      => intent.putExtra(name, v)
          case v: Array[String]     => intent.putExtra(name, v)
          case v: Boolean           => intent.putExtra(name, v)
          case v: Bundle            => intent.putExtra(name, v)
          case v: Byte              => intent.putExtra(name, v)
          case v: Char              => intent.putExtra(name, v)
          case v: CharSequence      => intent.putExtra(name, v)
          case v: Double            => intent.putExtra(name, v)
          case v: Float             => intent.putExtra(name, v)
          case v: Int               => intent.putExtra(name, v)
          case v: Long              => intent.putExtra(name, v)
          case v: Parcelable        => intent.putExtra(name, v)
          case v: Short             => intent.putExtra(name, v)
          // defer to CharSequenceArrayExtra
          case v: Array[CharSequence] => putExtra(
            CharSequenceArrayExtra(name)(sdkVersions),
            v)
          // ArrayList extras are not supported since the correct type cannot
          // be known
          case v: ArrayList[_] => putArrayListExtra(name, v, sdkVersions)
          // Serializable is the least-preferred match
          case v: Serializable => intent.putExtra(name, v)
          case v               => throw new IllegalArgumentException(
            String.format("%s has an invalid type for an extra", v.toString))
        }
    }
    this
  }

  /** Helper method to aid in putting an arbitrary ArrayList as an extra in
    * an intent. */
  private def putArrayListExtra(
    name: String,
    value: ArrayList[_],
    sdkVersions: SdkVersions
  ) {
    if (value.isEmpty) {
      throw new IllegalArgumentException(
        "Empty ArrayList extras are not supported due to type erasure.")
    } else {
      value.get(0) match {
        case _: java.lang.Integer => intent.putExtra(
          name,
          value.asInstanceOf[ArrayList[java.lang.Integer]])
        case _: Parcelable        => intent.putExtra(
          name,
          value.asInstanceOf[ArrayList[Parcelable]])
        case _: String            => intent.putExtra(
          name,
          value.asInstanceOf[ArrayList[String]])
        case _: CharSequence      => putExtra(
          CharSequenceArrayListExtra(name)(sdkVersions),
          value.asInstanceOf[ArrayList[CharSequence]])
        case _                    => throw new IllegalArgumentException(
          "Invalid type parameter for an ArrayList extra")
      }
    }
  }

  /** Remove the given extra from the intent. */
  def removeExtra(extra: TypedExtra[_]) {
    intent.removeExtra(extra.name)
  }
}

/** Provides an implicit conversion from an `Intent` to an `EnhancedIntent`.
  *
  * @author Daniel Solano Gómez */
object EnhancedIntent {
  /** Converts an `Intent` to an `EnhancedIntent`. */
  implicit def enhanceIntent(intent: Intent): EnhancedIntent = {
    new EnhancedIntent(intent)
  }
}
