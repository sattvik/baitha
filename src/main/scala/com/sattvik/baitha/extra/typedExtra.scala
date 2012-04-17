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
package com.sattvik.baitha.extra

import java.io.Serializable
import java.util.ArrayList
import android.content.Intent
import android.os.{Parcelable, Bundle}
import com.sattvik.baitha.SdkVersions

/** The `TypedExtra` trait is primarily intended to be used with the
  * `EnhancedIntent` class, so please consult the documentation there.
  *
  * Additionally, the `TypedExtra` trait provides an apply method and an
  * extractor so that the `TypedExtra` may be used similarly to a case class.
  * In particular, the `apply` function creates a value object that can be
  * used with `EnhancedIntent` as follows:
  *
  * {{{
  * import com.sattvik.baitha.EnhancedIntent._
  *
  * val intent: Intent = … // some intent
  * val FooExtra = IntExtra("foo") // typed extra
  *
  * // equivalent to intent.putExtra("foo", 3), or, using Baitha
  * // intent.putExtra(FooExtra, 3)
  * intent.putExtra(FooExtra(3))
  * }}}
  *
  * Additionally, the extra can be used as an extractor with an intent:
  *
  * {{{
  * val intent: Intent = … // some intent
  * val FooExtra = IntExtra("foo") // typed extra
  *
  * intent match {
  *   case FooExtra(v) => // if "foo" is in the intent, sets v to its value
  * }
  * }}}
  *
  * @author Daniel Solano Gómez */
sealed trait TypedExtra[A] { outer =>
  /** Returns the name for the extra, which is used as the key when inserting
    * into an intent. */
  def name: String

  /** Returns the value associated with this extra from the intent.  For
    * value types, a default value is returned if the extra is not in the
    * intent.  For reference types, typically null is returned for a missing
    * extra. */
  def getFrom(intent: Intent): A

  /** Inserts the value into the intent using the name of this extra. */
  def putInto(intent: Intent, value: A)

  /** Extracts the value associated with this intent, if available. */
  final def unapply(intent: Intent): Option[A] = {
    if(intent.hasExtra(name)) Some(getFrom(intent)) else None
  }

  /** Creates a value object that associates a value with this extra.  The
    * resulting object can be inserted into an intent directly using an
    * `EnhancedIntent`. */
  final def apply(value: A): Value = new Value(value)

  /** A simple object that associates a value with the extra. */
  final class Value(val value: A) {
    val extra = outer
  }
}

/** A mix-in trait for extras that require a default value.
  *
  * @tparam A the type for the default value
  *
  * @author Daniel Solano Gómez */
sealed trait DefaultValue[A] {
  /** The default value. */
  def default: A
}

/** Creates a new extra that can contain a boolean value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class BooleanExtra(name: String, default: Boolean = false)
    extends TypedExtra[Boolean] with DefaultValue[Boolean] {
  def getFrom(intent: Intent) = intent.getBooleanExtra(name, default)

  def putInto(intent: Intent, value: Boolean) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a boolean array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class BooleanArrayExtra(name: String)
    extends TypedExtra[Array[Boolean]] {
  def getFrom(intent: Intent) = intent.getBooleanArrayExtra(name)

  def putInto(intent: Intent, value: Array[Boolean]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a `Bundle` reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class BundleExtra(name: String) extends TypedExtra[Bundle] {
  def getFrom(intent: Intent) = intent.getBundleExtra(name)

  def putInto(intent: Intent, value: Bundle) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a byte value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class ByteExtra(name: String, default: Byte = 0)
    extends TypedExtra[Byte] with DefaultValue[Byte] {
  def getFrom(intent: Intent) = intent.getByteExtra(name, default)

  def putInto(intent: Intent, value: Byte) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a byte array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class ByteArrayExtra(name: String) extends TypedExtra[Array[Byte]] {
  def getFrom(intent: Intent) = intent.getByteArrayExtra(name)

  def putInto(intent: Intent, value: Array[Byte]) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a character value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class CharExtra(name: String, default: Char = 0)
    extends TypedExtra[Char] with DefaultValue[Char] {
  def getFrom(intent: Intent) = intent.getCharExtra(name, default)

  def putInto(intent: Intent, value: Char) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a character array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class CharArrayExtra(name: String) extends TypedExtra[Array[Char]] {
  def getFrom(intent: Intent) = intent.getCharArrayExtra(name)

  def putInto(intent: Intent, value: Array[Char]) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a `CharSequence` reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class CharSequenceExtra(name: String) extends TypedExtra[CharSequence] {
  def getFrom(intent: Intent) = intent.getCharSequenceExtra(name)

  def putInto(intent: Intent, value: CharSequence) {
    intent.putExtra(name, value)
  }
}

/** This object contains a factory that can mimic the behaviour of a case
  * class factory.  This is needed as `CharSequence` array extras are not
  * supported before API level 8 (FroYo).
  *
  * @author Daniel Solano Gómez */
