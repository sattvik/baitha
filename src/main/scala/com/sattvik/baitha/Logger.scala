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

import Logger.{LogPrinter, LogConfig}

import SdkVersions._

/** An enhanced logging facility for Android inspired from the facility from
  * [[http://code.google.com/p/roboguice RoboGuice]].
  *
  * == Getting a logger ==
  *
  * The simplest way to get a logger is to simply use the
  * `Logger.createLogger` factory function as in:
  *
  * {{{
  * val log = Logger.createLogger("MyTag")
  * }}}
  *
  * If your application already makes use of a [[com.sattvik.baitha.BuildMode]]
  * instance, you can use it combination with a `DefaultLogger` as follows:
  *
  * {{{
  * trait MyBuildMode extends DebugMode
  *
  * val log = new DefaultLoggeR("MyTag") extends MyBuildMode
  * }}}
  *
  *
  * == Using a logger ==
  *
  * === Log levels ===
  *
  * The log levels supported by `Logger` in order of increasing severity are:
  *
  * <ol>
  *   <li>Verbose: `Logger.v(…)`</li>
  *   <li>Debug: `Logger.d(…)`</li>
  *   <li>Info: `Logger.i(…)`</li>
  *   <li>Warn: `Logger.w(…)`</li>
  *   <li>Error: `Logger.e(…)`</li>
  *   <li>What a Terrible Failure!: `Logger.wtf(…)`</li>
  * </ol>
  *
  * '''Note:''' The WTF level is treated specially.  On FroYo (Android 2.2)
  * and newer, it uses the native WTF mechanism.  On older version of Android,
  * it simply logs at the `ASSERT` level.
  *
  *
  * === Log arguments ===
  *
  * Generally, log messages may include a message, an exception, or both.  It
  * is also possible to use `String.format` style formatting.  Some examples
  * follow:
  *
  * {{{
  * Log.i("hello there")
  * Log.v("%s %s", "hello", "there")
  * Log.w(exception, "Error occurred, panic!")
  * Log.e(exception, "Another error occured, %s panic!", "please")
  * }}}
  *
  * '''Note:''' Unlike Android's Log mechanism, exceptions occur at the
  * beginning of the argument list.  Also, additional arguments to the log
  * message will not simply be appended, they must be included using a format
  * parameter.
  *
  * == Advanced configuration ==
  *
  * The Logger trait depends on two different other traits that can be used to
  * change the logging behaviour: `LogConfig` and `LogPrinter`.
  *
  * `LogConfig` is used to specify the logging 'scope', which can be
  * understood as the log source and corresponds to Android's log tag.  The
  * `LogConfig` also specifies the minimum log level that should be printed.
  *
  * A `LogPrinter` can modify the message before it is printed, and can even
  * be used to redirect logging to some place other than the Android log
  * mechanism.
  *
  * @author Daniel Solano Gómez */
trait Logger {this: LogConfig with LogPrinter =>
  /** The handler for terrible failures.  On FroYo and beyond, this defers to
    * Android's mechanism.  On versions before FroYo, it is simply handled as
    * an ASSERT level log message. */
  private val wtfLogger = {
    if (currentSdkSince(FroYo)) {
      new FroYoWtfLogger
    }
    else {
      new PreFroYoWtfLogger
    }
  }

  /** Logs the exception at a VERBOSE level. */
  def v(throwable: Throwable) = logThrowable(Log.VERBOSE, throwable)

  /** Log the message at a VERBOSE level. */
  def v(message: AnyRef, args: Any*) = logMessage(Log.VERBOSE, message, args)

  /** Log the exception with a message at a VERBOSE level */
  def v(throwable: Throwable, message: AnyRef, args: Any*) = {
    logThrowableWithMessage(Log.VERBOSE, throwable, message, args)
  }

  /** Logs the exception at a DEBUG level. */
  def d(throwable: Throwable) = logThrowable(Log.DEBUG, throwable)

