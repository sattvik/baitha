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

import android.view.View
import android.widget._
import org.mockito.Matchers._
import org.mockito.Mockito._
import com.sattvik.baitha.EnhancedViews
import org.scalatest.{Suite, OneInstancePerTest}

/** Defines the tests for the EnhancedViews trait and companion object.
  *
  * @author Daniel Solano Gómez */
abstract class EnhancedViewsSuite extends Suite with OneInstancePerTest {
  private val mockedView = mock(classOf[View])

  /** Tests that views can have an enhanced onClick method. */
  final def testOnClick() {
    doOnClick(mockedView)

    verify(mockedView).setOnClickListener(any(classOf[View.OnClickListener]))
  }

  /** Tests that `AdapterView`s can have an enhanced onItemClick method. */
  final def testOnItemClick() {
    val view = mock(classOf[AdapterView[ListAdapter]])

    doOnItemClick(view)

    verify(view).setOnItemClickListener(
      isA(classOf[AdapterView.OnItemClickListener]))
  }

  /** Tests that `AdapterView`s can have an enhanced onItemLongClick method. */
  final def testOnItemLongClick() {
    val view = mock(classOf[AdapterView[ListAdapter]])

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
      def newMockRadioButton = mock(classOf[RadioButton])
      val group = mock(classOf[RadioGroup])
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
}