object CharSequenceArrayExtra {
  /** Creates a new extra that can contain a `CharSequence` array reference.
    * However, if the runtime is API level 7 or older, throws an
    * `UnsupportedOperationException` as these platforms do not support this
    * type of extra.
    *
    * @param name the name of the extra
    * @param sdkVersion an implicit parameter that is *for testing only*.
    *
    * @author Daniel Solano Gómez */
  def apply(name: String)(
    implicit sdkVersions: SdkVersions = SdkVersions
  ): TypedExtra[Array[CharSequence]] = {
    if (sdkVersions.currentSdkBefore(SdkVersions.FroYo)) {
      PreFroYoUnsupportedExtra[Array[CharSequence]](name)
    } else {
      FroYoCharSequenceArrayExtra(name)
    }
  }

  /** The actual implementation of a working `CharSequence` array extra. */
  private case class FroYoCharSequenceArrayExtra(name: String)
      extends TypedExtra[Array[CharSequence]] {
    def getFrom(intent: Intent) = intent.getCharSequenceArrayExtra(name)

    def putInto(intent: Intent, value: Array[CharSequence]) {
      intent.putExtra(name, value)
    }
  }
}

/** This object contains a factory that can mimic the behaviour of a case
  * class factory.  This is needed as extras of the type
  * `ArrayList[CharSequence]` are not supported before API level 8 (FroYo).
  *
  * @author Daniel Solano Gómez */
object CharSequenceArrayListExtra {
  /** Creates a new extra that can contain a reference to an `ArrayList` of
    * `CharSequence` objects. However, if the runtime is API level 7 or older,
    * throws an `UnsupportedOperationException` as these platforms do not
    * support this type of extra.
    *
    * @param name the name of the extra
    * @param sdkVersion an implicit parameter that is *for testing only*.
    *
    * @author Daniel Solano Gómez */
  def apply(name: String)(
    implicit sdkVersions: SdkVersions = SdkVersions
  ): TypedExtra[ArrayList[CharSequence]] = {
    if (sdkVersions.currentSdkBefore(SdkVersions.FroYo)) {
      PreFroYoUnsupportedExtra[ArrayList[CharSequence]](name)
    } else {
      FroYoCharSequenceArrayListExtra(name)
    }
  }

  /** The actual implementation of a working `CharSequence` `ArrayList`
    * extra. */
  private case class FroYoCharSequenceArrayListExtra(name: String)
      extends TypedExtra[ArrayList[CharSequence]] {
    def getFrom(intent: Intent) = intent.getCharSequenceArrayListExtra(name)

    def putInto(intent: Intent, value: ArrayList[CharSequence]) {
      intent.putExtra(name, value)
    }
  }
}

/** Utility object to throw exceptions. */
private[extra] object PreFroYoUnsupportedExtra {
  def apply[A: Manifest](name: String): TypedExtra[A] = {
    throw new UnsupportedOperationException(
      "%s extras are not available before API level 8".format(manifest[A]))
  }
}

