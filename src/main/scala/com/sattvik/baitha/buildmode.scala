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
trait BuildMode {
  /** Executes argument only if debugging is enabled.
    *
    * For example:
    * {{{
    * withDebug {
    *   // do something when debug is enabled
    * }
    * }}} */
  def whenDebug(body: => Unit)
}

/** The debug version of BuildMode where methods actually do something.
  *
  * @author Daniel Solano Gómez */
trait DebugMode extends BuildMode {
  /** Executes the body. */
  final override def whenDebug(body: => Unit) {body}
}

/** The production version of BuildMode; does nothing.
  *
  * @author Daniel Solano Gómez */
trait ProductionMode extends BuildMode {
  /** Does nothing. */
  final override def whenDebug(body: => Unit) {}
}

