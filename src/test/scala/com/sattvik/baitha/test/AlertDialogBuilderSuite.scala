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
import android.widget.ListAdapter
import org.mockito.Mockito._
import org.mockito.Matchers
import org.mockito.Matchers._
import org.scalatest.{OneInstancePerTest, Suite}
import com.sattvik.baitha.AlertDialogBuilder
import com.sattvik.baitha.AlertDialogBuilder._
import com.sattvik.baitha.test.AlertDialogBuilderSuite._
import android.content.{DialogInterface, Context}
import android.database.Cursor
import org.mockito.internal.matchers.InstanceOf

/** Test suite for the AlertDialogBuilder utility.
  *
  * @author Daniel Solano Gómez */
class AlertDialogBuilderSuite extends Suite with OneInstancePerTest {
  /** Mock builder used to test the AlertDialogBuilder. */
  private val builder = mock(classOf[AlertDialog.Builder])

  /** Builder creator to inject the mock builder into the tested
    * AlertDialogBuilder. */
  private val mockFactory = { c: Context => builder }

  /** Tests the default settings. */
  def testDefaults() {
    AlertDialogBuilder(context, messageString)(mockFactory)
    verify(builder).setMessage(messageString)
    verify(builder).setCancelable(true)
    verify(builder).setInverseBackgroundForced(false)
    verifyNoMoreInteractions(builder)
  }

  /** Tests that the underlying create method is called. */
  def testCreate() {
    AlertDialogBuilder(context, messageString)(mockFactory).create
    verify(builder).create
  }

  /** Tests that the underlying show method is called. */
  def testShow() {
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
      positiveButton = positiveButtonId onClick onClickListener
    )(mockFactory)

    verify(builder).setPositiveButton(positiveButtonId, onClickListener)
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

  /** Tests adding a neutral button based on a resource ID. */
  def testResourceNeutralButton() {
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonId
    )(mockFactory)

