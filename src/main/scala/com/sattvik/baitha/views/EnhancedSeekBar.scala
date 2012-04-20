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
package com.sattvik.baitha.views

import android.widget.SeekBar

/** Adds a convenience method for setting an event handler for a SeekBar.
  *
  * @author Daniel Solano Gómez */
class EnhancedSeekBar(seekBar: SeekBar) {
  /** Sets the change listener on SeekBar by wrapping the given partial
    * function in a `SeekBar.OnSeekBarChangeListener`.  As such,
    * you can use it a manner similar to:
    *
    * {{{
    * val seekBar: SeekBar = …
    * seekBar oSeekBarEvent {
    *   case ProgressChange(`seekBar`, x, true) => // do something…
    *   case _ => // ignore all other events
    * }
    * }}}
    *
    * @param f the partial function to wrap into the listener object that
    *          will handle SeekBarEvents.
    */
  def onSeekBarEvent(f: PartialFunction[SeekBarEvent, Unit]) {
    seekBar.setOnSeekBarChangeListener(
      new SeekBar.OnSeekBarChangeListener {
        def onProgressChanged(
          seekBar: SeekBar,
          progress: Int,
          fromUser: Boolean
        ) {
          f(ProgressChange(seekBar, progress, fromUser))
        }

        def onStopTrackingTouch(seekBar: SeekBar) {
          f(StopTrackingTouch(seekBar))
        }

        def onStartTrackingTouch(seekBar: SeekBar) {
          f(StartTrackingTouch(seekBar))
        }
      }
    )
  }
}

/** The parent class for all seek bar events. */
sealed trait SeekBarEvent {
  /** The source of the event. */
  def source: SeekBar
}

/** A notification that the progress level has changed.  You can use the
  * fromUser field to distinguish between user-initiated changes from those
  * that occurred programmatically.
  *
  * @param source the seek bar that triggered the event
  * @param progress the current progress level, which will be in the range
  *                 of 0…max
  * @param fromUser true if the progress change was initiated by the user
  */
final case class ProgressChange(
  source: SeekBar, progress: Int,
  fromUser: Boolean
) extends SeekBarEvent

/** A notification that the user has started a touch gesture.  You may want
  * to use this to disable advancing the seek bar.
  *
  * @param source the seek bar that triggered the event
  */
final case class StartTrackingTouch(source: SeekBar) extends SeekBarEvent

/** A notification that the user has finished a touch gesture.  You may want
  * to use this to re-enable advancing the seek bar.
  *
  * @param source the seek bar that triggered the event
  */
final case class StopTrackingTouch(source: SeekBar) extends SeekBarEvent
