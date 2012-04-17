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

import java.util.Date
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.{AdapterView, ListAdapter}
import org.mockito.Mockito._
import org.mockito.Matchers
import org.mockito.Matchers.{eq => isEq, _}
import org.scalatest.{OneInstancePerTest, Suite}
import com.sattvik.baitha.AlertDialogBuilder
import com.sattvik.baitha.AlertDialogBuilder._
import com.sattvik.baitha.test.AlertDialogBuilderSuite._
import android.content.{DialogInterface, Context}
import android.database.Cursor

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
      isEq(positiveButtonId),
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
      isEq(positiveButtonString),
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
      isEq(neutralButtonId),
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
      isEq(neutralButtonString),
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
      isEq(negativeButtonId),
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
      isEq(negativeButtonString),
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
      isEq(adapter),
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
      isEq(cursor),
      isEq(checkedColumn),
      isEq(labelColumn),
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

  /** Tests using an array resource ID for content. */
  def testArrayResourceContent() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList
    )(mockFactory)

    verify(builder).setItems(arrayResourceId, null)
  }

  /** Tests using an array resource ID for content with a listener. */
  def testArrayResourceContentWithListener() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList onClick onClickListener
    )(mockFactory)

    verify(builder).setItems(arrayResourceId, onClickListener)
  }

  /** Tests using an array resource ID with single-choice mode. */
  def testArrayResourceContentWithSingleChoice() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList withSingleChoice checkedItem
    )(mockFactory)

    verify(builder).setSingleChoiceItems(arrayResourceId, checkedItem, null)
  }

  /** Tests using an array resource ID with single-choice mode and a
    * listener. */
  def testArrayResourceContentWithSingleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList withSingleChoice() onClick onClickListener
    )(mockFactory)

    verify(builder).setSingleChoiceItems(arrayResourceId, -1, onClickListener)
  }

  /** Tests using an array resource ID with multiple-choice mode. */
  def testArrayResourceContentWithMultipleChoice() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList withMultipleChoices arrayChoices
    )(mockFactory)

    verify(builder).setMultiChoiceItems(arrayResourceId, arrayChoices,  null)
  }

  /** Tests using an array resource ID with multiple-choice mode and a
    * listener. */
  def testArrayResourceContentWithMultipleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList withMultipleChoices arrayChoices
          onMultiChoiceClick onMultiClickListener
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      arrayResourceId, arrayChoices, onMultiClickListener)
  }

  /** Tests using an array resource ID in multiple-choice mode with no
    * selections. */
  def testArrayResourceContentWithNoMultipleChoices() {
    AlertDialogBuilder(
      context,
      arrayResourceId.asList withMultipleChoices()
          onMultiChoiceClick onMultiClickListener
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      arrayResourceId, null, onMultiClickListener)
  }

  /** Tests using an array of strings for content. */
  def testStringArrayContent() {
    AlertDialogBuilder(
      context,
      stringArrayItems
    )(mockFactory)

    verify(builder).setItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      null)
  }

  /** Tests using a null array of strings for content. */
  def testNullStringArrayContent() {
    val nullContent: Array[CharSequence] = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        nullContent
      )(mockFactory)
    }
  }

  /** Tests using an array of general objects for content. */
  def testObjectArrayContent() {
    AlertDialogBuilder(
      context,
      dateArrayItems
    )(mockFactory)

    verify(builder).setItems(
      dateArrayItems.map(_.toString).toArray.asInstanceOf[Array[CharSequence]],
      null)
  }

  /** Tests using a null array of other objects for content. */
  def testNullObjectArrayContent() {
    val nullContent: Array[Int] = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        nullContent
      )(mockFactory)
    }
  }

  /** Tests using an collection for content. */
  def testCollectionContent() {
    AlertDialogBuilder(
      context,
      stringListItems
    )(mockFactory)

    verify(builder).setItems(
      stringListItems.toArray.asInstanceOf[Array[CharSequence]],
      null)
  }

  /** Tests using a null collection for content. */
  def testNullCollectionContent() {
    val nullContent: List[List[AnyRef]] = null

    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        nullContent
      )(mockFactory)
    }
  }

  /** Tests using a string array content with a listener. */
  def testStringArrayContentWithListener() {
    AlertDialogBuilder(
      context,
      stringArrayItems onClick onClickListener
    )(mockFactory)

    verify(builder).setItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      onClickListener)
  }

  /** Tests using a string array with single-choice mode. */
  def testStringArrayContentWithSingleChoice() {
    AlertDialogBuilder(
      context,
      stringArrayItems withSingleChoice checkedItem
    )(mockFactory)

    verify(builder).setSingleChoiceItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      checkedItem,
      null)
  }

  /** Tests using a string array with single-choice mode and a listener. */
  def testStringArrayContentWithSingleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      stringArrayItems withSingleChoice() onClick onClickListener
    )(mockFactory)

    verify(builder).setSingleChoiceItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      -1,
      onClickListener)
  }

  /** Tests using an array resource ID with multiple-choice mode. */
  def testStringArrayContentWithMultipleChoice() {
    AlertDialogBuilder(
      context,
      stringArrayItems withMultipleChoices arrayChoices
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      arrayChoices,
      null)
  }

  /** Tests using an array resource ID with wrong number of multiple choices. */
  def testStringArrayContentWithBadMultipleChoices() {
    intercept[IllegalArgumentException] {
      AlertDialogBuilder(
        context,
        stringArrayItems withMultipleChoices Array(true, false)
      )(mockFactory)
    }
  }

  /** Tests using a string array with multiple-choice mode and a listener. */
  def testStringArrayContentWithMultipleChoiceAndListener() {
    AlertDialogBuilder(
      context,
      stringArrayItems withMultipleChoices arrayChoices
          onMultiChoiceClick onMultiClickListener
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      arrayChoices,
      onMultiClickListener)
  }

  /** Tests using a string array in multiple-choice mode with no selections. */
  def testStringArrayContentWithNoMultipleChoices() {
    AlertDialogBuilder(
      context,
      stringArrayItems withMultipleChoices()
          onMultiChoiceClick onMultiClickListener
    )(mockFactory)

    verify(builder).setMultiChoiceItems(
      stringArrayItems.asInstanceOf[Array[CharSequence]],
      null,
      onMultiClickListener)
  }

  /** Tests setting an on cancel listener. */
  def testSetOnCancelListener() {
    val listener = new DialogInterface.OnCancelListener {
      def onCancel(dialog: DialogInterface) {}
    }

    AlertDialogBuilder(
      context,
      messageString,
      onCancel = listener
    )(mockFactory)

    verify(builder).setOnCancelListener(listener)
  }

  /** Tests setting an on cancel listener with a function. */
  def testSetOnCancelFunction() {
    val function = {_: DialogInterface => }

    AlertDialogBuilder(
      context,
      messageString,
      onCancel = function
    )(mockFactory)

    verify(builder).setOnCancelListener(
      isA(classOf[DialogInterface.OnCancelListener]))
  }

  /** Tests setting an on cancel listener. */
  def testSetOnItemSelectedListener() {
    val listener = new AdapterView.OnItemSelectedListener {
      def onNothingSelected(p1: AdapterView[_]) {}

      def onItemSelected(p1: AdapterView[_], p2: View, p3: Int, p4: Long) {}
    }

    AlertDialogBuilder(
      context,
      messageString,
      onItemSelectedListener = listener
    )(mockFactory)

    verify(builder).setOnItemSelectedListener(listener)
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
  /** Resource ID for list content. */
  val arrayResourceId = android.R.array.phoneTypes
  /** An array of choices for multi-choice mode. */
  val arrayChoices = Array(false, true, true, false, false)
  /** An array of items to pass directly as content. */
  val stringArrayItems = Array("one", "two", "three", "four", "five")
  /** A list of items to pass directly as content. */
  val stringListItems = List("one", "two", "three", "four", "five")
  /** An array of non-character sequence objects to pass as content. */
  val dateArrayItems = Array(new Date, new Date, new Date)
}