    verify(builder).setNeutralButton(neutralButtonId, null)
  }

  /** Tests adding a neutral button based on a string. */
  def testStringNeutralButton() {
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonString
    )(mockFactory)

    verify(builder).setNeutralButton(neutralButtonString, null)
  }

  /** Tests adding a neutral button based on a resource ID along with a
    * onClick listener. */
  def testResourceNeutralButtonWithOnClickListener() {
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonId onClick onClickListener
    )(mockFactory)

    verify(builder).setNeutralButton(neutralButtonId, onClickListener)
  }

  /** Tests adding a neutral button based on a resource ID along with a
    * onClick handler function. */
  def testResourceNeutralButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonId onClick onClickFn
    )(mockFactory)

    verify(builder).setNeutralButton(
      Matchers.eq(neutralButtonId),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a neutral button based on a string along with a onClick
    *  handler function. */
  def testStringNeutralButtonWithOnClickFunction() {
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonString onClick onClickFn
    )(mockFactory)

    verify(builder).setNeutralButton(
      Matchers.eq(neutralButtonString),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests adding a neutral button based on a resource ID along with a null
    * onClick handler function. */
  def testResourceNeutralButtonWithNullOnClickFunction() {
    val fn: OnClickFunction = null
    AlertDialogBuilder(
      context,
      messageString,
      neutralButton = neutralButtonId onClick fn
    )(mockFactory)

    verify(builder).setNeutralButton(neutralButtonId, null)
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
      negativeButton = negativeButtonId onClick onClickListener
    )(mockFactory)

    verify(builder).setNegativeButton(negativeButtonId, onClickListener)
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

  /** Tests turning off cancellation. */
  def testNotCancellable() {
    AlertDialogBuilder(
      context,
      messageString,
      cancellable = false
    )(mockFactory)

    verify(builder).setCancelable(false)
  }

  /** Tests turning off cancellation. */
  def testCancellable() {
    AlertDialogBuilder(
      context,
      messageString,
      cancellable = true
    )(mockFactory)

    verify(builder).setCancelable(true)
  }

  /** Tests the basic use case of having an adapter for content. */
  def testAdapterContent() {
    AlertDialogBuilder(
      context,
      adapter
    )(mockFactory)

    verify(builder).setAdapter(adapter, null)
  }

  /** Trying to use a null adapter should fail. */
  def testNullAdapterContent() {
    val nullAdapter: ListAdapter = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        nullAdapter
      )(mockFactory)
    }
  }

  /** Tests the use case of having an adapter for content and a listener
    * object. */
  def testAdapterContentWithListener() {
    AlertDialogBuilder(
      context,
      adapter onClick onClickListener
    )(mockFactory)

    verify(builder).setAdapter(adapter, onClickListener)
  }

  /** Tests the use case of using an adapter for content with a handler
    * function. */
  def testAdapterContentWithFunction() {
    AlertDialogBuilder(
      context,
      adapter onClick onClickFn
    )(mockFactory)

    verify(builder).setAdapter(
      Matchers.eq(adapter),
      isA(classOf[DialogInterface.OnClickListener]))
  }

  /** Tests the case of having an adapter for content with a selected item. */
  def testAdapterContentWithCheckedItem() {
    AlertDialogBuilder(
      context,
      adapter withSingleChoice checkedItem
    )(mockFactory)

    verify(builder).setSingleChoiceItems(adapter, checkedItem, null)
  }

  /** Tests the case of having an adapter for content with a selected item. */
  def testAdapterContentWithBadCheckedItem() {
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        adapter withSingleChoice -2
      )(mockFactory)
    }
  }

  /** Tests the case of having an adapter for content with a selected item
    * and a listener. */
  def testAdapterContentWithCheckedItemAndListener() {
    AlertDialogBuilder(
      context,
      adapter withSingleChoice checkedItem onClick onClickListener
    )(mockFactory)

    verify(builder).setSingleChoiceItems(
      adapter,
      checkedItem,
      onClickListener)
  }

  /** Tests the case of having an adapter for content that supports a single
    * choice with no checked item and a listener. */
  def testAdapterContentWithNoCheckedItemAndListener() {
    AlertDialogBuilder(
      context,
      adapter withSingleChoice() onClick onClickListener
    )(mockFactory)

    verify(builder).setSingleChoiceItems(adapter, -1, onClickListener)
  }

  /** Tests the basic use case of having an cursor for content. */
  def testCursorContent() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn
    )(mockFactory)

    verify(builder).setCursor(cursor, null, labelColumn)
  }

  /** Trying to use a null cursor should fail. */
  def testNullCursorContent() {
    val nullCursor: Cursor = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        nullCursor withLabel labelColumn
      )(mockFactory)
    }
  }

  /** Trying to use a cursor with a null label column should fail. */
  def testCursorContentNullLabel() {
    val nullLabel: String = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        cursor withLabel nullLabel
      )(mockFactory)
    }
  }

  /** Tests using a cursor with a listener. */
  def testCursorContentWithListener() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn onClick onClickListener
    )(mockFactory)

    verify(builder).setCursor(cursor, onClickListener, labelColumn)
  }

  /** Tests using a cursor with single-choice mode. */
  def testCursorContentWithSingleChoice() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn withSingleChoice checkedItem
    )(mockFactory)

    verify(builder).setSingleChoiceItems(
      cursor, checkedItem,  labelColumn, null)
  }

  /** Tests using a cursor with single-choice mode and a listener. */
  def testCursorContentWithSingleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn withSingleChoice() onClick onClickListener
    )(mockFactory)

    verify(builder).setSingleChoiceItems(
      cursor, -1,  labelColumn, onClickListener)
  }

  /** Tests using a cursor with multiple-choice mode. */
  def testCursorContentWithMultipleChoice() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn withMultipleChoices checkedColumn
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      cursor, checkedColumn,  labelColumn, null)
  }

  /** Trying to use a null multiple choice label should fail. */
  def testCursorContentWithNullMultipleChoiceLabel() {
    val nullLabel: String = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        cursor withLabel labelColumn withMultipleChoices nullLabel
      )(mockFactory)
    }
  }

  /** Tests using a cursor with multiple-choice mode and a listener. */
  def testCursorContentWithMultipleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn withMultipleChoices checkedColumn
          onMultiChoiceClick onMultiClickListener
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      cursor, checkedColumn,  labelColumn, onMultiClickListener)
  }

  /** Tests using a cursor with multiple-choice mode and a handler function. */
  def testCursorContentWithMultipleChoiceAndHandlerFunction() {
    AlertDialogBuilder(
      context,
      cursor withLabel labelColumn withMultipleChoices checkedColumn
          onMultiChoiceClick onMultiClickFn
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      Matchers.eq(cursor),
      Matchers.eq(checkedColumn),
      Matchers.eq(labelColumn),
      isA(classOf[DialogInterface.OnMultiChoiceClickListener]))
  }

  /** Tests forcing an inverse background. */
  def testForceInverseBackground() {
    AlertDialogBuilder(
      context,
      messageString,
      forceInverseBackground = true
    )(mockFactory)

    verify(builder).setInverseBackgroundForced(true)
  }

  /** Tests not forcing an inverse background. */
  def testDoNotForceInverseBackground() {
    AlertDialogBuilder(
      context,
      messageString,
      forceInverseBackground = false
    )(mockFactory)

    verify(builder).setInverseBackgroundForced(false)
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
  /** Resource ID for neutral button. */
  val neutralButtonId = android.R.string.cancel
  /** String for neutral button. */
  val neutralButtonString = "Maybe"
  /** Resource ID for negative button. */
  val negativeButtonId = android.R.string.no
  /** String for negative button. */
  val negativeButtonString = "No"
  /** A listener for click events. */
  val onClickListener = mock(classOf[DialogInterface.OnClickListener])
  /** An on-click handler function. */
  val onClickFn = {(_: DialogInterface, _: Int) => }
  /** A mock adapter to be used for content. */
  val adapter = mock(classOf[ListAdapter])
  /** The checked item for a single choice adapter. */
  val checkedItem = 3
  /** A mock cursor to be used for content. */
  val cursor = mock(classOf[Cursor])
  /** The label column for the cursor. */
  val labelColumn = "label"
  /** The label column for the cursor. */
  val checkedColumn = "isChecked"
  /** A listener for multi-click events. */
  val onMultiClickListener =
    mock(classOf[DialogInterface.OnMultiChoiceClickListener])
  val onMultiClickFn = {(_:DialogInterface, _: Int, _:Boolean) => }
}