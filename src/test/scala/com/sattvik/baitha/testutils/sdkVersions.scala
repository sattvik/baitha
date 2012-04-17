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
package com.sattvik.baitha.testutils

import com.sattvik.baitha.SdkVersions

/** A version of the `SdkVersions` injection hard-coded to API level 7. */
trait EclairMR1SdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.EclairMR1
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 8. */
trait FroYoSdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.FroYo
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 9. */
trait GingerbreadSdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.Gingerbread
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 10. */
trait GingerbreadMR1SdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.GingerbreadMR1
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 11. */
trait HoneycombSdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.Honeycomb
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 12. */
trait HoneycombMR1SdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.HoneycombMR1
  }
}

/** A version of the `SdkVersions` injection hard-coded to API level 13. */
trait HoneycombMR2SdkVersions extends SdkVersions.Injected  {
  def sdkVersions = new SdkVersions {
    override protected lazy val currentSdkVersion = SdkVersions.HoneycombMR2
  }
}

