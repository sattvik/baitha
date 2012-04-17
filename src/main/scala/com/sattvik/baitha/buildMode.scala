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
package com.sattvik.baitha

/** Trait for conditionally executing code based on a debug flag.
  *
  * @author Daniel Solano Gómez */
trait BuildMode {
  /** A simple flag for whether or not debug mode is enabled. */
  def debugMode: scala.Boolean

  /** Executes argument only if debugging is enabled.
    *
    * For example:
    * {{{
    * withDebug {
    *   // do something when debug is enabled
    * }
    * }}} */
  def whenDebug(body: => Unit)

  /** If supported, enables strict mode for the current thread and VM. */
  def enableStrictMode()

  /** If supported, disables strict mode for the current thread and VM. */
  def disableStrictMode()
}

/** The debug version of BuildMode where methods actually do something.
  *
  * @author Daniel Solano Gómez */
trait DebugMode extends BuildMode {
  /** Hard code debug mode to true. */
  final override val debugMode = true

  /** Executes the body. */
  final def whenDebug(body: => Unit) {body}

  /** Enables strict mode, if available. */
  final def enableStrictMode() {
    DebugMode.STRICT_MODE_HELPER.enableStrictMode()
  }

  /** Disables strict mode, if available. */
  final def disableStrictMode() {
    DebugMode.STRICT_MODE_HELPER.disableStrictMode()
  }
}

/** Companion object to DebugMode that helps with strict mode.
  *
  * @author Daniel Solano Gómez */
object DebugMode {
  /** Helper object that handles strict mode, depending on which Android
    * version is running. */
  val STRICT_MODE_HELPER =
    if (SdkVersions.currentSdkSince(SdkVersions.Gingerbread))
      GingerbreadStrictModeHelper
    else
      PreGingerbreadStrictModeHelper

  /** Interface for the strict mode helper. */
  sealed trait StrictModeHelper {
    def enableStrictMode()
    def disableStrictMode()
  }

  /** The Gingerbread strict mode helper enables detection of all suspect
    *  behaviours and logs them. */
  object GingerbreadStrictModeHelper extends StrictModeHelper {
    import android.os.StrictMode

    def enableStrictMode() {
      StrictMode.setThreadPolicy(
        new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
      StrictMode.setVmPolicy(
        new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
    }

    def disableStrictMode() {
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
      StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
    }
  }

  /** The pre-Gingerbread helper does nothing. */
  object PreGingerbreadStrictModeHelper extends StrictModeHelper {
    def enableStrictMode() {}

    def disableStrictMode() {}
  }
}

/** The production version of BuildMode; does nothing.
  *
  * @author Daniel Solano Gómez */
trait ProductionMode extends BuildMode {
  /** Hard code debug mode to false. */
  final override val debugMode = false

  /** Does nothing. */
  final def whenDebug(body: => Unit) {}

  final def enableStrictMode() {}

  final def disableStrictMode() {}
}

