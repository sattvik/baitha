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
package com.sattvik.baitha.threading

import android.app.Activity
import android.os._
import java.util.concurrent._
import scala.language.implicitConversions

/** Adds a number of utility functions to facilitate interaction between UI
  * and background threads.
  *
  * @author Daniel Solano Gómez */
trait ThreadUtils {
  /** An Android handler to post things onto the UI thread. */
  protected def handler: Option[Handler]
  /** A Java executor service to manage background tasks. */
  protected def executorService: Option[ExecutorService]

  /** Facilitates conversions of blocks into Runnable lambdas. */
  private implicit def blockToRunnable(block: => Unit): Runnable = {
    new Runnable {
      def run() {
        block
      }
    }
  }

  /** Posts the given block so that it will run on the UI thread.  This
    * method is asynchronous.
    *
    * @param block a block of code to execute on the UI thread
    *
    * @return Returns true if the message was successfully placed in to the
    *         message queue. Returns false on failure, usually because the
    *         looper processing the message queue is exiting. */
  protected final def onUiThread(block: => Unit): Boolean = {
    handler map (_.post(block)) getOrElse (false)
  }

  /** Runs the given block of code off the UI thread.
    *
    * @param block a block of code to execute on a background thread
    *
    * @return an option containing a future representing pending completion of
    *         the task.  If empty, this means the task failed to be scheduled.
    */
  protected final def inBackground(block: => Unit): Option[Future[_]] = {
    try {
      executorService.map(_.submit(block))
    } catch {
      case _: RejectedExecutionException => None
    }
  }

  /** Runs the block of code off the UI thread as a future.  Use this when
    * you are interested in the result of the block.
    *
    * @param block a block of code to execute in a background thread
    *
    * @tparam T the type of value the block returns
    *
    * @return an option containing a future representing pending completion of
    *         the task.  If empty, this means the task failed to be scheduled.
    */
  protected final def asFuture[T](block: => T): Option[Future[T]] = {
    try {
      executorService.map(_.submit(new Callable[T] {
        def call() = block
      }))
    } catch {
      case _: RejectedExecutionException => None
    }
  }
}

/** An implementation of ThreadUtils designed to be mixed into an Activity.
  * This will create the handler and executor service in the Activity's
  * onCreate() and destroy them in the Activity’s onDestroy.
  *
  * @author Daniel Solano Gómez */
trait ActivityThreadUtils extends Activity with ThreadUtils {
  protected var handler        : Option[Handler]         = None
  protected var executorService: Option[ExecutorService] = None

  abstract override def onCreate(state: Bundle) {
    super.onCreate(state)
    handler = Some(new Handler(Looper.getMainLooper))
    executorService = Some(Executors.newCachedThreadPool())
  }

  abstract override def onDestroy() {
    handler = None
    executorService.get.shutdownNow()
    executorService = None
    super.onDestroy()
  }
}
