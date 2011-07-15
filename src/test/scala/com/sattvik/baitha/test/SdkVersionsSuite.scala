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

import com.sattvik.baitha.SdkVersions
import org.scalatest.Suite

/** Tests for the SdkVersions object.
  *
  * @author Daniel Solano Gómez */
class SdkVersionsSuite extends Suite {
  import SdkVersions._

  def testBefore() {
    val test = new SdkVersions {
      override lazy val currentSdkVersion = Gingerbread
    }

    expect(true) {
      SdkVersions.values.filter(_ > Gingerbread).forall(test currentSdkBefore _)
    }
    expect(true) {
      SdkVersions.values.filterNot(_ > Gingerbread).forall(!test.currentSdkBefore(_))
    }
  }

  def testSince() {
    val eclair = new SdkVersions {
      override lazy val currentSdkVersion = EclairMR1
    }

    expect(true) {
      SdkVersions.values.filter(_ <= EclairMR1).forall(eclair.currentSdkSince(_))
    }
    expect(true) {
      SdkVersions.values.filterNot(_ <= EclairMR1).forall(!eclair.currentSdkSince(_))
    }
  }

  def testObject() {
    expect(true)(currentSdkBefore(Donut))
    expect(true)(currentSdkSince(NoVersion))
  }

  /** Tests gracefully handling an unfamiliar API version. */
  def testUnknownVersion() {
    val unknown = new SdkVersions {
      override lazy val currentSdkVersion = SdkVersions.fromApiLevel(42)
    }
    expect(true)(unknown.currentSdkBefore(CurrentDevelopment))
    expect(true)(unknown.currentSdkSince(Donut))
  }
}
