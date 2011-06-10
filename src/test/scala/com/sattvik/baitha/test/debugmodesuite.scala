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
package com.sattvik.baitha.test

import com.sattvik.baitha._
import org.scalatest.Suite

/** Defines the tests for the DebugMode traits.
  *
  * @author Daniel Solano Gómez */
abstract class DebugModeSuite extends Suite with DebugMode {
  protected def isDebugEnabled = {
    var enabled = false
    whenDebug {
      enabled = true
    }
    enabled
  }

  def testWhenDebug()
}

/** Ensures the Production trait disables debug mode.
  *
  * @author Daniel Solano Gómez */
class ProductionSuite extends DebugModeSuite with Production {
  def testWhenDebug() {
    expect(false)(isDebugEnabled)
  }
}

/** Ensures the Debug trait enables debug mode.
  *
  * @author Daniel Solano Gómez */
class DebugSuite extends DebugModeSuite with Debug {
  def testWhenDebug() {
    expect(true)(isDebugEnabled)
  }
}
