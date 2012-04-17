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

import android.app.AlertDialog
import android.content.{DialogInterface, Context}
import android.content.DialogInterface._
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ListAdapter
import com.sattvik.baitha.AlertDialogBuilder.{DialogueFunctor, BuilderFactory}

/** Builds or shows an alert dialogue from the arguments passed in to the
  * constructor.  Too how to use the builder, refer to the documentation for
  * the companion object.
  *
  * @constructor Creates a new `AlertDialog.Builder` object and applies the
  * arguments to it.  This is used by the `apply()` function from the
  * companion object and should not be called directly.
  *
  * @param context the context for the builder and the generated `AlertDialog`
  * @param factory a function used to create new `AlertDialog.Builder`
  * objects, primarily exists to inject mock objects for testing
  * @param actions the collection of dialogue component action
  * @param cancellable whether or not the dialogue should be cancellable
  *
  * @author Daniel Solano Gómez */
class AlertDialogBuilder private(
  context: Context,
  factory: BuilderFactory,
  actions: List[DialogueFunctor]
) {

  /** The underlying builder. */
  private val builder = factory(context)

  require(context != null, "Dialog context must not be null.")
  // apply all of the actions to the builder
  actions foreach (_(builder))

  /** Creates an `AlertDialog` with the arguments supplied to this builder.
    * It does not `show()` the dialog. This allows the user to do any extra
    * processing before displaying the dialog. Use `show()` if you don't have
    * any other processing to do and want this to be created and displayed. */
  def create: AlertDialog = builder.create

  /** Creates an `AlertDialog` with the arguments supplied to this builder and
    * `show()`s the dialog.
    *
    * @return the dialog that was shown */
  def show(): AlertDialog = builder.show()
}

