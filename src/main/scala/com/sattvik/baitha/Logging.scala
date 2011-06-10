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

import android.util.Log

/** Trait with convenience methods for writing log messages.  Whether or not
  * logging takes place is determined by the Debug trait.  Classes that
  * implement this trait must set a log tag.
  *
  * @author Daniel Solano Gómez */
trait Logging extends BuildMode {
  import Logging.wtfLogger

  /** The tag to use for log messages. */
  protected def tag: String

  /** Send a DEBUG log message. */
  protected def d(message: String) {
    whenDebug(Log.d(tag, message))
  }

  /** Send a DEBUG log message and log the exception. */
  protected def d(message: String, throwable: Throwable) {
    whenDebug(Log.d(tag, message, throwable))
  }

  /** Send a DEBUG log message and log the exception. */
  protected def d(throwable: Throwable) {
    whenDebug(Log.d(tag, Log.getStackTraceString(throwable)))
  }

  /** Send an ERROR log message. */
  protected def e(message: String) {
    whenDebug(Log.e(tag, message))
  }

  /** Send an ERROR log message and log the exception. */
  protected def e(message: String, throwable: Throwable) {
    whenDebug(Log.e(tag, message, throwable))
  }

  /** Send an ERROR log message and log the exception. */
  protected def e(throwable: Throwable) {
    whenDebug(Log.e(tag, Log.getStackTraceString(throwable)))
  }

  /** Send an INFO log message. */
  protected def i(message: String) {
    whenDebug(Log.i(tag, message))
  }

  /** Send an INFO log message and log the exception. */
  protected def i(message: String, throwable: Throwable) {
    whenDebug(Log.i(tag, message, throwable))
  }

  /** Send an INFO log message and log the exception. */
  protected def i(throwable: Throwable) {
    whenDebug(Log.i(tag, Log.getStackTraceString(throwable)))
  }

  /** Send a VERBOSE log message. */
  protected def v(message: String) {
    whenDebug(Log.v(tag, message))
  }

  /** Send a VERBOSE log message and log the exception. */
  protected def v(message: String, throwable: Throwable) {
    whenDebug(Log.v(tag, message, throwable))
  }

  /** Send a VERBOSE log message and log the exception. */
  protected def v(throwable: Throwable) {
    whenDebug(Log.v(tag, Log.getStackTraceString(throwable)))
  }

  /** Send a WARN log message. */
  protected def w(message: String) {
    whenDebug(Log.w(tag, message))
  }

  /** Send a WARN log message and log the exception. */
  protected def w(message: String, throwable: Throwable) {
    whenDebug(Log.w(tag, message, throwable))
  }

  /** Send a WARN log message and log the exception. */
  protected def w(throwable: Throwable) {
    whenDebug(Log.w(tag, throwable))
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * The error will always be logged at level ASSERT with the call stack.
    * Depending on system configuration, a report may be added to the
    * DropBoxManager and/or the process may be terminated immediately with an
    * error dialog. */
  protected def wtf(message: String) {
    whenDebug(wtfLogger.wtf(tag, message))
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * Similar to wtf(Throwable), but with an exception as well. */
  protected def wtf(message: String, throwable: Throwable) {
    whenDebug(wtfLogger.wtf(tag, message, throwable))
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * Similar to wtf(String), but with an exception to log. */
  protected def wtf(throwable: Throwable) {
    whenDebug(wtfLogger.wtf(tag, throwable))
  }
}

/** Companion object that adds support for WTF on pre-FroYo platforms.
  *
  * @author Daniel Solano Gómez */
private object Logging {

  import com.sattvik.baitha.SdkVersions._

  private val wtfLogger =
    if (currentSdkSince(FroYo)) {
      FroYoWtfLogger
    } else {
      PreFroYoWtfLogger
    }

  /** The interface for a logger to support the What a Terrible Failure
    * report. */
  private abstract class WtfLogger {
    def wtf(tag: String, message: String)

    def wtf(tag: String, message: String, throwable: Throwable)

    def wtf(tag: String, throwable: Throwable)
  }

  /** The logger that actually uses the wtf log messages available since
    * FroYo. */
  private object FroYoWtfLogger extends WtfLogger {
    def wtf(tag: String, message: String) {
      Log.wtf(tag, message)
    }

    def wtf(tag: String, message: String, throwable: Throwable) {
      Log.wtf(tag, message, throwable)
    }

    def wtf(tag: String, throwable: Throwable) {
      Log.wtf(tag, throwable)
    }
  }

  /** The logger that uses the ASSERT log level for wtf messages.  This is for
    * platforms that don't support the wtf log method. */
  private object PreFroYoWtfLogger extends WtfLogger {
    class TerribleFailure(message: String, cause: Throwable)
        extends Exception(message, cause)
    def wtf(tag: String, message: String) {
      wtf(tag, message, null)
    }

    def wtf(tag: String, message: String, throwable: Throwable) {
      val ex = new TerribleFailure(message, throwable)
      Log.println(Log.ASSERT, tag, Log.getStackTraceString(ex))
    }

    def wtf(tag: String, throwable: Throwable) {
      wtf(tag, "", null)
    }
  }
}