/** Creates a new extra that can contain a double value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class DoubleExtra(name: String, default: Double = 0)
    extends TypedExtra[Double] with DefaultValue[Double] {
  def getFrom(intent: Intent) = intent.getDoubleExtra(name, default)

  def putInto(intent: Intent, value: Double) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a double array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class DoubleArrayExtra(name: String) extends TypedExtra[Array[Double]] {
  def getFrom(intent: Intent) = intent.getDoubleArrayExtra(name)

  def putInto(intent: Intent, value: Array[Double]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a float value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class FloatExtra(name: String, default: Float = 0)
    extends TypedExtra[Float] with DefaultValue[Float] {
  def getFrom(intent: Intent) = intent.getFloatExtra(name, default)

  def putInto(intent: Intent, value: Float) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a float array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class FloatArrayExtra(name: String) extends TypedExtra[Array[Float]] {
  def getFrom(intent: Intent) = intent.getFloatArrayExtra(name)

  def putInto(intent: Intent, value: Array[Float]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain an int value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class IntExtra(name: String, default: Int = 0)
    extends TypedExtra[Int] with DefaultValue[Int] {
  def getFrom(intent: Intent) = intent.getIntExtra(name, default)

  def putInto(intent: Intent, value: Int) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain an int array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class IntArrayExtra(name: String) extends TypedExtra[Array[Int]] {
  def getFrom(intent: Intent) = intent.getIntArrayExtra(name)

  def putInto(intent: Intent, value: Array[Int]) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a reference to an `ArrayList` of
  * `Integer` objects.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class IntegerArrayListExtra(name: String)
    extends TypedExtra[ArrayList[java.lang.Integer]] {
  def getFrom(intent: Intent) = intent.getIntegerArrayListExtra(name)

  def putInto(intent: Intent, value: ArrayList[java.lang.Integer]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a long value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class LongExtra(name: String, default: Long = 0)
    extends TypedExtra[Long] with DefaultValue[Long] {
  def getFrom(intent: Intent) = intent.getLongExtra(name, default)

  def putInto(intent: Intent, value: Long) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain an long array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class LongArrayExtra(name: String) extends TypedExtra[Array[Long]] {
  def getFrom(intent: Intent) = intent.getLongArrayExtra(name)

  def putInto(intent: Intent, value: Array[Long]) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a reference to a `Parcelable` object.
  * Note that it is possible to get references to a specific subclass of
  * `Parcelable`.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class ParcelableExtra[A <: Parcelable](name: String)
    extends TypedExtra[A] {
  def getFrom(intent: Intent): A = intent.getParcelableExtra(name)

  def putInto(intent: Intent, value: A) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a reference to an array of
  * `Parcelable` objects.  Note that the array must have a type of
  * `Parcelable` and not of a subclass.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class ParcelableArrayExtra(name: String)
    extends TypedExtra[Array[Parcelable]] {
  def getFrom(intent: Intent) = intent.getParcelableArrayExtra(name)

  def putInto(intent: Intent, value: Array[Parcelable]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a reference to an `ArrayList` of
  * `Parcelable` objects.  Note that the type parameter of the `ArrayList`
  * may be a specific subclass of `Parcelable`.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class ParcelableArrayListExtra[A <: Parcelable](name: String)
    extends TypedExtra[ArrayList[A]] {
  def getFrom(intent: Intent) = intent.getParcelableArrayListExtra(name)

  def putInto(intent: Intent, value: ArrayList[A]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a reference to a `Serializable` object.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class SerializableExtra[A <: Serializable](name: String)
    extends TypedExtra[A] {
  def getFrom(intent: Intent): A = {
    intent.getSerializableExtra(name).asInstanceOf[A]
  }

  def putInto(intent: Intent, value: A) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain a short value.
  *
  * @param name the name of the extra
  * @param default an optional default value for the extra
  *
  * @author Daniel Solano Gómez */
case class ShortExtra(name: String, default: Short = 0)
    extends TypedExtra[Short] with DefaultValue[Short] {
  def getFrom(intent: Intent) = intent.getShortExtra(name, default)

  def putInto(intent: Intent, value: Short) {intent.putExtra(name, value)}
}

/** Creates a new extra that can contain an short array reference.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class ShortArrayExtra(name: String) extends TypedExtra[Array[Short]] {
  def getFrom(intent: Intent) = intent.getShortArrayExtra(name)

  def putInto(intent: Intent, value: Array[Short]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a reference to a `String` object.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class StringExtra(name: String) extends TypedExtra[String] {
  def getFrom(intent: Intent) = intent.getStringExtra(name)

  def putInto(intent: Intent, value: String) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a reference to an array of `String`
  * objects.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class StringArrayExtra(name: String) extends TypedExtra[Array[String]] {
  def getFrom(intent: Intent) = intent.getStringArrayExtra(name)

  def putInto(intent: Intent, value: Array[String]) {
    intent.putExtra(name, value)
  }
}

/** Creates a new extra that can contain a reference to an `ArrayList` of
  * `String` objects.
  *
  * @param name the name of the extra
  *
  * @author Daniel Solano Gómez */
case class StringArrayListExtra(name: String)
    extends TypedExtra[ArrayList[String]] {
  def getFrom(intent: Intent) = intent.getStringArrayListExtra(name)

  def putInto(intent: Intent, value: ArrayList[String]) {
    intent.putExtra(name, value)
  }
}