  /** Log the message at a DEBUG level. */
  def d(message: AnyRef, args: Any*) = logMessage(Log.DEBUG, message, args)

  /** Log the exception with a message at a DEBUG level */
  def d(throwable: Throwable, message: AnyRef, args: Any*) = {
    logThrowableWithMessage(Log.DEBUG, throwable, message, args)
  }

  /** Logs the exception at a INFO level. */
  def i(throwable: Throwable) = logThrowable(Log.INFO, throwable)

  /** Log the message at a INFO level. */
  def i(message: AnyRef, args: Any*) = logMessage(Log.INFO, message, args)

  /** Log the exception with a message at a INFO level */
  def i(throwable: Throwable, message: AnyRef, args: Any*) = {
    logThrowableWithMessage(Log.INFO, throwable, message, args)
  }

  /** Logs the exception at a WARN level. */
  def w(throwable: Throwable) = logThrowable(Log.WARN, throwable)

  /** Log the message at a WARN level. */
  def w(message: AnyRef, args: Any*) = logMessage(Log.WARN, message, args)

  /** Log the exception with a message at a WARN level */
  def w(throwable: Throwable, message: AnyRef, args: Any*) = {
    logThrowableWithMessage(Log.WARN, throwable, message, args)
  }

  /** Logs the exception at a ERROR level. */
  def e(throwable: Throwable) = logThrowable(Log.ERROR, throwable)

  /** Log the message at a ERROR level. */
  def e(message: AnyRef, args: Any*) = logMessage(Log.ERROR, message, args)

