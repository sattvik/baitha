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
package com.sattvik.baitha.database

/** A typed column is meant to be used with the `TypedCursorWrapper` in order
  * to provide easier access to the contents of a cursor.  Compare the
  * following two examples:
  *
  * {{{
  * // using standard cursor
  * val myInt = cursor.getInt(cursor.getColumnIndexOrThrow("foo"))
  *
  * // using typed column
  * val fooColumn = TypedColumn[Int]("foo")
  * val myInt = cursor.getOrThrow(fooColumn)
  * }}}
  *
  * @tparam A the type for the column.  Valid values include `Int`, `Long`,
  * `Short`, `Float`, `Double`, `String`, and `Array[Byte]`
  *
  * @author Daniel Solano Gómez */
case class TypedColumn[A](name: String) {
  override def toString = name
}
