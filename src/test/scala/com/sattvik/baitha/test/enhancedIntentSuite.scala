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

package com.sattvik.baitha.test

import java.io.Serializable
import java.util.ArrayList
import android.content.Intent
import android.os._
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest._
import org.scalatest.mock.MockitoSugar

import com.sattvik.baitha._
import com.sattvik.baitha.EnhancedIntent._
import com.sattvik.baitha.extra._

import AbstractTypedExtraSuite._

/** General tests for the `EnhancedIntent` class. */
class EnhancedIntentSuite
    extends Suite with MockitoSugar with OneInstancePerTest {
  val intent = mock[Intent]

  /** Tests that `putExtras` does nothing with any empty map. */
  def testPutExtrasEmpty() {
    val result = intent.putExtras(Map[String, Any]())
    verifyNoMoreInteractions(intent)
    assert(result.isInstanceOf[EnhancedIntent])
  }

  /** Tests that `putExtras` supports multiple items in a map. */
  def testPutExtrasMany() {
    val values = Map(
      "Boolean" -> true,
      "Int" -> 2,
      "String" -> "Baitha")
    intent.putExtras(values)
    verify(intent).putExtra("Boolean", true)
    verify(intent).putExtra("Int", 2)
    verify(intent).putExtra("String", "Baitha")
  }

  /** Tests that adding a Boolean extra from a map works. */
  def testPutExtrasBoolean() {
    val value = true
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  /** Tests that adding a Boolean array extra from a map works. */
  def testPutExtrasBooleanArray() {
    val value = Array(false, true, true)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  /** Tests that adding a Bundle extra from a map works. */
  def testPutExtrasBundle() {
    // Bundle cannot be mocked
    pending
  }

  /** Tests that adding a byte extra from a map works. */
  def testPutExtrasByte() {
    val value = 5.toByte
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  /** Tests that adding a byte array extra from a map works. */
  def testPutExtrasByteArray() {
    val value = Array[Byte](3, 1, 4)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  /** Tests that adding a char extra from a map works. */
  def testPutExtrasChar() {
    val value = 'a'
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  /** Tests that adding a char array extra from a map works. */
  def testPutExtrasCharArray() {
    val value = Array('S', 'c', 'a', 'l', 'a')
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasCharSequence() {
    val value = mock[CharSequence]
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasCharSequenceArrayFroYo() {
    val value = Array(mock[CharSequence], mock[CharSequence])
    intent.putExtras(Map("Foo" -> value))(FroYoSdkVersions)
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasCharSequenceArrayEclair() {
    assertUnsupportedAtAPI {
      val value = Array(mock[CharSequence], mock[CharSequence])
      intent.putExtras(Map("Foo" -> value))(EclairSdkVersions)
    }
  }

  def testPutExtrasCharSequenceArrayListEclair() {
    assertUnsupportedAtAPI {
      val value = new ArrayList[CharSequence] {
        add(mock[CharSequence])
        add(mock[CharSequence])
      }
      intent.putExtras(Map("Foo" -> value))(EclairSdkVersions)
    }
  }

  def testPutExtrasCharSequenceArrayListFroYo() {
    val value = new ArrayList[CharSequence] {
      add(mock[CharSequence])
      add(mock[CharSequence])
    }
    intent.putExtras(Map("Foo" -> value))(FroYoSdkVersions)
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasCharSequenceArrayListEmpty() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new ArrayList[CharSequence]))
    }
    expect(true)(ex.getMessage.contains("erasure"))
  }

  def testPutExtrasDouble() {
    val value = 42.0
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasDoubleArray() {
    val value = Array(42.0, 3.14159)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasFloat() {
    val value = 42.0f
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasFloatArray() {
    val value = Array(42.0f, 3.14159f)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasInt() {
    val value = 42
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasIntArray() {
    val value = Array(42, 1337)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasIntegerArrayList() {
    val value = new ArrayList[java.lang.Integer] {
      add(42)
      add(1337)
    }
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasIntegerArrayListEmpty() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new ArrayList[java.lang.Integer]))
    }
    expect(true)(ex.getMessage.contains("erasure"))
  }

  def testPutExtrasLong() {
    val value = 42L
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasLongArray() {
    val value = Array(42L, 1337L)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasParcelable() {
    val value = new DummyParcelable
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasParcelableArray() {
    val value = Array[Parcelable](new DummyParcelable, new DummyParcelable)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasParcelableArrayList() {
    val value = new ArrayList[Parcelable] {
      add(new DummyParcelable)
      add(new DummyParcelable)
    }
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasParcelableArrayListEmpty() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new ArrayList[Parcelable]))
    }
    expect(true)(ex.getMessage.contains("erasure"))
  }

  def testPutExtrasSerializable() {
    val value = new DummySerializable
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasShort() {
    val value = 42.toShort
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasShortArray() {
    val value = Array(42.toShort, 1337.toShort)
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasString() {
    val value = "Scala"
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasStringArray() {
    val value = Array("Scala", "Baitha")
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasStringArrayList() {
    val value = new ArrayList[String] {
      add("Scala")
      add("Baitha")
    }
    intent.putExtras(Map("Foo" -> value))
    verify(intent).putExtra("Foo", value)
  }

  def testPutExtrasStringArrayListEmpty() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new ArrayList[String]))
    }
    expect(true)(ex.getMessage.contains("erasure"))
  }

  def testPutExtrasUnsupportedArrayList() {
    val ex = intercept[IllegalArgumentException] {
      val value = new ArrayList[Option[_]] {
        add(None)
      }
      intent.putExtras(Map("Foo" -> value))
    }
    expect(true)(ex.getMessage.contains("type parameter"))
  }

  def testPutExtrasUnsupportedArrayListEmpty() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new ArrayList[Option[_]]))
    }
    expect(true)(ex.getMessage.contains("erasure"))
  }

  def testPutExtrasUnsupportedType() {
    val ex = intercept[IllegalArgumentException] {
      intent.putExtras(Map("Foo" -> new Object))
    }
    expect(true)(ex.getMessage.contains("invalid type"))
  }

  /** Checks that an exception with the proper type and message is thrown for
    * an unsupported extra for an API level. */
  private def assertUnsupportedAtAPI(body: => Unit) {
    val ex = intercept[UnsupportedOperationException] {
      body
    }
    assert(ex.getMessage.contains("API"))
  }

  /** Tests the `CharSequenceArrayExtra` class for the Eclair API level. */
  def testPreFroYoCharSequenceArrayExtra() {
    assertUnsupportedAtAPI {
      CharSequenceArrayExtra(Name)(EclairSdkVersions)
    }
  }

  /** Tests the `CharSequenceArrayListExtra` class for the Eclair API level. */
  def testPreFroYoCharSequenceArrayListExtra() {
    assertUnsupportedAtAPI {
      CharSequenceArrayListExtra(Name)(EclairSdkVersions)
    }
  }
}

/** Abstract base class for individual `TypedExtra` test suites. */
abstract class AbstractTypedExtraSuite[A]
    extends Suite with MockitoSugar with OneInstancePerTest {
  /** The regular extra to test. */
  protected val extra = createExtra(Name)
  /** The extra that is not present in the intent. */
  protected val missingExtra = createExtra(MissingName)
  /** The intent to use for testing. */
  protected val intent = mock[Intent]

  // stub responses for hasExtra
  when(intent.hasExtra(Name)).thenReturn(true)
  when(intent.hasExtra(MissingName)).thenReturn(false)

  /** Creates an extra with the given name. */
  protected def createExtra(name: String): TypedExtra[A]

  /** The value to expect as part of the extra. */
  protected def value: A

  /** Tests that `EnhancedIntent.hasExtra(TypedExtra[A])` works when the
    * extra is present. */
  def testHasExtra() {
    expect(true)(intent.hasExtra(extra))
  }

  /** Tests that `EnhancedIntent.hasExtra(TypedExtra[A])` works when the
    * extra is missing. */
  def testHasExtraMissing() {
    expect(false)(intent.hasExtra(missingExtra))
  }

  /** Tests that `EnhancedIntent.getExtra(TypedExtra[A])` works when the
    * extra is present. */
  def testGetExtra() {
    expect(value)(intent.getExtra(extra))
  }

  /** Tests that `EnhancedIntent.getExtraOption(TypedExtra[A])` works when the
    * extra is present. */
  def testGetExtraOption() {
    expect(Some(value))(intent.getExtraOption(extra))
  }

  /** Tests that `EnhancedIntent.getExtraOption(TypedExtra[A])` works when the
    * extra is missing. */
  def testGetExtraOptionMissing() {
    expect(None)(intent.getExtraOption(missingExtra))
  }

  /** Tests that `EnhancedIntent.putExtra(TypedExtra[A], A)` works. */
  def testPutExtra() {
    val result = intent.putExtra(extra, value)
    verifyPutExtra()
    assert(result.isInstanceOf[EnhancedIntent])
  }

  /** Verifies that the appropriate `Intent.putExtra` method was called. */
  def verifyPutExtra()

  /** Tests that `EnhancedIntent.putExtra(TypedExtra[A], Option[A])` works when
    * there is a value to add. */
  def testPutExtraOption() {
    val result = intent.putExtra(extra, Some(value))
    verifyPutExtra()
    assert(result.isInstanceOf[EnhancedIntent])
  }

  /** Tests that `EnhancedIntent.putExtra(TypedExtra[A], Option[A])` removes
    * the extra if given `None` as the value. */
  def testPutExtraOptionNone() {
    val result = intent.putExtra(extra, None)
    verifyNoMoreInteractions(intent)
    assert(result.isInstanceOf[EnhancedIntent])
  }

  /** Tests that `EnhancedIntent.putExtra(TypedExtra[A]#Value)` works. */
  def testPutExtraValue() {
    val result = intent.putExtra(extra(value))
    verifyPutExtra()
    assert(result.isInstanceOf[EnhancedIntent])
  }

  /** Tests that `EnhancedIntent.removeExtra(TypedExtra[A])` works. */
  def testRemoveExtra() {
    intent.removeExtra(extra)
    verify(intent).removeExtra(Name)
  }

  /** Tests direct use of the extractor in a `TypedExtra`. */
  def testExtractor() {
    expect(Some(value))(extra.unapply(intent))
  }

  /** Tests use of the extractor in a `TypedExtra` using an intent for
    * matching. */
  def testExtractorMatch() {
    expect(value) {
      intent match {
        case extra(v) => v
      }
    }
  }

  /** Tests direct use of the extractor in a `TypedExtra` when the extra is
    * missing. */
  def testExtractorMissing() {
    expect(None)(missingExtra.unapply(intent))
  }
}

/** Some handy constants. */
object AbstractTypedExtraSuite {
  val Name        = "foo"
  val MissingName = "missing"
}

/** Adds tests for value extras to test the use of defaults. */
abstract class AbstractValTypedExtraSuite[A <: AnyVal]
    extends AbstractTypedExtraSuite[A] {
  /** Generates an extra that uses a non-default value for an extra. */
  protected def overrideExtra(name: String): TypedExtra[A] with DefaultValue[A]

  /** Tests that `EnhancedIntent.getExtra(TypedExtra[A])` uses the correct
    * default. */
  override def testGetExtra() {
    intent.getExtra(extra)
    verifyGetExtra(extra.asInstanceOf[DefaultValue[A]].default)
  }

  /** Verifies that the correct arguments to `Intent.getXxxExtra` where
    * used.
    *
    * @param default the expected default value */
  protected def verifyGetExtra(default: A)

  /** Tests that `EnhancedIntent.getExtra(TypedExtra[A])` uses the correct
    * default when it has been overridden. */
  def testGetExtraWithDefault() {
    val testExtra = overrideExtra(Name)
    require(testExtra.default != extra.asInstanceOf[DefaultValue[A]].default)
    intent.getExtra(testExtra)
    verifyGetExtra(testExtra.default)
  }
}

/** Tests the `BooleanExtra` class. */
class BooleanExtraSuite extends AbstractValTypedExtraSuite[Boolean] {
  val value = true

  def overrideExtra(name: String) = BooleanExtra(name, true)

  when(intent.getBooleanExtra(same(Name), anyBoolean())).thenReturn(value)

  def createExtra(name: String) = BooleanExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Boolean) {
    verify(intent).getBooleanExtra(Name, default)
  }
}

/** Tests the `BooleanArrayExtra` class. */
class BooleanArrayExtraSuite extends AbstractTypedExtraSuite[Array[Boolean]] {
  val value = Array(true, false, false, true)

  when(intent.getBooleanArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = BooleanArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `BundleExtra` class. */
class BundleExtraSuite extends AbstractTypedExtraSuite[Bundle] {
  val value: Bundle = null

  when(intent.getBundleExtra(Name)).thenReturn(value)

  def createExtra(name: String) = BundleExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `ByteExtra` class. */
class ByteExtraSuite extends AbstractValTypedExtraSuite[Byte] {
  val value = 10.toByte

  def overrideExtra(name: String) = ByteExtra(name, 5.toByte)

  when(intent.getByteExtra(same(Name), anyByte())).thenReturn(value)

  def createExtra(name: String) = ByteExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Byte) {verify(intent).getByteExtra(Name, default)}
}

/** Tests the `ByteArrayExtra` class. */
class ByteArrayExtraSuite extends AbstractTypedExtraSuite[Array[Byte]] {
  val value = Array[Byte](0, 1, 2, 3, 4, 5, 6)

  when(intent.getByteArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = ByteArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `CharExtra` class. */
class CharExtraSuite extends AbstractValTypedExtraSuite[Char] {
  val value = 'z'

  def overrideExtra(name: String) = CharExtra(name, 'f')

  when(intent.getCharExtra(same(Name), anyChar())).thenReturn(value)

  def createExtra(name: String) = CharExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Char) {verify(intent).getCharExtra(Name, default)}
}

/** Tests the `CharArrayExtra` class. */
class CharArrayExtraSuite extends AbstractTypedExtraSuite[Array[Char]] {
  val value = Array('S', 'c', 'a', 'l', 'a')

  when(intent.getCharArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = CharArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `CharSequenceExtra` class. */
class CharSequenceExtraSuite extends AbstractTypedExtraSuite[CharSequence] {
  val value: CharSequence = "Scala"

  when(intent.getCharSequenceExtra(Name)).thenReturn(value)

  def createExtra(name: String) = CharSequenceExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `DoubleExtra` class. */
class DoubleExtraSuite extends AbstractValTypedExtraSuite[Double] {
  val value = 5.1

  def overrideExtra(name: String) = DoubleExtra(name, 5.4)

  when(intent.getDoubleExtra(same(Name), anyDouble())).thenReturn(value)

  def createExtra(name: String) = DoubleExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Double) {
    verify(intent).getDoubleExtra(
      Name,
      default)
  }
}

/** Tests the `DoubleArrayExtra` class. */
class DoubleArrayExtraSuite extends AbstractTypedExtraSuite[Array[Double]] {
  val value = Array(3.1415, 42.0, 1337.0)

  when(intent.getDoubleArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = DoubleArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `FloatExtra` class. */
class FloatExtraSuite extends AbstractValTypedExtraSuite[Float] {
  val value = 42.0f

  def overrideExtra(name: String) = FloatExtra(name, 3.14159f)

  when(intent.getFloatExtra(same(Name), anyFloat())).thenReturn(value)

  def createExtra(name: String) = FloatExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Float) {
    verify(intent).getFloatExtra(
      Name,
      default)
  }
}

/** Tests the `FloatArrayExtra` class. */
class FloatArrayExtraSuite extends AbstractTypedExtraSuite[Array[Float]] {
  val value = Array(3.1415f, 42.0f, 1337.0f)

  when(intent.getFloatArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = FloatArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `IntExtra` class. */
class IntExtraSuite extends AbstractValTypedExtraSuite[Int] {
  val value = 42

  def overrideExtra(name: String) = IntExtra(name, 1337)

  when(intent.getIntExtra(same(Name), anyInt())).thenReturn(value)

  def createExtra(name: String) = IntExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Int) {verify(intent).getIntExtra(Name, default)}
}

/** Tests the `IntArrayExtra` class. */
class IntArrayExtraSuite extends AbstractTypedExtraSuite[Array[Int]] {
  val value = Array(42, 1337)

  when(intent.getIntArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = IntArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `IntegerArrayListExtra` class. */
class IntegerArrayListExtraSuite
    extends AbstractTypedExtraSuite[ArrayList[java.lang.Integer]] {
  val value = new ArrayList[java.lang.Integer] {
    add(42)
    add(1337)
  }

  when(intent.getIntegerArrayListExtra(Name)).thenReturn(value)

  def createExtra(name: String) = IntegerArrayListExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `LongExtra` class. */
class LongExtraSuite extends AbstractValTypedExtraSuite[Long] {
  val value = 42L

  def overrideExtra(name: String) = LongExtra(name, 1337L)

  when(intent.getLongExtra(same(Name), anyLong())).thenReturn(value)

  def createExtra(name: String) = LongExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Long) {verify(intent).getLongExtra(Name, default)}
}

/** Tests the `LongArrayExtra` class. */
class LongArrayExtraSuite extends AbstractTypedExtraSuite[Array[Long]] {
  val value = Array(42L, 1337L)

  when(intent.getLongArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = LongArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `ParcelableExtra` class. */
class ParcelableExtraSuite extends AbstractTypedExtraSuite[DummyParcelable] {
  val value = new DummyParcelable

  when(intent.getParcelableExtra(Name)).thenReturn(value)

  def createExtra(name: String) = ParcelableExtra[DummyParcelable](name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `ParcelableArrayExtra` class. */
class ParcelableArrayExtraSuite
    extends AbstractTypedExtraSuite[Array[Parcelable]] {
  val value = Array[Parcelable](new DummyParcelable, new DummyParcelable)

  when(intent.getParcelableArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = ParcelableArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `ParcelableArrayListExtra` class. */
class ParcelableArrayListExtraSuite
    extends AbstractTypedExtraSuite[ArrayList[DummyParcelable]] {
  val value = new ArrayList[DummyParcelable] {
    add(new DummyParcelable)
    add(new DummyParcelable)
  }

  when[ArrayList[DummyParcelable]](intent.getParcelableArrayListExtra(Name))
      .thenReturn(value)

  def createExtra(name: String) = ParcelableArrayListExtra[DummyParcelable](name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `SerializableExtra` class. */
class SerializableExtraSuite
    extends AbstractTypedExtraSuite[DummySerializable] {
  val value = new DummySerializable

  when(intent.getSerializableExtra(Name)).thenReturn(value,
    Array[Serializable](): _*)

  def createExtra(name: String) = SerializableExtra[DummySerializable](name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `ShortExtra` class. */
class ShortExtraSuite extends AbstractValTypedExtraSuite[Short] {
  val value = 42.toShort

  def overrideExtra(name: String) = ShortExtra(name, 1337.toShort)

  when(intent.getShortExtra(same(Name), anyShort())).thenReturn(value)

  def createExtra(name: String) = ShortExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}

  def verifyGetExtra(default: Short) {
    verify(intent).getShortExtra(
      Name,
      default)
  }
}

/** Tests the `ShortArrayExtra` class. */
class ShortArrayExtraSuite extends AbstractTypedExtraSuite[Array[Short]] {
  val value = Array[Short](42, 1337)

  when(intent.getShortArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = ShortArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `StringExtra` class. */
class StringExtraSuite extends AbstractTypedExtraSuite[String] {
  val value = "Scala"

  when(intent.getStringExtra(Name)).thenReturn(value)

  def createExtra(name: String) = StringExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `StringArrayExtra` class. */
class StringArrayExtraSuite extends AbstractTypedExtraSuite[Array[String]] {
  val value = Array("Scala", "Baitha")

  when(intent.getStringArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = StringArrayExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `StringArrayListExtra` class. */
class StringArrayListExtraSuite
    extends AbstractTypedExtraSuite[ArrayList[String]] {
  val value = new ArrayList[String] {
    add("Scala")
    add("Baitha")
  }

  when(intent.getStringArrayListExtra(Name)).thenReturn(value)

  def createExtra(name: String) = StringArrayListExtra(name)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** A dummy serializable implementation. */
class DummySerializable extends Serializable

/** A fake SdkVersions that says the current SDK version is Eclair. */
object EclairSdkVersions extends SdkVersions {
  override protected lazy val currentSdkVersion = SdkVersions.EclairMR1
}

/** A fake SdkVersions that says the current SDK version is FroYo. */
object FroYoSdkVersions extends SdkVersions {
  override protected lazy val currentSdkVersion = SdkVersions.FroYo
}

/** Tests the `CharSequenceArrayExtra` class for the FroYo API level. */
class FroyoCharSequenceArrayExtraSuite
    extends AbstractTypedExtraSuite[Array[CharSequence]] {
  val value = Array[CharSequence]("Scala", "Baitha")

  when(intent.getCharSequenceArrayExtra(Name)).thenReturn(value)

  def createExtra(name: String) = CharSequenceArrayExtra(name)(FroYoSdkVersions)

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}

/** Tests the `CharSequenceArrayListExtra` class for the FroYo API level. */
class FroyoCharSequenceArrayListExtraSuite
    extends AbstractTypedExtraSuite[ArrayList[CharSequence]] {
  val value = new ArrayList[CharSequence]() {
    add("Scala")
    add("Baitha")
  }

  when(intent.getCharSequenceArrayListExtra(Name)).thenReturn(value)

  def createExtra(name: String) = {
    CharSequenceArrayListExtra(name)(FroYoSdkVersions)
  }

  def verifyPutExtra() {verify(intent).putExtra(Name, value)}
}
