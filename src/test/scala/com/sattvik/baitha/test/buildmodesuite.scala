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

import com.sattvik.baitha._
import org.scalatest.Suite

/** Defines the tests for the BuildMode traits.
  *
  * @author Daniel Solano Gómez */
abstract class BuildModeSuite extends Suite with BuildMode {
  protected def isDebugEnabled = {
    var enabled = false
    whenDebug {
      enabled = true
    }
    enabled
  }

  def testWhenDebug()
}

/** Ensures the ProductionMode trait disables debug mode.
  *
  * @author Daniel Solano Gómez */
class ProductionModeSuite extends BuildModeSuite with ProductionMode {
  def testWhenDebug() {
    expectResult(false)(isDebugEnabled)
  }
}

/** Ensures the DebugMode trait enables debug mode.
  *
  * @author Daniel Solano Gómez */
class DebugModeSuite extends BuildModeSuite with DebugMode {
  def testWhenDebug() {
    expectResult(true)(isDebugEnabled)
  }
}
