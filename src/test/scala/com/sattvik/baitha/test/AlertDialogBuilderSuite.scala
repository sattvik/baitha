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

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.view.View
import org.mockito.Mockito._
import org.mockito.Matchers
import org.mockito.Matchers._
import org.scalatest.{OneInstancePerTest, Suite}
import com.sattvik.baitha.AlertDialogBuilder
import com.sattvik.baitha.AlertDialogBuilder._
import com.sattvik.baitha.test.AlertDialogBuilderSuite._
import android.content.{DialogInterface, Context}
import org.mockito.Matchers

/** Test suite for the AlertDialogBuilder utility.
  *
  * @author Daniel Solano Gómez */
class AlertDialogBuilderSuite extends Suite with OneInstancePerTest {
  /** Mock builder used to test the AlertDialogBuilder. */
  private val builder = mock(classOf[AlertDialog.Builder])

  /** Builder creator to inject the mock builder into the tested
    * AlertDialogBuilder. */
  private val mockFactory = { c: Context => builder }

  /** Tests that the underlying create method is called. */
  def testDefaultsCreate() {
    AlertDialogBuilder(context, messageString)(mockFactory).create
    verify(builder).create
  }

  /** Tests that the underlying show method is called. */
  def testDefaultsShow() {
    AlertDialogBuilder(context, messageString)(mockFactory).show()
    verify(builder).show()
  }

  /** Tests that trying to pass in a null context fails. */
  def testNullContext() {
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(null, messageString)(mockFactory)
    }
  }

  /** Tests defining a string-based message. */
  def testStringContent() {
    AlertDialogBuilder(context, messageString)(mockFactory)
    verify(builder).setMessage(messageString)
    verifyNoMoreInteractions(builder)
  }

  /** Tests trying use null string as content fails.. */
  def testNullStringContent() {
    val nullString: String = null
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(context, nullString)(mockFactory)
    }
  }

  /** Tests using a resource ID-based message. */
  def testResourceContent() {
    AlertDialogBuilder(context, messageId)(mockFactory)
    verify(builder).setMessage(messageId)
  }

  /** Tests that passing in a view for a title sets a custom view. */
  def testCustomViewTitle() {
    val view = mock(classOf[View])
    AlertDialogBuilder(
      context,
      messageString,
      title = view
    )(mockFactory)

    verify(builder).setCustomTitle(view)
  }

  /** Tests that a null view for a title does not work. */
  def testNullViewTitle() {
    val view: View = null
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        messageString,
        title = view
      )(mockFactory)
    }
  }

  /** Tests that passing in a string causes it to be set as the title. */
  def testCharSeqTitle() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleString
    )(mockFactory)

    verify(builder).setTitle(titleString)
  }

  /** Tests setting the title to a string and with a resource ID icon. */
  def testCharSeqTitleWithResourceIcon() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleString withIcon iconId
    )(mockFactory)

    verify(builder).setTitle(titleString)
    verify(builder).setIcon(iconId)
  }

  /** Tests setting the title to a string and with a drawable icon. */
  def testCharSeqTitleWithDrawableIcon() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleString withIcon iconDrawable
    )(mockFactory)

    verify(builder).setTitle(titleString)
    verify(builder).setIcon(iconDrawable)
  }

  /** Tests that a null character sequence for a title does not work. */
  def testNullStringTitle() {
    val title: String = null
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        messageString,
        title = title
      )(mockFactory)
    }
  }

  /** Tests setting the title to a string and with a drawable icon. */
  def testCharSeqTitleWithNullIcon() {
    val icon: Drawable = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        messageString,
        title = titleString withIcon icon
      )(mockFactory)
    }
  }

  /** Tests setting the title to a resource ID works. */
  def testResourceTitle() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleId
    )(mockFactory)

    verify(builder).setTitle(titleId)
  }

  /** Tests setting the title to a resource ID and with a resource ID icon. */
  def testResourceTitleWithResourceIcon() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleId withIcon iconId
    )(mockFactory)

    verify(builder).setTitle(titleId)
    verify(builder).setIcon(iconId)
  }

  /** Tests setting the title to a resource ID and with a resource ID icon. */
  def testResourceTitleWithDrawableIcon() {
    AlertDialogBuilder(
      context,
      messageString,
      title = titleId withIcon iconDrawable
    )(mockFactory)

    verify(builder).setTitle(titleId)
    verify(builder).setIcon(iconDrawable)
  }

  /** Tests setting the title to a string and with a drawable icon. */
  def testResourceTitleWithNullIcon() {
    val icon: Drawable = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        messageString,
        title = titleString withIcon icon
      )(mockFactory)
    }
  }

  /** Tests adding a positive button based on a resource ID. */
  def testResourcePositiveButton() {
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonId
    )(mockFactory)

    verify(builder).setPositiveButton(positiveButtonId, null)
  }

  /** Tests adding a positive button based on a string. */
  def testStringPositiveButton() {
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonString
    )(mockFactory)

    verify(builder).setPositiveButton(positiveButtonString, null)
  }

  /** Tests adding a positive button based on a resource ID along with a
    * onClick listener. */
  def testResourcePositiveButtonWithOnClickListener() {
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonId onClick positiveListener
    )(mockFactory)

    verify(builder).setPositiveButton(positiveButtonId, positiveListener)
  }

  /** Tests adding a positive button based on a resource ID along with a
    * onClick handler function. */
  def testResourcePositiveButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonId onClick onClickFn
    )(mockFactory)

    verify(builder).setPositiveButton(
      Matchers.eq(positiveButtonId),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a positive button based on a string along with a onClick
    *  handler function. */
  def testStringPositiveButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonString onClick onClickFn
    )(mockFactory)

    verify(builder).setPositiveButton(
      Matchers.eq(positiveButtonString),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a positive button based on a resource ID along with a null
    * onClick handler function. */
  def testResourcePositiveButtonWithNullOnClickFunction() {
    val fn: OnClickFunction = null
    AlertDialogBuilder(
      context,
      messageString,
      positiveButton = positiveButtonId onClick fn
    )(mockFactory)

    verify(builder).setPositiveButton(positiveButtonId, null)
  }

  /** Tests adding a negative button based on a resource ID. */
  def testResourceNegativeButton() {
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonId
    )(mockFactory)

    verify(builder).setNegativeButton(negativeButtonId, null)
  }

  /** Tests adding a negative button based on a string. */
  def testStringNegativeButton() {
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonString
    )(mockFactory)

    verify(builder).setNegativeButton(negativeButtonString, null)
  }

  /** Tests adding a negative button based on a resource ID along with a
    * onClick listener. */
  def testResourceNegativeButtonWithOnClickListener() {
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonId onClick negativeListener
    )(mockFactory)

    verify(builder).setNegativeButton(negativeButtonId, negativeListener)
  }

  /** Tests adding a negative button based on a resource ID along with a
    * onClick handler function. */
  def testResourceNegativeButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonId onClick onClickFn
    )(mockFactory)

    verify(builder).setNegativeButton(
      Matchers.eq(negativeButtonId),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a negative button based on a string along with a onClick
    *  handler function. */
  def testStringNegativeButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonString onClick onClickFn
    )(mockFactory)

    verify(builder).setNegativeButton(
      Matchers.eq(negativeButtonString),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a negative button based on a resource ID along with a null
    * onClick handler function. */
  def testResourceNegativeButtonWithNullOnClickFunction() {
    val fn: OnClickFunction = null
    AlertDialogBuilder(
      context,
      messageString,
      negativeButton = negativeButtonId onClick fn
    )(mockFactory)

    verify(builder).setNegativeButton(negativeButtonId, null)
  }
}

