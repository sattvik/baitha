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

import scala.collection.mutable.ListBuffer
import android.view.View
import android.widget._
import com.sattvik.baitha.EnhancedViews
import com.sattvik.baitha.views._
import org.mockito.ArgumentCaptor
import org.scalatest.{Suite, OneInstancePerTest}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

import org.mockito.Matchers._
import org.mockito.Mockito.{verify, when}

/** Defines the tests for the EnhancedViews trait and companion object.
  *
  * @author Daniel Solano Gómez */
abstract class EnhancedViewsSuite extends Suite with OneInstancePerTest with
                                          MockitoSugar with ShouldMatchers {
  /** Tests that views can have an enhanced onClick method. */
  final def testOnClick() {
    val mockedView = mock[View]

    doOnClick(mockedView)

    verify(mockedView).setOnClickListener(any(classOf[View.OnClickListener]))
  }

  /** Tests that `AdapterView`s can have an enhanced onItemClick method. */
  final def testOnItemClick() {
    val view = mock[AdapterView[ListAdapter]]

    doOnItemClick(view)

    verify(view).setOnItemClickListener(
      isA(classOf[AdapterView.OnItemClickListener]))
  }

  /** Tests that `AdapterView`s can have an enhanced onItemLongClick method. */
  final def testOnItemLongClick() {
    val view = mock[AdapterView[ListAdapter]]

    doOnItemLongClick(view)

    verify(view).setOnItemLongClickListener(
      isA(classOf[AdapterView.OnItemLongClickListener]))
  }

  /** Allow each implementation to actually do the onClick */
  def doOnClick(view: View)

  /** Allow each implementation to actually do the onItemClick */
  def doOnItemClick(view: AdapterView[_])

  /** Allow each implementation to actually do the onItemLongClick */
  def doOnItemLongClick(view: AdapterView[_])

  /** Tests that the setChildrenEnabled method works. */
  def testSetChildrenEnabled() {
    def performTest(numChildren: Int, enabled: scala.Boolean) {
      def newMockRadioButton = mock[RadioButton]
      val group = mock[RadioGroup]
      val buttons = Array.fill(numChildren)(newMockRadioButton)

      // mock the calls to the radio group
      when(group.getChildCount).thenReturn(numChildren)
      for (i <- 0 until numChildren) {
        when(group.getChildAt(i)).thenReturn(buttons(i))
      }

      doSetChildrenEnabled(group, enabled)

      buttons foreach {
        verify(_).setEnabled(enabled)
      }
    }

    performTest(6, true)
    performTest(101, false)
    performTest(1, true)
    performTest(0, false)
  }

  /** Actually performs the setChildrenEnabled call. */
  def doSetChildrenEnabled(radioGroup: RadioGroup, enabled: scala.Boolean)

  def testOnSeekBarEvent() {
    val seekBar = mock[SeekBar]
    val events = ListBuffer[SeekBarEvent]()

    val eventHandler: PartialFunction[SeekBarEvent, Unit] = {
      case ev @ ProgressChange(`seekBar`, _, _) => events += ev
      case ev @ StartTrackingTouch(`seekBar`) => events += ev
      case ev @ StopTrackingTouch(`seekBar`) => events += ev
      case _ => // ignore everything else
    }

    doOnSeekBarEvent(seekBar, eventHandler)

    val captor = ArgumentCaptor
        .forClass(classOf[SeekBar.OnSeekBarChangeListener]);
    verify(seekBar).setOnSeekBarChangeListener(captor.capture());
    val listener = captor.getValue

    val fake = mock[SeekBar]("Fake SeekBar")

    listener.onStartTrackingTouch(seekBar)
    listener.onStartTrackingTouch(fake)
    listener.onProgressChanged(seekBar, 10, true)
    listener.onProgressChanged(fake, 10, true)
    listener.onProgressChanged(seekBar, 40, false)
    listener.onStopTrackingTouch(seekBar)

    events should equal (List(
      StartTrackingTouch(seekBar),
      ProgressChange(seekBar, 10, true),
      ProgressChange(seekBar, 40, false),
      StopTrackingTouch(seekBar)
    ))
  }

  /** Actually sets the event listener. */
  def doOnSeekBarEvent(seekBar: SeekBar, f: PartialFunction[SeekBarEvent, Unit])

  /** Tests that compound buttons can have an enhanced onCheckChange method. */
  final def testOnCheckChange() {
    val button = mock[CompoundButton]

    doOnCheckChange(button)

    verify(button).setOnCheckedChangeListener(any(classOf[CompoundButton.OnCheckedChangeListener]))
  }

  /** Allow each implementation to actually do the onCheckChange */
  def doOnCheckChange(button: CompoundButton)
}

/** Tests the EnhancedViews companion object.
  *
  * @author Daniel Solano Gómez */
class EnhancedViewsObjectSuite extends EnhancedViewsSuite {
  import com.sattvik.baitha.EnhancedViews._

  def doOnClick(view: View) {
    view onClick {_ =>}
  }

  def doOnItemClick(view: AdapterView[_]) {
    view onItemClick {(_, _, _, _) =>}
  }

  def doOnItemLongClick(view: AdapterView[_]) {
    view onItemLongClick {(_, _, _, _) => true}
  }

  def doSetChildrenEnabled(radioGroup: RadioGroup, enabled: scala.Boolean) {
    radioGroup.setChildrenEnabled(enabled)
  }

  def doOnSeekBarEvent(
    seekBar: SeekBar,
    f: PartialFunction[SeekBarEvent, Unit]
  ) {
    seekBar onSeekBarEvent f
  }

  def doOnCheckChange(button: CompoundButton) {
    button onCheckChange {(_,_) =>}
  }
}

/** Tests the EnhancedViews trait.
  *
  * @author Daniel Solano Gómez */
class EnhancedViewsTraitSuite extends EnhancedViewsSuite with EnhancedViews {
  def doOnClick(view: View) {
    view onClick {_ =>}
  }

  def doOnItemClick(view: AdapterView[_]) {
    view onItemClick {(_, _, _, _) =>}
  }

  def doOnItemLongClick(view: AdapterView[_]) {
    view onItemLongClick {(_, _, _, _) => true}
  }

  def doSetChildrenEnabled(radioGroup: RadioGroup, enabled: scala.Boolean) {
    radioGroup.setChildrenEnabled(enabled)
  }

  def doOnSeekBarEvent(
    seekBar: SeekBar,
    f: PartialFunction[SeekBarEvent, Unit]
  ) {
    seekBar onSeekBarEvent f
  }

  def doOnCheckChange(button: CompoundButton) {
    button onCheckChange {(_,_) =>}
  }
}
