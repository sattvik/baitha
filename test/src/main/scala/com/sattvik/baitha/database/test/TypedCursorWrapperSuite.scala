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
package com.sattvik.baitha.database.test

import java.util.Calendar
import android.test.mock.MockCursor
import org.scalatest.junit.JUnit3Suite
import com.sattvik.baitha.database.{TypedColumn, TypedCursorWrapper}

import TypedCursorWrapperSuite._

/** Tests for the `TypedCursorWrapper`.  These must be run on an actual
  *  emulator or device instance as `TypedCursorWrapper` extends from the
  *  Android class `CursorWrapper`.
  */
class TypedCursorWrapperSuite extends JUnit3Suite {
  /** The cursor to be wrapped. */
  val mockCursor = new MockCursor {
    override def getColumnIndex(name: String): Int = {
      if (name == Column) ColumnIndex else -1
    }

    override def getColumnIndexOrThrow(name: String): Int = {
      if (name == Column) ColumnIndex else throw new IllegalArgumentException
    }

    override def getBlob(index: Int) = BlobData

    override def getDouble(index: Int) = DoubleData

    override def getFloat(index: Int) = FloatData

    override def getInt(index: Int) = IntData

    override def getLong(index: Int) = LongData

    override def getShort(index: Int) = ShortData

    override def getString(index: Int) = if(index > 0) StringData else null
  }

  /** The cursor to test. */
  val cursor = new TypedCursorWrapper(mockCursor)

  def testGetColumnIndex() {
    expect(ColumnIndex)(cursor.getColumnIndex(TypedColumn[Int](Column)))
  }

  def testGetColumnIndexOrThrow() {
    expect(ColumnIndex)(cursor.getColumnIndexOrThrow(TypedColumn[Int](Column)))
  }

  def testGetColumnIndexMissing() {
    expect(-1)(cursor.getColumnIndex(TypedColumn[Int](MissingColumn)))
  }

  def testGetColumnIndexOrThrowMissing() {
    intercept[IllegalArgumentException] {
      cursor.getColumnIndexOrThrow(TypedColumn[Int](MissingColumn))
    }
  }

  def testGetBlob() {
    expect(BlobData) {
      cursor.get(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetOrThrowBlob() {
    expect(BlobData) {
      cursor.getOrThrow(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetDouble() {
    expect(DoubleData) {
      cursor.get(TypedColumn[Double](Column))
    }
  }

  def testGetOrThrowDouble() {
    expect(DoubleData) {
      cursor.getOrThrow(TypedColumn[Double](Column))
    }
  }

  def testGetFloat() {
    expect(FloatData) {
      cursor.get(TypedColumn[Float](Column))
    }
  }

  def testGetOrThrowFloat() {
    expect(FloatData) {
      cursor.getOrThrow(TypedColumn[Float](Column))
    }
  }

  def testGetInt() {
    expect(IntData) {
      cursor.get(TypedColumn[Int](Column))
    }
  }

  def testGetOrThrowInt() {
    expect(IntData) {
      cursor.getOrThrow(TypedColumn[Int](Column))
    }
  }

  def testGetLong() {
    expect(LongData) {
      cursor.get(TypedColumn[Long](Column))
    }
  }

  def testGetOrThrowLong() {
    expect(LongData) {
      cursor.getOrThrow(TypedColumn[Long](Column))
    }
  }

  def testGetShort() {
    expect(ShortData) {
      cursor.get(TypedColumn[Short](Column))
    }
  }

  def testGetOrThrowShort() {
    expect(ShortData) {
      cursor.getOrThrow(TypedColumn[Short](Column))
    }
  }

  def testGetString() {
    expect(StringData) {
      cursor.get(TypedColumn[String](Column))
    }
  }

  def testGetOrThrowString() {
    expect(StringData) {
      cursor.getOrThrow(TypedColumn[String](Column))
    }
  }


    def testGetMissingColumn() {
      expect(null) {
        cursor.get(TypedColumn[String](MissingColumn))
      }
    }

  def testGetOrThrowMissingColumn() {
    intercept[IllegalArgumentException] {
      cursor.getOrThrow(TypedColumn[String](MissingColumn))
    }
  }

  def testGetUnsupportedType() {
    val ex = intercept[IllegalArgumentException] {
      cursor.get(TypedColumn[Calendar](Column))
    }
    ex.getMessage.contains("unsupported TypeColumn")
  }

  def testGetOrThrowUnsupportedType() {
    val ex = intercept[IllegalArgumentException] {
      cursor.getOrThrow(TypedColumn[Calendar](Column))
    }
    ex.getMessage.contains("unsupported TypeColumn")
  }
}

object TypedCursorWrapperSuite {
  /** The name of the column. */
  val Column = "exists"
  val ColumnIndex = 2
  /** The name of a missing column. */
  val MissingColumn = "missing"

  val BlobData   = Array[Byte](0, 1, 2, 3)
  val DoubleData = 4.0
  val FloatData  = 5.0f
  val IntData    = 6
  val LongData   = 7L
  val ShortData  = 8.toShort
  val StringData = "9"
}