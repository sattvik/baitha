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

/** An abstraction of a query on the Android platform.  Instances of this
  * class are immutable and may be freely shared.
  *
  * @constructor The private copy constructor.
  *
  * @tparam A the type for the source.  Currently only `Uri` is supported via
  * `Resolver`
  *
  * @param source the source for the query.  This may be the URI of a content
  * provider.
  * @param columns the columns which should be retrieved.  If `None`,
  * all columns will be retrieved.
  * @param where an optional clause that determines which rows will be
  * returned.  If missing, then all rows will be returned.
  * @param sort an optional clause describing how to sort the rows.
  *
  * @author Daniel Solano Gómez */
class Query[A] private(
  val source: A,
  val columns: Option[Array[String]],
  val where: Option[String],
  val whereArgs: Option[Array[String]],
  val sort: Option[String]
) {

  /** Creates a new query for the given source. */
  def this(source: A) = this (source, None, None, None, None)

  /** Returns a version of the query that retrieves the given columns. */
  def withColumns(columns: TypedColumn[_]*): Query[A] = withColumns(columns)

  /** Returns a version of the query that retrieves the given columns. */
  def withColumns(columns: Traversable[_]): Query[A] = {
    val columnArray = Some(columns map {a: Any => a.toString} toArray)
    new Query(source, columnArray, where, whereArgs, sort)
  }

  /** Returns a version of the query that retrieves the given columns. */
  def withColumns(columns: Array[String]): Query[A] = {
    new Query(source, Some(columns), where, whereArgs, sort)
  }

  /** Returns a version of the query that uses the given filter clause. */
  def where(clause: String): Query[A] = {
    new Query(source, columns, Some(clause), whereArgs, sort)
  }

  /** Returns a version of the query that uses the given sort clause. */
  def withSort(sort: String): Query[A] = {
    new Query(source, columns, where, whereArgs, Some(sort))
  }

  /** Returns a version of the query that retrieves the given columns. */
  def withArgs(args: Any*): Query[A] = {
    val argArray = Some(args map {_.toString} toArray)
    new Query(source, columns, where, argArray, sort)
  }

  /** Returns a version of the query that retrieves the given columns. */
  def withArgs(args: Array[String]): Query[A] = {
    new Query(source, columns, where, Some(args), sort)
  }
}

/** Provides a factory function to construct new queries.
  *
  * @author Daniel Solano Gómez */
object Query {
  /** Creates a new query for the given source. */
  def apply[A](source: A): Query[A] = new Query(source)
}