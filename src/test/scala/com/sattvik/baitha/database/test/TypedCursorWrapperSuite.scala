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
import android.content.ContentResolver
import android.database._
import android.net.Uri
import android.os.Bundle
import org.scalatest.{Suite, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar

import com.sattvik.baitha.database.{TypedColumn, TypedCursorWrapper}
import com.sattvik.baitha.testutils.{EclairMR1SdkVersions, HoneycombSdkVersions}

import org.mockito.Mockito.{verify, when}
import TypedCursorWrapperSuite._

/** Tests for the `TypedCursorWrapper`.  These must be run on an actual
  *  emulator or device instance as `TypedCursorWrapper` extends from the
  *  Android class `CursorWrapper`.
  */
class TypedCursorWrapperSuite
    extends Suite with MockitoSugar with OneInstancePerTest {
  /** The cursor to be wrapped. */
  val mockCursor = mock[Cursor]
  when(mockCursor.getColumnIndex(Column)).thenReturn(ColumnIndex)
  when(mockCursor.getColumnIndex(NullColumn)).thenReturn(NullColumnIndex)
  when(mockCursor.getColumnIndex(MissingColumn)).thenReturn(-1)
  when(mockCursor.getColumnIndexOrThrow(Column)).thenReturn(ColumnIndex)
  when(mockCursor.getColumnIndexOrThrow(NullColumn)).thenReturn(NullColumnIndex)
  when(mockCursor.getColumnIndexOrThrow(MissingColumn)).thenThrow(
    new IllegalArgumentException)
  when(mockCursor.getBlob(ColumnIndex)).thenReturn(BlobData)
  when(mockCursor.getDouble(ColumnIndex)).thenReturn(DoubleData)
  when(mockCursor.getFloat(ColumnIndex)).thenReturn(FloatData)
  when(mockCursor.getInt(ColumnIndex)).thenReturn(IntData)
  when(mockCursor.getLong(ColumnIndex)).thenReturn(LongData)
  when(mockCursor.getShort(ColumnIndex)).thenReturn(ShortData)
  when(mockCursor.getString(ColumnIndex)).thenReturn(StringData)
  when(mockCursor.isNull(NullColumnIndex)).thenReturn(true)

  /** The cursor to test. */
  val cursor = new TypedCursorWrapper(mockCursor) with HoneycombSdkVersions

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

  def testIsNull() {
    expect(false)(cursor.isNull(TypedColumn[Int](Column)))
  }

  def testIsNullWhenNull() {
    expect(true)(cursor.isNull(TypedColumn[Int](NullColumn)))
  }

  def testGetBlob() {
    expect(BlobData) {
      cursor.get(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetOptionBlob() {
    expect(Some(BlobData)) {
      cursor.getOption(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetOptionNullBlob() {
    expect(None) {
      cursor.getOption(TypedColumn[Array[Byte]](NullColumn))
    }
  }

  def testGetOrThrowBlob() {
    expect(BlobData) {
      cursor.getOrThrow(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetOrThrowOptionBlob() {
    expect(Some(BlobData)) {
      cursor.getOrThrowOption(TypedColumn[Array[Byte]](Column))
    }
  }

  def testGetOrThrowOptionNullBlob() {
    expect(None) {
      cursor.getOrThrowOption(TypedColumn[Array[Byte]](NullColumn))
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

  def testClose() {
    cursor.close()
    verify(mockCursor).close()
  }

  def testCopyStringToBuffer() {
    val buffer: CharArrayBuffer = null
    cursor.copyStringToBuffer(1, buffer)
    verify(mockCursor).copyStringToBuffer(1, buffer)
  }

  def testDeactivate() {
    cursor.deactivate()
    verify(mockCursor).deactivate()
  }

  def testGetBlobIndex() {expect(BlobData)(cursor.getBlob(ColumnIndex))}

  def testGetColumnCount() {
    cursor.getColumnCount
    verify(mockCursor).getColumnCount
  }

  def testGetColumnName() {
    cursor.getColumnName(0)
    verify(mockCursor).getColumnName(0)
  }

  def testGetColumnNames() {
    cursor.getColumnNames
    verify(mockCursor).getColumnNames
  }

  def testGetCount() {
    cursor.getCount
    verify(mockCursor).getCount
  }

  def testGetDoubleIndex() {expect(DoubleData)(cursor.getDouble(ColumnIndex))}

  def testGetExtras() {
    cursor.getExtras
    verify(mockCursor).getExtras
  }

  def testGetFloatIndex() {expect(FloatData)(cursor.getFloat(ColumnIndex))}

  def testGetIntIndex() {expect(IntData)(cursor.getInt(ColumnIndex))}

  def testGetLongIndex() {expect(LongData)(cursor.getLong(ColumnIndex))}

  def testGetPosition() {
    cursor.getPosition
    verify(mockCursor).getPosition
  }

  def testGetShortIndex() {expect(ShortData)(cursor.getShort(ColumnIndex))}

  def testGetStringIndex() {expect(ShortData)(cursor.getShort(ColumnIndex))}

  def testGetTypePreHoneycomb() {
    intercept[UnsupportedOperationException] {
      val cursor = new TypedCursorWrapper(mockCursor) with EclairMR1SdkVersions
      cursor.getType(ColumnIndex)
    }
  }

  def testGetTypeHoneycomb() {
    cursor.getType(ColumnIndex)
    verify(mockCursor).getType(ColumnIndex)
  }

  def testGetWantsAllOnMoveCalls() {
    cursor.getWantsAllOnMoveCalls
    verify(mockCursor).getWantsAllOnMoveCalls
  }

  def testIsBeforeFirst() {
    cursor.isBeforeFirst
    verify(mockCursor).isBeforeFirst
  }

  def testIsClosed() {
    cursor.isClosed
    verify(mockCursor).isClosed
  }

  def testIsFirst() {
    cursor.isFirst
    verify(mockCursor).isFirst
  }

  def testIsLast() {
    cursor.isLast
    verify(mockCursor).isLast
  }

  def testIsNullIndex() {
    cursor.isNull(ColumnIndex)
    verify(mockCursor).isNull(ColumnIndex)
  }

  def testMove() {
    cursor.move(3)
    verify(mockCursor).move(3)
  }

  def testMoveToFirst() {
    cursor.moveToFirst()
    verify(mockCursor).moveToFirst()
  }

  def testMoveToLast() {
    cursor.moveToLast()
    verify(mockCursor).moveToLast()
  }

  def testMoveToNext() {
    cursor.moveToNext()
    verify(mockCursor).moveToNext()
  }

  def testMoveToPosition() {
    cursor.moveToPosition(3)
    verify(mockCursor).moveToPosition(3)
  }

  def testMoveToPrevious() {
    cursor.moveToPrevious()
    verify(mockCursor).moveToPrevious()
  }

  def testRegisterContentObserver() {
    val observer = mock[ContentObserver]
    cursor.registerContentObserver(observer)
    verify(mockCursor).registerContentObserver(observer)
  }

  def testRegisterDataSetObserver() {
    val observer = mock[DataSetObserver]
    cursor.registerDataSetObserver(observer)
    verify(mockCursor).registerDataSetObserver(observer)
  }

  def testRequery() {
    cursor.requery()
    verify(mockCursor).requery()
  }

  def testRespond() {
    val bundle: Bundle = null
    cursor.respond(bundle)
    verify(mockCursor).respond(bundle)
  }

  def testSetNotificationUri() {
    val resolver = mock[ContentResolver]
    val uri = mock[Uri]
    cursor.setNotificationUri(resolver, uri)
    verify(mockCursor).setNotificationUri(resolver, uri)
  }

  def testUnregisterContentObserver() {
    val observer = mock[ContentObserver]
    cursor.unregisterContentObserver(observer)
    verify(mockCursor).unregisterContentObserver(observer)
  }

  def testUnregisterDataSetObserver() {
    val observer = mock[DataSetObserver]
    cursor.unregisterDataSetObserver(observer)
    verify(mockCursor).unregisterDataSetObserver(observer)
  }
}

object TypedCursorWrapperSuite {
  /** The name of the column. */
  val Column = "exists"
  /** The index of `Column`. */
  val ColumnIndex = 2
  /** The name of a missing column. */
  val MissingColumn = "missing"
  /** The name of a column with a null value. */
  val NullColumn = "null"
  /** The index of `Column`. */
  val NullColumnIndex = 42

  val BlobData   = Array[Byte](0, 1, 2, 3)
  val DoubleData = 4.0
  val FloatData  = 5.0f
  val IntData    = 6
  val LongData   = 7L
  val ShortData  = 8.toShort
  val StringData = "9"
}