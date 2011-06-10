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

import android.os.Build

/** A utility object used to determine for creating code that can adapt to the
  * run-time version of the SDK.
  *
  * @author Daniel Solano Gómez */
object SdkVersions extends Enumeration with SdkVersions {
  type SdkVersion = Value

  /** The zero value occurs when running outside of a device or the emulator.
    *  Under normal conditions, it should not occur. */
  val NoVersion = Value(0)
  /** First version of Android, released October 2008. */
  val Base = Value(1)
  /** Android 1.1, released February 2009. */
  val Base1_1 = Value(2)
  /** Android 1.5, released May 2009. */
  val Cupcake = Value(3)
  /** Android 1.6, released September 2009. */
  val Donut = Value(4)
  /** Android 2.0, released November 2009. */
  val Eclair = Value(5)
  /** Android 2.0.1, released December 2009. */
  val Eclair0_1 = Value(6)
  /** Android 2.1, released January 2010. */
  val EclairMR1 = Value(7)
  /** Android 2.2, released June 2010. */
  val FroYo = Value(8)
  /** Android 2.3, released November 2010. */
  val Gingerbread = Value(9)
  /** Android 2.3.3, released February 2011. */
  val GingerbreadMR1 = Value(10)
  /** Android 3.0, released February 2011. */
  val Honeycomb = Value(11)
  /** Android 3.1, released May 2011. */
  val HoneycombMR1 = Value(12)
  /** Magic version number for a current development build. */
  val CurrentDevelopment = Value(10000)
}

/** A somewhat private trait that implements the logic for the SdkVersions
  * singleton object.  This is separated as a trait for the sake of testing.
  *
  * @author Daniel Solano Gómez */
private[baitha] trait SdkVersions {
  import SdkVersions._

  /** The current SDK version, initialised at run-time. */
  protected lazy val currentSdkVersion = SdkVersions(Build.VERSION.SDK_INT)

  /** Returns true if the current SDK version is less than the given SDK
    * version. */
  final def currentSdkBefore(version: SdkVersion) = currentSdkVersion < version

  /** Returns true if the current SDK version is greater than or equal to the
    * given SDK version. */
  final def currentSdkSince(version: SdkVersion) = currentSdkVersion >= version
}