/** Defines the factory function for creating a new builder, as well as a
  * number of auxiliary types and implicit conversions.
  *
  * = Building a dialogue =
  *
  * To take full advantage of this builder, you will need to have imports as
  * follows:
  *
  * {{{
  * import com.sattvik.baitha.AlertDialogBuilder
  * import com.sattvik.baitha.AlertDialogBuilder._
  * }}}
  *
  * With these imports, you can create a minimal dialogue that contains a
  * message as follows, where `context` is a valid Android context:
  *
  * {{{
  * val builder = AlertDialogBuilder(context, "Some text.")
  * }}}
  *
  * However, there is much more that you can do.  The only requirement is
  * that you specify content and provide a valid context.
  *
  * == Specifying content ==
  *
  * Android's dialogue builder fundamentally supports three different types of
  * dialogue content: messages, lists, and custom views.
  *
  * === Message content ===
  *
  * Message content consists of a string of text that can be specified either
  * as a string or as resource ID reference to a string, as follows:
  *
  * {{{
  * // using a string
  * AlertDialogBuilder(context, "A String.")
  *
  * // using a resource ID
  * AlertDialogBuilder(context, R.string.message)
  * }}}
  *
  * === List content ===
  *
  * Android supports showing dialogues that present a list as their content.
  * Generally, these lists work in three different modes:
  *
  * <ol>
  *   <li>''Default mode'': In this mode, the items are shown in a simple list.
  *   As soon as the user selects an item, the dialogue is dismissed.  An
  *   `OnClickListener` or handler function is used to notify of the result
  *   of the selection.</li>
  *   <li>''Single choice mode'': In this mode, the items are shown with a
  *   check mark displayed next to the text of the selected item.  Clicking on
  *   an item on the list will not dismiss the dialogue.  Instead, you should
  *   provide at least one button to allow the user to dismiss the dialogue.
  *   </li>
  * </ol>
  *
  * In addition, the source of the items for the list may come from one of the
  * following:
  *
  * <ul>
  *   <li>A `ListAdapter` object</li>
  *   <li>A `Cursor` object</li>
  *   <li>Arrays from resource IDs or from collections passed in directly</li>
  * </ul>
  *
  * ==== Choice modes and callbacks ====
  *
  * All list content starts out in the default mode.  You can enable either
  * single-choice or multiple-choice modes by calling `withSingleChoice` or
  * `withMultipleChoices` respectively.  Naturally, the two are mutually
  * incompatible.
  *
  * Additionally, the callback for the default and single-choice modes is an
  * `DialogInterface.OnClickListener` or compatible function, which can be set
  * using `onClick`.  For multiple-choice mode, the callback is
  * `DialogInterface.OnMultiChoiceClickListener`, which is set using
  * `onMultiChoiceClick`.
  *
  * ==== `ListAdapter`-based content ====
  *
  * Dialogue content backed by a `ListAdapter` can operate in either default or
  * single-choice modes.  To enable single-choice mode, add a call to
  * `withSingleChoice(Int)`.  The argument to `withSingleChoice` is optional,
  * and if left out will cause the dialogue to be in single-choice mode with
  * no checked items.
  *
  * Examples:
  *
  * {{{
  * val adapter: ListAdapter = …
  *
  * // default choice mode with no call-back given
  * AlertDialogBuilder(context, adapter)
  *
  * // default choice mode a call-back given
  * AlertDialogBuilder(context, adapter onClick {(_,_) =>})
  *
  * // single-choice mode, with item 2 checked (third item)
  * AlertDialogBuilder(context, adapter withSingleChoice 2)
  *
  * // single-choice mode, no item checked, with call-back
  * val listener: DialogInterface.OnClickListener = …
  * AlertDialogBuilder(context, adapter withSingleChoice() onClick listener)
  * }}}
  *
  * ==== `Cursor`-based content ====
  *
  * Dialogue content backed by a `Cursor` can operate in all three modes.
  *
  * To enable single-choice mode, add a call to `withSingleChoice(Int)`.  The
  * argument to `withSingleChoice` is optional, and if left out will cause the
  * dialogue to be in single-choice mode with no checked items.  Note that
  * using `withSingleChoice` without arguments requires parentheses if there
  * are any following settings.
  *
  * To enable multiple-choice mode, add a call to
  * `withMultipleChoices(String)`.  The argument is mandatory and is the name
  * of the column within the cursor that determines if an item is checked or
  * not.  The column must return an integer value where 1 means checked and 0
  * means unchecked.
  *
  * Examples:
  *
  * {{{
  * val cursor: Cursor = …
  *
  * // default choice mode with no call-back given
  * AlertDialogBuilder(context, cursor)
  *
  * // default choice mode a call-back given
  * AlertDialogBuilder(context, cursor onClick {(_,_) =>})
  *
  * // single-choice mode, with item 2 checked (third item)
  * AlertDialogBuilder(context, cursor withSingleChoice 2)
  *
  * // single-choice mode, no item checked, with call-back
  * val listener: DialogInterface.OnClickListener = …
  * AlertDialogBuilder(context, cursor withSingleChoice() onClick listener)
  *
  * // multiple-choice mode
  * AlertDialogBuilder(context, cursor withMultipleChoices "enabled"
  *
  * // multiple-choice mode, with a call-back
  * AlertDialogBuilder(context, cursor withMultipleChoices "enabled"
  *   onMultiChoiceClick {(_,_,_) => // do something})
  * }}}
  *
  * ==== Collection/array-based content ====
  *
  * Dialogue content backed by an can operate in all three modes.  The array
  * can be passed directly the builder, or it can be read from an array
  * resource ID.  In order to disambiguate from an ID for a message,
  * you should append `.asList` to the resource ID you pass in, as follows:
  *
  * {{{
  * // interpreted as a string message
  * AlertDialogBuilder(context, R.string.my_message)
  *
  * // interpreted as an array
  * AlertDialogBuilder(context, R.array.list_of_goodies.asList)
  * }}}
  *
  * To enable single-choice mode, add a call to `withSingleChoice(Int)`.  The
  * argument to `withSingleChoice` is optional, and if left out will cause the
  * dialogue to be in single-choice mode with no checked items.  Note that
  * using `withSingleChoice` without arguments requires parentheses if there
  * are any following settings.
  *
  * To enable multiple-choice mode, add a call to
  * `withMultipleChoices(Traversable[Boolean])` where the argument is
  * optional.  If omitted, then no items will be checked, and you should place
  * empty parentheses after the `withMultipleChoices`.  If supplied, the
  * length of collection should be the same as the length of the array
  * backing the list.
  *
  * Examples:
  *
  * {{{
  * // default choice mode, using the given array
  * AlertDialogBuilder(context, Array("one", "two", "foo"))
  *
  * // default choice mode a call-back given, using a resource ID
  * AlertDialogBuilder(context, R.array.goodies.asList onClick {(_,_) =>})
  *
  * // single-choice mode, with "foo" checked
  * val items = List("one", "two", "foo")
  * AlertDialogBuilder(context, items withSingleChoice 2)
  *
  * // single-choice mode, no item checked, with call-back
  * val listener: DialogInterface.OnClickListener = …
  * AlertDialogBuilder(context, items withSingleChoice() onClick listener)
  *
  * // multiple-choice mode, with "one" ahd "foo" checked
  * val checked = Array(true, false, true)
  * AlertDialogBuilder(context, items withMultipleChoices checked
  *
  * // multiple-choice mode, with a call-back, and no items checked
  * AlertDialogBuilder(context, R.array.goodies.asList withMultipleChoices()
  *   onMultiChoiceClick {(_,_,_) => // do something})
  * }}}
  *
  * == Specifying the title ==
  *
  * Android supports regular titles that contain some text and an optional
  * icon.  If this is insufficient, you can also provide a custom view that
  * will be used as the title.
  *
  * === Regular titles ===
  *
  * A regular title must have a string, which can be passed directly as an
  * argument, or indirectly as a resource ID:
  *
  * {{{
  * AlertDialogBuilder(…, title = "My Title")
  * AlertDialogBuilder(…, title = R.string.my_title)
  * }}}
  *
  * If you wish to include an icon in your title, you can include it by
  * following the title string/id with `withIcon` and either a resource ID
  * for a drawable or a `Drawable` object:
  * {{{
  * AlertDialogBuilder(…, title = "My Title" withIcon R.drawable.my_icon)
  *
  * val icon: Drawable = …
  * AlertDialogBuilder(…, title = "My Title" withIcon icon)
  * }}}
  *
  * === Custom view titles ===
  *
  * In order to use a custom view, simply pass it as the argument for the
  * title as follows:
  *
  * {{{
  * val myTitleView: View = …
  * AlertDialogBuilder(…, title = myTitleView)
  * }}}
  *
  * == Specifying the buttons ==
  *
  * Android supports up to three buttons, which can be specified by the
  * parameters `positiveButton`, `neutralButton`, and `negativeButton`.  The
  * name for each button has no particular meaning, it is used primarily to
  * differentiate between the three.
  *
  * Each button requires some text either passed in directly or as a reference
  * to a string resource ID:
  *
  * {{{
  * // as a string
  * AlertDialog(…, positiveButton = "Yes")
  *
  * // as a resource ID
  * AlertDialog(…, negativeButton = )
  * }}}
  *
  *
  * Additionally, each button supports an on-click callback that can be
  * passed in either as a function with the signature `(DialogInterface,
  * Int) => Unit` or as instance of `DialogInterface.OnClickListener`.  These
  * can be passed to the builder using `onClick` as follows:
  *
  * {{{
  * val listener: DialogInterface.OnClickListener = …
  *
  * AlertDialog(…,
  *   // as listener object
  *   positiveButton = R.string.yes_please onClick listener,
  *
  *   // as a function
  *   negativeButton = "No, Thanks" onClick { (_, _) =>
  *     // do something!
  *   })
  * }}}
  *
  * == Other settings ==
  *
  * You can also set the following additional settings:
  *
  * <ul>
  *   <li>`cancellable`: whether or not the user can cancel the dialogue,
  *   defaults to true</li>
  *   <li>`forceInverseBackground`: whether or not to force the use of an
  *   inverse background regardless of the content.  List-type content
  *   generally automatically has an inverse background.  This seems to only
  *   have an effect when using a custom view for content.  Defaults to
  *   false</li>
  *   <li>`onCancel`: adds a listener or function that will be executed if
  *   the dialogue is cancelled.</li>
  *   <li>`onItemSelectedListener`: adds a listener that will be executed
  *   when an item in the list is selected or if the selection disappears.</li>
  * </ul>
  *
  *
  * = Using the dialogue =
  *
  * Once you have created your dialogue, there are two ways to use it.
  *
  * <ol>
  *   <li>You  can use the `create` method to get access to a built
  *   `AlertDialog` object you can further customise.</li>
  *   <li>You can use the `show()` method to construct and show the
  *   dialogue all in one go.</li>
  * </ol>
  *
  * @todo Add functionality fo
  *
  * @author Daniel Solano Gómez
  */
