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
package com.sattvik.baitha.database

import android.database.{CursorWrapper, Cursor}

/** A cursor wrapper that provides a handy interface for getting values out
  * of using a `TypedColumn`.
  *
  * The most practical way to use this class is to declare the columns you
  * will be referencing as `TypedColumn` constants:
  *
  * {{{
  * object MyCursorUser {
  *   val MyIntColumn = TypedColumn[Int]("MyInt")
  *   val MyStringColumn = TypedColumn[String]("MyString")
  * }
  * }}}
  *
  * Then, once you have a cursor with these columns, you can easily get the
  * values without needing to cast or worrying about using the correct
  * `getXxx` method:
  *
  * {{{
  *   val cursor = …
  *   …
  *   val myInt = cursor.get(MyIntColumn)
  *   val myString = cursor.get(MyStringColumn)
  * }}}
  *
  * This class also provides `Option` variants of `get` and `getOrThrow` that
  * check to see if the column has a null value.
  *
  * Note that `TypedCursorWrapper` also implements Android's `Cursor`
  * interface, allowing you to use this wrapper as a complete replacement for
  * the underlying cursor.
  *
  * @author Daniel Solano Gómez */
class TypedCursorWrapper(cursor: Cursor) extends CursorWrapper(cursor) {
  /** Returns the zero-based index for the given column name, or -1 if the
    * column does not exist.  If you expect the column to exist use
    * `getColumnIndexOrThrow` instead, which will make the error more clear.
    *
    * @param column the target column
    *
    * @return the zero-based column index for the given column name, or -1 if
    * the column name does not exist. */
  def getColumnIndex(column: TypedColumn[_]): Int = {
    cursor.getColumnIndex(column.name)
  }

  /** Returns the zero-based index for the given column name, or -1 if the
    * column does not exist.  If you expect the column to exist use
    * `getColumnIndexOrThrow` instead, which will make the error more clear.
    *
    * @param column the target column
    *
    * @return the zero-based column index for the given column name, or -1 if
    * the column name does not exist. */
  def getColumnIndexOrThrow(column: TypedColumn[_]): Int = {
    cursor.getColumnIndexOrThrow(column.name)
  }

  /** Returns the value of the requested column.  This is functionally
    * equivalent the appropriate `getXxx` method on the raw cursor after
    * getting the index using `getColumnIndex`.  If you expect the column to
    * exist use `getOrThrow` instead,  which will make the error more clear.
    *
    * The result and whether this method throws an exception when the column
    * value is null, the column type is not of the right type, or the column
    * value is not representable as an instance fo the type value is
    * implementation-defined.
    *
    * @param column the column to target
    *
    * @return the value of the column, or `None` if the column is not in the
    * cursor */
  def get[A: Manifest](column: TypedColumn[A]): A = {
    get(manifest[A], getColumnIndex(column))
  }

  /** Returns the value of the requested column as an option.  This method is
    * like `get(TypedColumn[A])` with the exception that if the value in the
    * column is null, this method will return `None` instead of having
    * undefined behaviour.
    *
    * @param column the column to target
    *
    * @return the value of the column as an option and `None` if the column
    * has a null value */
  def getOption[A: Manifest](column: TypedColumn[A]): Option[A] = {
    getOption(manifest[A], getColumnIndex(column))
  }

  /** Returns the value of the requested column.  This is functionally
    * equivalent the appropriate `getXxx` method on the raw cursor after
    * getting the index using `getColumnIndexOrThrow`.
    *
    * The result and whether this method throws an exception when the column
    * value is null, the column type is not of the right type, or the column
    * value is not representable as an instance fo the type value is
    * implementation-defined.
    *
    * @param column the column to target
    *
    * @return the value of the column */
  def getOrThrow[A: Manifest](column: TypedColumn[A]): A = {
    get(manifest[A], getColumnIndexOrThrow(column))
  }

  /** Returns the value of the requested column as an option.  This method is
    * like `getOrThrow(TypedColumn[A])` with the exception that if the value in
    * the column is null, this method will return `None` instead of having
    * undefined behaviour.
    *
    * @param column the column to target
    *
    * @return the value of the column as an option and `None` if the column
    * has a null value */
  def getOrThrowOption[A: Manifest](column: TypedColumn[A]): Option[A] = {
    getOption(manifest[A], getColumnIndexOrThrow(column))
  }

  /** Returns the value of the requested column as the requested type.  The
    * result and whether this method throws an exception when the column
    * value is null, the column type is not of the right type, or the column
    * value is not representable as an instance fo the type value is
    * implementation-defined.
    *
    * @param manifest the manifest of the type of the value to get
    * @param index the zero-based index of the target column
    *
    * @return the value of the column as an instance of the type */
  private def get[A](manifest: Manifest[A], index: Int): A = {
    try {
      TypedCursorWrapper.Getters(manifest)(cursor, index).asInstanceOf[A]
    } catch {
      case _: NoSuchElementException => throw new IllegalArgumentException(
        "'%s' is not a supported TypedColumn type".format(manifest))
    }
  }

  /** Like `get[A](Manifest[A], Int)`, except that it wraps the result in an
    * Option.  If the value at the index is null, returns `None`.
    *
    * @param manifest the manifest of the type of the value to get
    * @param index the zero-based index of the target column
    *
    * @return the value of the column as an instance of the type in an option */
  private def getOption[A](manifest: Manifest[A], index: Int): Option[A] = {
    if (cursor.isNull(index)) None else Some(get(manifest, index))
  }
}

/** Provides a handy implicit conversion.
  *
  * @author Daniel Solano Gómez */
object TypedCursorWrapper {
  /** Provides an implicit conversion to wrap a cursor. */
  implicit def cursorToWrappedCursor(cursor: Cursor): TypedCursorWrapper = {
    new TypedCursorWrapper(cursor)
  }

  /** A map of types to the appropriate `getXxx` function. */
  private val Getters = Map[Manifest[_], (Cursor, Int) => Any](
    manifest[Array[Byte]] -> {(_: Cursor).getBlob(_)},
    manifest[Double] -> {(_: Cursor).getDouble(_)},
    manifest[Float] -> {(_: Cursor).getFloat(_)},
    manifest[Int] -> {(_: Cursor).getInt(_)},
    manifest[Long] -> {(_: Cursor).getLong(_)},
    manifest[Short] -> {(_: Cursor).getShort(_)},
    manifest[String] -> {(_: Cursor).getString(_)}
  )
}

