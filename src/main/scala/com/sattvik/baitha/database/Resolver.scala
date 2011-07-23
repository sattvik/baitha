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

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.support.v4.content.CursorLoader

/** A resolver is capable of actually executing queries.
  *
  * @author Daniel Solano Gómez */
class Resolver(context: Context) {
  /** Executes the query synchronously, returning a cursor to the result.
    *
    * @param query the query to execute
    * @param args arguments for the query's filter
    *
    * @return a cursor to the query results */
  def query(aQuery: Query[Uri], args: Any*): Cursor = {
    query(aQuery withArgs args)
  }

  /** Executes the query synchronously, returning a cursor to the result.
    *
    * @param query the query to execute
    *
    * @return a cursor to the query results */
  def query(query: Query[Uri]): Cursor = {
    import query._
    context.getContentResolver.query(
      source,
      columns.orNull,
      where.orNull,
      whereArgs.orNull,
      sort.orNull)
  }

  /** Returns a cursor loader that will execute the query.
    *
    * @param query the query to execute
    * @param args arguments for the query's filter
    *
    * @return a cursor loader that will execute the query. */
  def queryLater(query: Query[Uri], args: Any*): CursorLoader = {
    queryLater(query withArgs args)
  }

  /** Returns a cursor loader that will execute the query.
    *
    * @param query the query to execute
    *
    * @return a cursor loader that will execute the query. */
  def queryLater(query: Query[Uri]): CursorLoader = {
    import query._
    new CursorLoader(
      context,
      source,
      columns.orNull,
      where.orNull,
      whereArgs.orNull,
      sort.orNull)
  }
}