object AlertDialogBuilder {
  /** Creates a new `DialogueFunctor` from a function. */
  private implicit def fnToDialogueFunctor(
    fn: AndroidBuilder => AndroidBuilder
  ) = {
    new DialogueFunctor {
      def apply(b: AndroidBuilder) {
        fn(b)
      }
    }
  }

  /** Creates a new alert dialogue builder.  See the object documentation for
    *  more information on how to use this method.
    *
    * @param context the context for this builder and the `AlertDialog` it
    * creates.  Must not be `null`.
    * @param content the contents of the dialogue.  Please see the
    * documentation for the object about the different ways to specify content
    * @param title an optional title for the dialogue
    * @param positiveButton an optional positive button for the dialogue
    * @param neutralButton an optional neutral button for the dialogue
    * @param negativeButton an optional negative button for the dialogue
    * @param cancellable whether or not the user may cancel the dialogue,
    * defaults to true
    * @param forceInverseBackground whether or not an inverse background
    * should be forced regardless of the content type.  By default,
    * list-type content has an inverse background.  This seems to only have
    * an effect with custom content using a view.
    */
  def apply(
    context: Context,
    content: Content,
    title: Title = NoOp,
    positiveButton: Button = NoButton,
    neutralButton: Button = NoButton,
    negativeButton: Button = NoButton,
    cancellable: Boolean = true,
    forceInverseBackground: Boolean = false,
    onCancel: OnCancelListener = null,
    onItemSelectedListener: OnItemSelectedListener = null
  )(
    implicit factory: BuilderFactory
  ): AlertDialogBuilder = {
    val actions = List[DialogueFunctor](
      content,
      title,
      newButtonFunctor(positiveButton, PositiveButton),
      newButtonFunctor(neutralButton, NeutralButton),
      newButtonFunctor(negativeButton, NegativeButton),
      { b: AndroidBuilder =>
        b.setInverseBackgroundForced(forceInverseBackground)
      },
      { (_: AndroidBuilder).setCancelable(cancellable) },
      { b: AndroidBuilder =>
        if(onCancel != null) b.setOnCancelListener(onCancel) else b
      },
      { b: AndroidBuilder =>
        if(onItemSelectedListener != null) {
          b.setOnItemSelectedListener(onItemSelectedListener)
        } else {
          b
        }
      }
    )
    new AlertDialogBuilder(context, factory, actions)
  }

