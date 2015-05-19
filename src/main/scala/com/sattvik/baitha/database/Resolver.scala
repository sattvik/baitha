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
    * @return a cursor to the query results or `None` */
  def query(aQuery: Query[Uri], args: Any*): Option[Cursor] = {
    query(aQuery withArgs args)
  }

  /** Executes the query synchronously, returning a cursor to the result.
    *
    * @param query the query to execute
    *
    * @return a cursor to the query results */
  def query(query: Query[Uri]): Option[Cursor] = {
    import query._
    val cursor = context.getContentResolver.query(
      source,
      columns.orNull,
      where.orNull,
      whereArgs.orNull,
      sort.orNull)
    if (cursor == null) None else Some(cursor)
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

/** Primarily serves as the container for the `Resolver.Injected` trait.
  *
  * @author Daniel Solano Gómez */
object Resolver {
  /** A trait useful for dependency injection using the cake pattern.  If you
    * have a class that needs use a resolver, you can add it as part of its
    * self type.  As a result, it can access a resolver by using `resolver`,
    * as in the following example:
    *
    * {{{
    * class MyResolverUser {
    *   this: Resolver.Injected =>
    *
    *   def resolveSomething() = {
    *     resolver.query(…)
    *   }
    * }
    * }}}
    *
    * As a result, whenever a new instance of `MyResolverUser` is needed,
    * the resolver needs to be provided.  For example, for production code this
    * may look like:
    *
    * {{{
    * class MyActivity extends Activity {
    *   trait MyResolver {
    *     val resolver = new Resolver(this)
    *   }
    *
    *   val resolverUser = new MyResolverUser with MyResolver
    * }
    * }}}
    */
  trait Injected {
    /** Gets a resolver that is suitable for use. */
    def resolver: Resolver
  }
}
