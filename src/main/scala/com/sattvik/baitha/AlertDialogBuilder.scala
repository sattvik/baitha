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

import android.app.AlertDialog
import android.content.{DialogInterface, Context}
import android.content.DialogInterface.OnClickListener
import android.graphics.drawable.Drawable
import android.view.View
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
  actions: List[DialogueFunctor],
  cancellable: Boolean
) {

  /** The underlying builder. */
  private val builder = factory(context)

  require(context != null, "Dialog context must not be null.")
  // apply all of the actions to the builder
  actions foreach (_(builder))
  builder.setCancelable(cancellable)

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
  * `DialogInterface.OnMultiClickListener`, which is set using `onMultiClick`.
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
    */
  def apply(
    context: Context,
    content: Content,
    title: Title = NoOp,
    positiveButton: Button = NoButton,
    neutralButton: Button = NoButton,
    negativeButton: Button = NoButton,
    cancellable: Boolean = true
  )(
    implicit factory: BuilderFactory
  ): AlertDialogBuilder = {
    val actions = List(
      content,
      title,
      newButtonFunctor(positiveButton, PositiveButton),
      newButtonFunctor(neutralButton, NeutralButton),
      newButtonFunctor(negativeButton, NegativeButton)
    )
    new AlertDialogBuilder(context, factory, actions, cancellable)
  }

  /** Handy name for the underlying builder type. */
  type AndroidBuilder = AlertDialog.Builder
  /** Trait for a factory object that creates new `AlertDialog.Builder`
    * objects.  The main purpose of this trait is to be able to inject a mock
    * factory for the purposes of testing.
    */
  type BuilderFactory = Context => AndroidBuilder
  /** Handy name for a function that can be converted into a
    * `DialogInterface.OnClickListener` */
  type OnClickFunction = (DialogInterface, Int) => Unit

  /** The default `BuilderFactory`, which simply creates a new
    * `AlertDialog.Builder` object using the given context. */
  implicit val defaultFactory = new AndroidBuilder(_: Context)

  /** A generic dialogue functor is an abstraction by which a particular user
    * setting is applied to the dialogue builder. */
  sealed trait DialogueFunctor extends ((AndroidBuilder) => Unit)

  /** The type for all content of an alert dialogue. */
  sealed trait Content extends DialogueFunctor

  /** Provides an `onClick` method that can be used to set an `OnClickListener`
    * option.
    *
    * @tparam T a return type for the `onClick` method */
  sealed trait OnClick[T] {
    /** The listener, if set. */
    private var _listener: Option[OnClickListener] = None

    /** Returns either the listener or null. */
    def listener: OnClickListener = _listener.orNull

    /** Sets the listener for the list.  */
    def onClick(l: OnClickListener): T = {
      if (l != null) {
        _listener = Some(l)
      }
      this.asInstanceOf[T]
    }
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
  final class AdapterContent(adapter: ListAdapter)
      extends Content with OnClick[AdapterContent] {
    /** If set, the single item that should be checked where -1 signifies that
      * no item will be checked though the list will support a single
      * choice. */
    var checkedItem: Option[Int] = None

    require(adapter != null, "Adapter must not be null.")

    /** Specifies which item of the list is checked.  Once this method is
      * called, the
      *
      * @param item optional, the item that should be checked.  If -1 or
      * omitted, no item will be checked.
      *
      * @return the object for more building
      */
    def withSingleChoice(item: Int = -1): AdapterContent = {
      require(
        item >= -1,
        "Checked item must be non-negative or -1 for no checked items")
      checkedItem = Some(item)
      this
    }

    /** Applies the adapter to the underlying Android builder. */
    def apply(b: AndroidBuilder) {
      if (checkedItem.isDefined) {
        b.setSingleChoiceItems(adapter, checkedItem.get, listener)
      } else {
        b.setAdapter(adapter, listener)
      }
    }
  }

  /** Converts a `ListAdapter` to a content object. */
  implicit def adapterToContent(adapter: ListAdapter): AdapterContent = {
    new AdapterContent(adapter)
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

  /** Converts an integer, presumably a resource ID, into the message portion
    * of a regular title. */
  implicit def idToRegularTitle(id: Int): RegularTitle = {
    new RegularTitle {
      override def apply(builder: AndroidBuilder) {
        builder.setTitle(id)
        super.apply(builder)
      }
    }
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

  /** Converts an `OnClickFunction` to an `OnClickListener`. */
  implicit def fnToOnClickListener(fn: OnClickFunction): OnClickListener = {
    whenNotNull(fn) {() =>
      new DialogInterface.OnClickListener {
        def onClick(dialog: DialogInterface, button: Int) {
          fn(dialog, button)
        }
      }
    }
  }

  /** When the first argument is not null, evaluates the thunk. */
  private def whenNotNull[T, U](t: T)(thunk: () => U): U = {
    if (t != null) thunk() else null.asInstanceOf[U]
  }
}

///** A Scala-friendly version of Android's `AlertDialog.Builder`.  For
//  * example, instead of using JavaBean-style mutators, it uses Scala-style
//  *  of
//  *  taking */
//class AlertDialogBuilder(
//  private val context: Context,
//  content: Content,
//  title: Title = null,
//  builderFactory: BuilderFactory = DefaultBuilderFactory
//) {
//  //    negativeButton: DialogButton = null,
//  //    neutralButton: DialogButton = null,
//  //    positiveButton: DialogButton = null,
//  //    cancellable: Boolean = true,
//  //    inverseBackgroundForced: Boolean = false) {)
//  require(context != null)
//  //  require(content != null)
//  val builder = builderFactory.createBuilder(context)
//  //  builder.setCancelable(cancellable)
//  //  builder.setInverseBackgroundForced(inverseBackgroundForced)
//  applyComponent(title)
//  applyComponent(content)
//
//  //  applyComponent(content)
//  //  applyComponent(icon)
//  //  applyButton(negativeButton, NegativeButton)
//  //  applyButton(neutralButton, NeutralButton)
//  //  applyButton(positiveButton, PositiveButton)
//  /** This method exists primarily as a means to inject a mock builder for the
//    * sake of testing. */
//  protected def newBuilder(context: Context): Builder = new Builder(context)
//
//  private def applyComponent(component: DialogComponent) {
//    if (component != null) {
//      component(builder)
//    }
//  }
//
//  private def applyButton(button: DialogButton, which: WhichButton) {
//    if (button != null) {
//      button.message match {
//        case id: Int           => which match {
//          case NegativeButton => {
//            builder.setNegativeButton(id, button.listener)
//          }
//          case NeutralButton  => {
//            builder.setNeutralButton(id, button.listener)
//          }
//          case PositiveButton => {
//            builder.setPositiveButton(id, button.listener)
//          }
//        }
//        case str: CharSequence => which match {
//          case NegativeButton => {
//            builder.setNegativeButton(str, button.listener)
//          }
//          case NeutralButton  => {
//            builder.setNeutralButton(str, button.listener)
//          }
//          case PositiveButton => {
//            builder.setPositiveButton(str, button.listener)
//          }
//        }
//      }
//    }
//  }
//
//  //  content(builder)
//  //  builder.setCancelable(cancellable)
//
//  //  def adapter_=(adapter: ListAdapter, listener: OnClickFunction) = {
//  //    builder.setAdapter(adapter, listener)
//  //  }
//  //
//  //  def withAdapter(adapter: ListAdapter, listener: OnClickFunction) = {
//  //    builder.setAdapter(adapter, listener)
//  //    this
//  //  }
//}
//
//object AlertDialogBuilder {
//  /** Handy alias for a function that can serve as a
//    * `DialogInterface.OnCancelListener` callback.
//    *
//    * The first parameter is a reference to the interface that was clicked. */
//  type OnCancel = DialogInterface => Unit
//
//  /** Handy alias for a function that can serve as a
//    * `DialogInterface.OnClickListener` callback.
//    *
//    * The first parameter is a reference to the interface that was clicked,
//    * and the second is either the ID of the button that was clicked or the
//    * position of the item that was clicked. */
//  type OnClick = (DialogInterface, Int) => Unit
//
//  /** Handy alias for a function that can serve as a
//    * `DialogInterface.OnMultiChoiceClickListener` callback.
//    *
//    * The first parameter is a reference to the interface that was clicked,
//    * and the second is the position of the item that was clicked, and the
//    * third is whether the click check the item or not. */
//  type OnMultiChoiceClick = (DialogInterface, Int, Boolean) => Unit
//
//  /** Conversion of a `OnClickFunction` to the appropriate listener instance.
//    * If the function is `null`, returns `null`. */
//  implicit def fnToOnClickListener(f: OnClick): OnClickListener = {
//    if (f != null) {
//      new DialogInterface.OnClickListener {
//        def onClick(dialog: DialogInterface, button: Int) {
//          f(dialog, button)
//        }
//      }
//    } else {
//      null
//    }
//  }
//
//  //  private def toOnClickListener(handler: SingleClickHandler) = {
//  //    new DialogInterface.OnClickListener {
//  //      def onClick(dialog: DialogInterface, button: Int) {
//  //        handler(dialog, button)
//  //      }
//  //    }
//  //  }
//  //  implicit def fnToOnCancelClickLister(f: OnCancel): OnCancelListener = {
//  //    foo(f) {
//  //      new OnCancelListener {
//  //        def onCancel(dialog: DialogInterface) {
//  //          f(dialog)
//  //        }
//  //      }
//  //    }
//  //  }
//  /** Conversion of a `OnMultiChoiceClickFunction` to the appropriate listener
//    * instance.  If the function is `null`, returns `null`. */
//  implicit def fnToOnMultiChoiceClickListener(f: OnMultiChoiceClick): OnMultiChoiceClickListener = {
//    if (f != null) {
//      new DialogInterface.OnMultiChoiceClickListener {
//        def onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
//          f(dialog, which, isChecked)
//        }
//      }
//    } else {
//      null
//    }
//  }
//
////  implicit def resourceIdToContent(id: Int)
//
//  //  sealed trait ListContent extends Content {
//  //    protected var handler: Option[ClickHandler] = None
//  //
//  //    def withListener(listener: ClickHandler) {
//  //      require(listener != null)
//  //      this.handler = Some(listener)
//  //    }
//  //  }
//  //
//  //  trait SingleChoice extends ListContent {
//  //    var choice: Option[Int] = None
//  //
//  //    final def withCheckedItem(item: Int) {
//  //      require(
//  //        item >= -1,
//  //        "Checked item must be -1 for no checked item or a valied item index")
//  //      choice = Some(item)
//  //    }
//  //  }
//  //
//  //  sealed trait SingleChoiceOnly extends ListContent with SingleChoice {
//  //    abstract override def withListener(listener: ClickHandler) {
//  //      listener match {
//  //        case _: SingleClickHandler => super.withListener(listener)
//  //        case _                     => {
//  //          require(false, "Listener must be a SingleClickListener")
//  //        }
//  //      }
//  //    }
//  //  }
//  //
//  //
//  //  implicit def adapterToContent(adapter: ListAdapter): AdapterContent = {
//  //    new AdapterContent(adapter)
//  //  }
//  //
//  //  final class AdapterContent(adapter: ListAdapter)
//  //      extends ListContent with SingleChoiceOnly {
//  //    require(adapter != null, "List adapter must not be null")
//  //
//  //    def apply(builder: Builder) {
//  //      val listener = (handler map {h =>
//  //        toOnClickListener(h.asInstanceOf[SingleClickHandler])
//  //      }).orNull
//  //      if (choice.isDefined) {
//  //        builder.setSingleChoiceItems(
//  //          adapter,
//  //          choice.get,
//  //          listener)
//  //      } else {
//  //        builder.setAdapter(adapter, listener)
//  //      }
//  //    }
//  //  }
//  //  /** Provides implicit conversions of raw character sequences and resource
//  //    * IDs to the appropriate content type.
//  //    */
//  //  object Content {
//  //    /** Converts a string or other character sequence to message content for
//  //      * the dialogue.
//  //      */
//  //    implicit def charSeqToContent(charSeq: CharSequence): Content = {
//  //      CharSeqMessageContent(charSeq)
//  //    }
//  //
//  //    /** Converts a raw resource ID, e.g. `R.string.my_title`, to a message
//  //      * content object.
//  //      */
//  //    implicit def idToContent(id: Int): Content = ResourceMessageContent(id)
//  //
//  //    /** Converts a view to a custom view content object. */
//  //    implicit def viewToContent(v: View): Content = CustomViewContent(v)
//  //  }
//  //
//  //  /** Set a list of items, which are supplied by the given `ListAdapter`, to be
//  //    * displayed in the dialog as the content, you will be notified of the
//  //    * selected item via the supplied listener.
//  //    *
//  //    * @param adapter supplies the list of items
//  //    * @param listener called when an item is clicked.  If not `null`,
//  //    * the dialogue will be dismissed automatically. */
//  //  case class AdapterContent(adapter: ListAdapter, listener: OnClick = null)
//  //      extends Content {
//  //    require(adapter != null)
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setAdapter(adapter, listener)
//  //    }
//  //  }
//  //
//  //  /** Set a list of items, which are supplied by the given `Cursor`, to be
//  //    * displayed in the dialog as the content, you will be notified of the
//  //    * selected item via the supplied listener.
//  //    *
//  //    * @param cursor supplies the list of items
//  //    * @param labelColumn The column name on the cursor containing the string
//  //    * to display in the label.
//  //    * @param listener called when an item is clicked.  If not `null`,
//  //    * the dialogue will be dismissed automatically. */
//  //  case class CursorContent(
//  //      cursor: Cursor,
//  //      labelColumn: String,
//  //      listener: OnClick = null) extends Content {
//  //    def apply(builder: Builder) {
//  //      builder.setCursor(cursor, listener, labelColumn)
//  //    }
//  //  }
//  //
//  //  /** Displays a list of items as the dialogue content.  You will be notified
//  //    * of the selected item via the supplied listener.  This should be an array
//  //    * type, i.e. `R.array.foo`.
//  //    *
//  //    * @param id the resource ID of an array
//  //    * @param listener called when an item is clicked.  If not `null`,
//  //    * the dialogue will be dismissed automatically. */
//  //  case class ArrayResourceContent(id: Int, listener: OnClick = null)
//  //      extends Content {
//  //    def apply(builder: Builder) {
//  //      builder.setItems(id, listener)
//  //    }
//  //  }
//  //
//  //  /** Displays a list of items as the dialogue content.  You will be notified
//  //    * of the selected item via the supplied listener.
//  //    *
//  //    * @param items the text of the items to be displayed in the list
//  //    * @param listener called when an item is clicked.  If not `null`,
//  //    * the dialogue will be dismissed automatically. */
//  //  case class ArrayContent(
//  //      items: Array[CharSequence],
//  //      listener: OnClick = null) extends Content {
//  //    require(items != null)
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setItems(items, listener)
//  //    }
//  //  }
//  //
//  //  /** Uses the given message as the dialogue content. */
//  //  case class CharSeqMessageContent(message: CharSequence) extends Content {
//  //    require(message != null)
//  //
//  //    override def apply(builder: Builder) {
//  //      builder.setMessage(message)
//  //    }
//  //  }
//  //
//  //  /** Uses the resource ID as the message to display. */
//  //  case class ResourceMessageContent(message: Int) extends Content {
//  //    override def apply(builder: Builder) {
//  //      builder.setMessage(message)
//  //    }
//  //  }
//  //
//  //  /** Displays a list of items as the dialogue content.  You will be notified
//  //    * of the selected item via the supplied listener.  The list will have a
//  //    * check mark displayed to the right of th text for each check item.
//  //    * Clicking on an item in the list will not dismiss the dialogue.
//  //    *
//  //    * @param items the text of the items to be display in the list
//  //    * @param checkedItems specifies which items are checked.  If `null`,
//  //    * no items are checked; otherwise, this mush have the same length as the
//  //    * array of items.
//  //    * @param listener called when an item is clicked.  Note that the dialogue
//  //    * will not be dismissed automatically. */
//  //  case class MultiChoiceArrayContent(
//  //      items: Array[CharSequence],
//  //      checkedItems: Array[Boolean] = null,
//  //      listener: OnMultiChoiceClick = null) extends Content {
//  //    require(items != null)
//  //    require(checkedItems == null || items.length == checkedItems.length)
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setMultiChoiceItems(items, checkedItems, listener)
//  //    }
//  //  }
//  //
//  //  /** Displays a list of items as the dialogue content.  You will be notified
//  //    * of the selected item via the supplied listener.  The list will have a
//  //    * check mark displayed to the right of th text for each check item.
//  //    * Clicking on an item in the list will not dismiss the dialogue.
//  //    *
//  //    * @param cursor the cursor used to provide the items
//  //    * @param isChecked specifies the column name on the cursor to use to
//  //    * determine whether a checkbox is checked or not. It must return an integer
//  //    * value where 1 means checked and 0 means unchecked.
//  //    * @param labelColumn the column name on the cursor containing the string
//  //    * to display in the label.
//  //    * @param listener called when an item is clicked.  Note that the dialogue
//  //    * will not be dismissed automatically. */
//  //  case class MultiChoiceCursorContent(
//  //      cursor: Cursor,
//  //      isCheckColumn: String,
//  //      labelColumn: String,
//  //      listener: OnMultiChoiceClick = null) extends Content {
//  //    require(cursor != null)
//  //    require(isCheckColumn != null)
//  //    require(labelColumn != null)
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setMultiChoiceItems(cursor, isCheckColumn, labelColumn, listener)
//  //    }
//  //  }
//  //
//  //  /** Displays a list of items as the dialogue content.  You will be notified
//  //    * of the selected item via the supplied listener.  The list will have a
//  //    * check mark displayed to the right of th text for each check item.
//  //    * Clicking on an item in the list will not dismiss the dialogue.
//  //    *
//  //    * @param items the resource ID of an array, i.e. `R.array.foo`
//  //    * @param checkedItems specifies which items are checked.  If `null`,
//  //    * no items are checked; otherwise, this mush have the same length as the
//  //    * array of items.
//  //    * @param listener called when an item is clicked.  Note that the dialogue
//  //    * will not be dismissed automatically. */
//  //  case class MultiChoiceArrayResourceContent(
//  //      id: Int,
//  //      checkedItems: Array[Boolean] = null,
//  //      listener: OnMultiChoiceClick = null) extends Content {
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setMultiChoiceItems(id, checkedItems, listener)
//  //    }
//  //  }
//  //
//  //  /** Sets the dialog content to be a custom view.  If the view is an instance
//  //    * of a `ListView` the light background will be used.
//  //    */
//  //  case class CustomViewContent(view: View) extends Content {
//  //    require(view != null)
//  //
//  //    def apply(builder: Builder) {
//  //      builder.setView(view)
//  //    }
//  //  }
//  /** Provides information for a dialog button.
//    *
//    * @param message must either be an integer resource ID or a character
//    * string name
//    * @param listener an optional listener for click events originating from
//    * the button */
//  final case class DialogButton(message: Any, listener: OnClick = null) {
//    // message must be either an Int or a CharSequence
//    require(
//      message match {
//        case _: Int          => true
//        case _: CharSequence => true
//        case _               => false
//      }, "message must be either an Int resource ID or a CharSequence")
//  }
//
//  /** Companion object that provides some implicit conversions. */
//  object DialogButton {
//    /** Converts an integer to a resource ID-based dialog button. */
//    implicit def intToDialogButton(i: Int): DialogButton = DialogButton(i)
//
//    /** Converts a character sequence to a dialog button. */
//    implicit def charSeqToDialogButton(cs: CharSequence): DialogButton = {
//      DialogButton(cs)
//    }
//  }
//
//  /** Master trait for all the different button types. */
//  private sealed trait WhichButton
//  private case object NegativeButton extends WhichButton
//  private case object NeutralButton extends WhichButton
//  private case object PositiveButton extends WhichButton
//
//  trait BuilderFactory {
//    def createBuilder(context: Context): AlertDialog.Builder
//  }
//
//  object DefaultBuilderFactory extends BuilderFactory {
//    def createBuilder(context: Context) = new AlertDialog.Builder(context)
//  }
//}