/** Constants for use by the `AlertDialogBuilderSuite` test suite.
  *
  * @author Daniel Solano Gómez */
object AlertDialogBuilderSuite {
  /** A mock context to use as the argument for the AlertDialogBuilder. */
  val context = mock(classOf[Context])
  /** A string to use as message content. */
  val messageString = "A message"
  /** A resource ID to use as message content. */
  val messageId = android.R.string.untitled
  /** A sample custom title view. */
  val titleView = mock(classOf[View])
  /** A sample title ID. */
  val titleId = android.R.string.dialog_alert_title
  /** A sample title string. */
  val titleString = "Some title"
  /** A sample icon resource ID. */
  val iconId = android.R.drawable.ic_dialog_info
  /** A sample drawable icon. */
  val iconDrawable = mock(classOf[Drawable])
  /** Resource ID for positive button. */
  val positiveButtonId = android.R.string.yes
  /** String for positive button. */
  val positiveButtonString = "Yes"
  /** A listener for positive button click events. */
  val positiveListener = mock(classOf[DialogInterface.OnClickListener])
  /** Resource ID for negative button. */
  val negativeButtonId = android.R.string.yes
  /** String for negative button. */
  val negativeButtonString = "Yes"
  /** A listener for negative button click events. */
  val negativeListener = mock(classOf[DialogInterface.OnClickListener])
  /** An on-click handler function. */
  val onClickFn = {(_: DialogInterface, _: Int) => }
}