  /** Handy name for the underlying builder type. */
  type AndroidBuilder = AlertDialog.Builder
  /** Trait for a factory object that creates new `AlertDialog.Builder`
    * objects.  The main purpose of this trait is to be able to inject a mock
    * factory for the purposes of testing.
    */
  type BuilderFactory = Context => AndroidBuilder
  /** Handy name for a function that can be converted into a
    * `DialogInterface.OnCancelListener` */
  type OnCancelFunction = DialogInterface => Unit
  /** Handy name for a function that can be converted into a
    * `DialogInterface.OnClickListener` */
  type OnClickFunction = (DialogInterface, Int) => Unit
  /** Handy name for a function that can be converted into a
    * `DialogInterface.OnMultiChoiceClickListener` */
  type OnMultiChoiceClickFunction = (DialogInterface, Int, Boolean) => Unit

  /** The default `BuilderFactory`, which simply creates a new
    * `AlertDialog.Builder` object using the given context. */
  implicit val defaultFactory = new AndroidBuilder(_: Context)

  /** A generic dialogue functor is an abstraction by which a particular user
    * setting is applied to the dialogue builder. */
  sealed trait DialogueFunctor extends ((AndroidBuilder) => Unit)

  /** The type for all content of an alert dialogue. */
  sealed trait Content extends DialogueFunctor

  /** Generic implementation of having a listener.
    *
    * @tparam ContentType what to return for `setCallback`
    * @tparam ListenerType the type of listener to support */
  sealed trait WithListener[ContentType, ListenerType] {
    /** The listener, if set. */
    private var _listener: Option[ListenerType] = None

    /** Returns either the listener or null. */
    def listener: ListenerType = {
      _listener.getOrElse(null.asInstanceOf[ListenerType])
    }

    /** Sets the listener, if not null. */
    protected def setCallback(l: ListenerType): ContentType = {
      if (l != null) {
        _listener = Some(l)
      }
      this.asInstanceOf[ContentType]
    }
  }

  /** Provides an `onClick` method that can be used to set an `OnClickListener`
    * option.
    *
    * @tparam T a return type for the `onClick` method */
  sealed trait OnClick[T] extends WithListener[T, OnClickListener] {
    /** Sets the listener for the list.  */
    def onClick(l: OnClickListener): T = setCallback(l)
  }

  sealed trait OnMultiChoiceClick[T]
      extends WithListener[T, OnMultiChoiceClickListener] {
    /** Sets the listener for the list.  */
    def onMultiChoiceClick(l: OnMultiChoiceClickListener): T = {
      setCallback(l)
    }
  }

