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
package com.sattvik.baitha

/** Trait for conditionally executing code based on a debug flag.
  *
  * @author Daniel Solano Gómez */
trait DebugMode {
  /** Executes argument only if debugging is enabled.
    *
    * For example:
    * {{{
    * withDebug {
    *   // do something when debug is enabled
    * }
    * }}} */
  protected def whenDebug(body: => Unit)
}

/** Debug version of DebugMode where methods actually do something.
  *
  * @author Daniel Solano Gómez */
trait Debug extends DebugMode {
  /** Executes the body. */
  protected final override def whenDebug(body: => Unit) {body}
}

/** Production version of DebugMode; does nothing.
  *
  * @author Daniel Solano Gómez */
trait Production extends DebugMode {
  /** Does nothing. */
  protected final override def whenDebug(body: => Unit) {}
}