  /** Log the exception with a message at a ERROR level */
  def e(throwable: Throwable, message: AnyRef, args: Any*) = {
    logThrowableWithMessage(Log.ERROR, throwable, message, args)
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * The error will always be logged at level ASSERT with the call stack.
    * Depending on system configuration, a report may be added to the
    * DropBoxManager and/or the process may be terminated immediately with an
    * error dialog. */
  def wtf(message: AnyRef, args: Any*) {
    wtfLogger.wtf(null, messageString(message, args))
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * Similar to wtf(AnyRef, Any*), but with an exception as well. */
  def wtf(throwable: Throwable, message: AnyRef, args: Any*) = {
    wtfLogger.wtf(throwable, messageString(message, args))
  }

  /** What a Terrible Failure: Report a condition that should never happen.
    * Similar to wtf(AnyRef, Any*), but with an exception to log. */
  def wtf(throwable: Throwable) = {
    wtfLogger.wtf(
      throwable,
      if (throwable == null) "null" else throwable.getMessage)
  }

  /** If the given log level is loggable, perform the given action. */
  private def whenLoggable(logLevel: Int)(action: => Int) = {
    if (logLevel < config.minLogLevel) 0 else action
  }

  /** Logs an exception if the given log level is loggable. */
  private def logThrowable(logLevel: Int, throwable: Throwable) = {
    whenLoggable(logLevel) {
      printer.printLog(logLevel, Log.getStackTraceString(throwable))
    }
  }

  /** Logs a message if the given log level is loggable. */
  private def logMessage(logLevel: Int, message: AnyRef, args: Seq[Any]) = {
    whenLoggable(logLevel) {
      printer.printLog(logLevel, messageString(message, args))
    }
  }

  /** Creates a message by applying the arguments to the message in a fashion
    * similar to String.format(message, args). */
  private def messageString(message: AnyRef, args: Seq[Any]) = {
    val messageStr = if (message == null) "null" else message.toString
    if (args.isEmpty) messageStr else messageStr.format(args: _*)
  }

  /** Logs an exception with a message if the given log level is loggable. */
  private def logThrowableWithMessage(
    logLevel: Int,
    throwable: Throwable,
    message: AnyRef,
    args: Seq[Any]
  ) = {
    whenLoggable(logLevel) {
      printer.printLog(
        logLevel, messageString(message, args) + '\n' +
            Log.getStackTraceString(throwable))
    }
  }

  /** The interface for a logger to support the What a Terrible Failure
    * report. */
  private trait WtfLogger {
    def wtf(throwable: Throwable, message: String): Int
  }

  /** The logger that uses the ASSERT log level for wtf messages.  This is for
    * platforms that don't support the wtf log method. */
  private class PreFroYoWtfLogger extends WtfLogger {
    def wtf(throwable: Throwable, message: String) = {
      printer.printLog(Log.ASSERT, message)
    }
  }

  /** The logger that actually uses the wtf log messages available since
    * FroYo. */
  private class FroYoWtfLogger extends WtfLogger {
    def wtf(throwable: Throwable, message: String) = {
      Log.wtf(config.scope, message, throwable)
    }
  }
}

/** Companion object that defined the types for log printers and
  * configurations.  It also provides a mostly-configure default logger
  * class, along with a factory function to easily create new loggers with a
  * given name for a scope.
  *
  * @todo Add a context-driven configuration
  * @todo Create a fancy printer with extra debug information
  *
  * @author Daniel Solano Gómez */
object Logger {
  /** Creates a new default logger with the given name for a scope.
    *
    * @param name the name of the scope for the logger
    * @param debug if true, the logger operates in debug mode,
    * otherwise it operates in production mode. */
  def createLogger(name: String, debug: Boolean = false) = {
    if (debug) new DefaultLogger(name) with DebugMode
    else new DefaultLogger(name) with ProductionMode
  }

  /** The log printer is in charge of actually taking a log message that has
    * been processed by the logger and logging it. */
  trait LogPrinter {
    /** A reference to the printer for use within the logger. */
    val printer: Printer

    /** The interface for the printer. */
    trait Printer {
      /** Prints the given message at the given priority. */
      def printLog(priority: Int, message: String): Int
    }
  }

  /** The simple log printer does not do any further processing of the
    * message and simply passes it along to Android. */
  trait SimpleLogPrinter extends LogPrinter {this: LogConfig =>
    /** Simple printer that uses the configuration scope as the log tag. */
    class SimplePrinter extends Printer {
      override def printLog(priority: Int, message: String) = {
        Log.println(priority, config.scope, message)
      }
    }
  }

  /** A logger instance must have a log configuration which determines what
    * log levels are active and names the source of the log message. */
  trait LogConfig {
    /** Reference to the configuration, for use by the logger. */
    val config: Config

    /** Basic configuration parameters for the logger. */
    trait Config {
      /** The minimum log level that is currently loggable. */
      def minLogLevel: Int
      /** A string used to identify the source of the log message. */
      def scope: String
    }
  }

  /** Simple implementation of a log configuration that uses a given name and
    * depends on a BuildMode to determine the minimum log level.  If the current
    * build mode is Debug, all log messages will be printed.  If the current
    * log level is Release, only WARN and higher priority messages will be
    * logged. */
  trait NamedDebugLogConfig extends LogConfig with BuildMode {
    /** Actual implementation of the named debug log configuration.
      *
      * @constructor Creates a new configuration using the given name as the
      * scope. */
    class NamedDebugConfig(name: String) extends Config {
      /** Sets the minimum log level depending on the debug flag. */
      override val minLogLevel = if (debugMode) Log.VERBOSE else Log.WARN
      /** Sets the scope to the given name. */
      override val scope = name
    }
  }

  /** A logger configuration that uses a SimpleLogPrinter and a
    * NamedDebugLogConfig.   Note that this class is abstract as it does not
    * actually have the build mode set. */
  abstract class DefaultLogger(name: String)
      extends Logger with SimpleLogPrinter with NamedDebugLogConfig {
    val config  = new NamedDebugConfig(name)
    val printer = new SimplePrinter
  }

  /** An implementation of the logger that does nothing, useful for testing. */
  object NoOpLogger extends Logger with LogConfig with LogPrinter {
    val printer = new Printer {
      def printLog(priority: Int, message: String) = 0
    }
    val config  = new Config {
      def minLogLevel = Int.MaxValue

      def scope = null
    }
  }
}