  /** Provides a `withSingleChoice` method that can be used to set some list
    * content into single-choice mode.
    *
    * @tparam T a return type for the `withSingleChoice` method */
  sealed trait SingleChoice[T <: Content] extends OnClick[T] with Content {
    /** If set, the single item that should be checked where -1 signifies that
      * no item will be checked though the list will support a single
      * choice. */
    var _checkedItem: Option[Int] = None

    /** Returns the value of the checked item, if set. */
    protected def checkedItem: Int = _checkedItem.get

    /** Returns true if the single choice has been selected. */
    protected def singleChoiceDefined: Boolean = _checkedItem.isDefined

    /** Specifies which item of the list is checked.  Once this method is
      * called, the list will be in single-choice mode.
      *
      * @param item optional, the item that should be checked.  If -1 or
      * omitted, no item will be checked.
      *
      * @return the object for more building
      */
    def withSingleChoice(item: Int = -1): T = {
      require(
        item >= -1,
        "Checked item must be non-negative or -1 for no checked items")
      _checkedItem = Some(item)
      this.asInstanceOf[T]
    }

    /** Applies the builder using either `defaultApply` or `singleChoiceApply`
      * depending on whether or not single choice mode has been activated. */
    def apply(b: AndroidBuilder) {
      if(_checkedItem.isDefined) singleChoiceApply(b) else defaultApply(b)
    }

    /** Like `apply`, but for the default mode. */
    protected def defaultApply(b: AndroidBuilder)

    /** Like `apply`, but for single-choice mode. */
    protected def singleChoiceApply(b: AndroidBuilder)
  }

  /** Converts a character sequence into content for the dialogue. */
  implicit def charSeqToContent(text: CharSequence): Content = {
    require(text != null, "Message must not be null.")
    new Content {
      def apply(b: AndroidBuilder) {
        b.setMessage(text)
      }
    }
  }

  /** Converts a resource ID into content for the dialogue. */
  implicit def intToContent(id: Int): Content = {
    new Content {
      def apply(b: AndroidBuilder) {
        b.setMessage(id)
      }
    }
  }

  /** Uses the given adapter as the source for a list of items to be
    * displayed as the dialogue content.
    *
    * By default, the dialogue will be dismissed and the listener (if set)
    * will be notified once an item is clicked.  If `withSingleChoiceItem` has
    * been called, then the list will be displayed with check marks next to
    * each item and the dialogue will not be automatically dismissed.
    *
    * @param adapter the adapter to use as the source of the list
    */
  final class AdapterContent(adapter: ListAdapter) extends Content
              with OnClick[AdapterContent]
              with SingleChoice[AdapterContent] {
    require(adapter != null, "Adapter must not be null.")

    def defaultApply(b: AndroidBuilder) {
      b.setAdapter(adapter, listener)
    }

    def singleChoiceApply(b: AndroidBuilder) {
      b.setSingleChoiceItems(adapter, checkedItem, listener)
    }
  }

  /** Converts a `ListAdapter` to a content object. */
  implicit def adapterToContent(adapter: ListAdapter): AdapterContent = {
    new AdapterContent(adapter)
  }

  /** This creates an intermediate object into which a cursor can be stored
    * before it gets a label.  This also causes compilation to fail if a
    * cursor is passed as content without an immediate cann to `withLabel`.
    *
    * @param cursor the cursor to wrap */
  final class UnlabelledCursor(cursor: Cursor) {
    require(cursor != null, "Cursor may not be null.")

    /** Creates a content object for the cursor using the given string as the
      * name of the column for the labels. */
    def withLabel(label: String): DefaultCursorContent = {
      new DefaultCursorContent(cursor, label)
    }
  }

  /** Holds the basic information for concrete cursor content implementations.
    *
    * @param cursor the underlying cursor to use as the source for the list
    * @param labelColumn the name of the column whose text should be used for
    * the items in the list */
  sealed abstract class CursorContent(
    protected val cursor: Cursor,
    protected val labelColumn: String
  ) extends Content {
    require(cursor != null, "Cursor may not be null.")
    require(labelColumn != null, "Label may not be null.")

    /** Essentially, a copy constructor. */
    protected def this(c: CursorContent) = this(c.cursor, c.labelColumn)
  }

  /** Basic class for list content backed by a cursor.  This class supports
    * both the default and single-choice selection methods.
    */
  final class DefaultCursorContent(cursor: Cursor, labelColumn: String)
      extends CursorContent(cursor, labelColumn)
              with SingleChoice[DefaultCursorContent] {

    /** Changes the list to multi-choice mode using the given column name as
      * the source for whether or not an item is checked.
      *
      * @param isCheckedColumn specifies the column name on the cursor to use
      * to determine whether a checkbox is checked or not. It must return an
      * integer value where 1 means checked and 0 means unchecked.
      */
    def withMultipleChoices(
      isCheckedColumn: String
    ): MultipleChoiceCursorContent = {
      new MultipleChoiceCursorContent(this, isCheckedColumn)
    }

    def defaultApply(b: AndroidBuilder) {
      b.setCursor(cursor, listener, labelColumn)
    }

    def singleChoiceApply(b: AndroidBuilder) {
      b.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener)
    }
  }

  /** Implements the multiple-choice mode for a list backed by a cursor.
    *
    * @param content an instance of cursor content used to get the cursor and
    * label string
    * @param isCheckedColumn the column name on the cursor used to determine
    * whether a checkbox is checked or not.
    */
  final class MultipleChoiceCursorContent(
    content: CursorContent,
    isCheckedColumn: String
  ) extends CursorContent(content) {
    /** The optional listener for multi-choice clicks. */
    private var listener: Option[OnMultiChoiceClickListener] = None

    require(isCheckedColumn != null)

    /** Sets the listener for multi-choice clicks. */
    def onMultiChoiceClick(
      l: OnMultiChoiceClickListener
    ):  MultipleChoiceCursorContent = {
      if(l != null) {
        listener = Some(l)
      }
      this
    }

    def apply(b: AndroidBuilder) {
      b.setMultiChoiceItems(
        cursor,
        isCheckedColumn,
        labelColumn,
        listener.orNull)
    }
  }

  /** Converts a `Cursor` to a content object. */
  implicit def cursorToContent(cursor: Cursor): UnlabelledCursor = {
    new UnlabelledCursor(cursor)
  }

  /** Parent class to all array-based list-type content.
    *
    * @tparam T the type of the underlying source for the array
    *
    * @param source the underlying source of the array */
  sealed abstract class ArrayContent[T](protected val source: T)
      extends Content {
    /** A basic copy constructor. */
    def this(content: ArrayContent[T]) = this(content.source)
  }

  /** Parent class for array content that is either in default choice or
    * multiple choice mode.
    *
    * @tparam T the type of the underlying source for the array
    *
    * @param source the underlying source of the array */
  sealed abstract class DefaultArrayContent[T](source: T)
      extends ArrayContent[T](source)
              with SingleChoice[DefaultArrayContent[T]] {
    /** Allows using the array in multiple-choice mode.
      *
      * @param choices specifies which items are checked.  If omitted then no
      * items will be checked.  Otherwise, must be the same length as the
      * array of items.
      *
      * @return a multiple-choice version of this array content */
    def withMultipleChoices(
      choices: Traversable[Boolean] = null
    ): MultipleChoiceArrayContent[T] = {
      toMultipleChoice(if(choices == null) None else Some(choices.toArray))
    }

    /** Returns a multiple-choice version of this content with the given
      * choice selections. */
    def toMultipleChoice(
      choices: Option[Array[Boolean]]
    ): MultipleChoiceArrayContent[T]
  }

  /** A multiple-choice version of array content.
    *
    * @constructor Generates a multiple-choice version of the given array
    * content.
    *
    * @param content the content upon which the new object is based
    * @param choices an optional set of checked items in the list
    */
  sealed abstract class MultipleChoiceArrayContent[T](
    content: ArrayContent[T],
    choices: Option[Array[Boolean]]
  ) extends ArrayContent[T](content)
            with  OnMultiChoiceClick[MultipleChoiceArrayContent[T]] {
    /** Returns the array of checked items for the list. */
    protected def checkedItems = choices.orNull
  }

  /** Array content based on the given array resource ID. */
  final class ArrayResourceContent(id: Int) extends DefaultArrayContent(id) {
    protected def defaultApply(b: AndroidBuilder) {
      b.setItems(id, listener)
    }

    protected def singleChoiceApply(b: AndroidBuilder) {
      b.setSingleChoiceItems(id, checkedItem, listener)
    }

    def toMultipleChoice(
      choices: Option[Array[Boolean]]
    ): MultipleChoiceArrayContent[Int] = {
      new MultipleChoiceArrayResourceContent(this, choices)
    }
  }

  /** Multiple-choice array content based on an array resource ID. */
  final class MultipleChoiceArrayResourceContent(
    content: ArrayResourceContent,
    choices: Option[Array[Boolean]]
  ) extends MultipleChoiceArrayContent(content, choices) {
    def apply(b: AndroidBuilder) {
      b.setMultiChoiceItems(source, checkedItems, listener)
    }
  }

  /** Array content based on a raw character sequence array. */
  final class CharSeqArrayContent(items: Array[CharSequence])
      extends  DefaultArrayContent(items) {
    require(items != null, "Items array cannot be null.")

    protected def defaultApply(b: AndroidBuilder) {
      b.setItems(source, listener)
    }

    protected def singleChoiceApply(b: AndroidBuilder) {
      b.setSingleChoiceItems(source, checkedItem, listener)
    }

    def toMultipleChoice(
      choices: Option[Array[Boolean]]
    ): MultipleChoiceArrayContent[Array[CharSequence]] = {
      new MultipleChoiceCharSeqArrayContent(this, choices)
    }
  }

  /** Multiple-choice array content based on a raw array. */
  final class MultipleChoiceCharSeqArrayContent(
    content: CharSeqArrayContent,
    choices: Option[Array[Boolean]]
  ) extends MultipleChoiceArrayContent(content, choices) {
    require(
      choices == None || choices.get.length == source.length,
      "Item array and multiple choice array have differing numbers of items.")

    def apply(b: AndroidBuilder) {
      b.setMultiChoiceItems(source, checkedItems, listener)
    }
  }

  /** Creates content base on a character sequence array.  The array is used
    * directly.  */
  implicit def charSeqArrayToContent[T <: CharSequence](
    items: Array[T]
  ):  CharSeqArrayContent = {
    new CharSeqArrayContent(items.asInstanceOf[Array[CharSequence]])
  }

  /** Creates content from general arrays that are not character sequences.
    * Calls `toString` on each element and creates an array from the result. */
  implicit def genericArrayToContent[T](
    items: Array[T]
  ):  CharSeqArrayContent = {
    collectionToContent(items)
  }

  /** Creates content from collections in general.  Calls `toString` on each
    * element and creates an array from the result. */
  implicit def collectionToContent[T](
    items: Traversable[T]
  ): CharSeqArrayContent = {
    require(items != null, "Cannot use a null reference as list content.")
    val itemArray = new Array[CharSequence](items.size)
    items.map(_.toString).copyToArray(itemArray)
    new CharSeqArrayContent(itemArray)
  }

  /** The master trait for any title for an alert dialogue. */
  sealed trait Title extends DialogueFunctor

  /** Converts the given view into a custom title for the dialogue.  This
    * type of title does not support an icon. */
  implicit def viewToCustomTitle(view: View): Title = {
    require(view != null, "Custom title view must not be null.")

    new Title {
      override def apply(builder: AndroidBuilder) {
        builder.setCustomTitle(view)
      }
    }
  }

  /** A regular title consists of a message and an optional icon. */
  sealed class RegularTitle extends Title {title =>
    /** An optional icon that will be part of the title. */
    private[this] var icon: Option[Icon] = None

    /** Adds the icon to the title. */
    final def withIcon(icon: Icon): RegularTitle = {
      require(icon != null)
      title.icon = Some(icon)
      this
    }

    /** Adds the icon to the title, if available. */
    def apply(builder: AndroidBuilder) {
      icon foreach (_(builder))
    }
  }

  /** Generic resource content that defaults to being a message, but can be
    * converted as a list as well. */
  final class ResourceContent(val id: Int) extends RegularTitle {
    /** Changes this content type from being interpreted as a message to
      * being interpreted as a list. */
    def asList: ArrayResourceContent = new ArrayResourceContent(id)

    override def apply(builder: AndroidBuilder) {
      builder.setTitle(id)
      super.apply(builder)
    }
  }

  /** Converts an integer, presumably a resource ID, into content that is
    * interpreted as a regular title, but can also be turned into an array. */
  implicit def idToResourceContent(id: Int): ResourceContent = {
    new ResourceContent(id)
  }

  /** Converts a character sequence into the message portion of a regular
    * title. */
  implicit def textToRegularTitle(text: CharSequence): RegularTitle = {
    require(text != null, "Title string must not be null")

    new RegularTitle {
      override def apply(builder: AndroidBuilder) {
        builder.setTitle(text)
        super.apply(builder)
      }
    }
  }

  /** The master trait for all of the icon types. */
  sealed trait Icon extends DialogueFunctor

  /** Allows conversion of a drawable into an appropriate `Icon`. */
  implicit def drawableToIcon(d: Drawable): Icon = {
    require(d != null, "Icon drawable must not be null.")

    new Icon {
      override def apply(builder: AndroidBuilder) {
        builder.setIcon(d)
      }
    }
  }

  /** Allows conversion of a resource ID into an appropriate `Icon`. */
  implicit def resourceIdToIcon(id: Int): Icon = {
    new Icon {
      override def apply(builder: AndroidBuilder) {
        builder.setIcon(id)
      }
    }
  }

  /** Abstract superclass to all button types that adds support for an
    * optional on-click listener.
    *
    * @tparam T used as return type for `onClick` to ensure type checking works
    * out
    */
  sealed class Button(val message: Option[Any]) extends OnClick[Button] {
    require(
      message match {
        case Some(_: Int)          => true
        case Some(_: CharSequence) => true
        case None                  => true
        case _                     => false
      }, "Button message be an Int or a CharSequence.")

    /** Adds the listener to the button. */
    override def onClick(l: OnClickListener): Button = {
      require(message.isDefined, "Cannot set a listener without a message")
      super.onClick(l)
    }
  }

  /** Creates a button from a resource ID. */
  implicit def resourceIdToButton(id: Int): Button = new Button(Some(id))

  /** Creates a button from a character sequence. */
  implicit def charSeqToButton(text: CharSequence): Button = {
    new Button(Some(text))
  }

  /** An object for no button. */
  object NoButton extends Button(None)

  /** An enumeration of all the button types. */
  private sealed trait ButtonType
  private case object PositiveButton extends ButtonType
  private case object NeutralButton extends ButtonType
  private case object NegativeButton extends ButtonType

  /** Creates a new functor for the given button and type. */
  private def newButtonFunctor(
    button: Button,
    buttonType: ButtonType
  ): DialogueFunctor = {
    button.message map {
      val listener = button.listener
      _ match {
        case id: Int => {
          new ResourceButtonFunctor(id, listener, buttonType)
        }
        case text: CharSequence => {
          new CharSeqButtonFunctor(text, listener, buttonType)
        }
        case _ => NoOp
      }
    } getOrElse NoOp
  }

  /** Applies a text-based button to a builder. */
  private class CharSeqButtonFunctor(
    text: CharSequence,
    listener: OnClickListener,
    buttonType: ButtonType
  ) extends DialogueFunctor {

    def apply(b: AndroidBuilder) {
      buttonType match {
        case PositiveButton => b.setPositiveButton(text, listener)
        case NeutralButton  => b.setNeutralButton(text, listener)
        case NegativeButton => b.setNegativeButton(text, listener)
      }
    }
  }

  /** Applies a resource ID-based button to a builder. */
  private class ResourceButtonFunctor(
    id: Int,
    listener: OnClickListener,
    buttonType: ButtonType
  ) extends DialogueFunctor {

    def apply(b: AndroidBuilder) {
      buttonType match {
        case PositiveButton => b.setPositiveButton(id, listener)
        case NeutralButton  => b.setNeutralButton(id, listener)
        case NegativeButton => b.setNegativeButton(id, listener)
      }
    }
  }

  /** A handy trait that defines a no-operation behaviour. */
  trait NoOp extends DialogueFunctor {
    def apply(b: AndroidBuilder) {}
  }

  /** A sensible default that does nothing. */
  object NoOp extends Title with NoOp

  /** Converts an `OnCancelFunction` to an `OnCancelListener`. */
  implicit def fnToOnCancelListener(fn: OnCancelFunction): OnCancelListener = {
    whenNotNull(fn) {() =>
      new OnCancelListener {
        def onCancel(d: DialogInterface) {
          fn(d)
        }
      }
    }
  }

  /** Converts an `OnClickFunction` to an `OnClickListener`. */
  implicit def fnToOnClickListener(fn: OnClickFunction): OnClickListener = {
    whenNotNull(fn) {() =>
      new DialogInterface.OnClickListener {
        def onClick(dialogue: DialogInterface, button: Int) {
          fn(dialogue, button)
        }
      }
    }
  }

  /** Converts an `OnMultiChoiceClickFunction` to an `OnMultiChoiceClickListener`. */
  implicit def fnToOnMultiChoiceClickListener(
    fn: OnMultiChoiceClickFunction
  ):  OnMultiChoiceClickListener = {
    whenNotNull(fn) {() =>
      new OnMultiChoiceClickListener {
        def onClick(p1: DialogInterface, p2: Int, p3: Boolean) {
          fn(p1, p2, p3)
        }
      }
    }
  }

  /** When the first argument is not null, evaluates the thunk. */
  private def whenNotNull[T, U](t: T)(thunk: () => U): U = {
    if (t != null) thunk() else null.asInstanceOf[U]
  }
}
