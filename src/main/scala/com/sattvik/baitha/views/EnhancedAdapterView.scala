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

import android.view.View
import android.widget.AdapterView

/** Adds a couple of convenience methods for setting call-backs using
  * functions.
  *
  * @author Daniel Solano Gómez */
class EnhancedAdapterView(adapterView: AdapterView[_]) {
  /** Sets the item click listener on an adapter view by wrapping the
    * function into an `AdapterView.OnItemClickListener`.
    *
    * @param f the function to wrap into the listener object.  It takes
    *          four arguments: 1) the adapter view where the click happened,
    *          2) the view within the adapter view that was clicked,
    *          3) the position of the view in the adapter, and 4) the row ID
    *          of the item that was clicked
    */
  def onItemClick(f: (AdapterView[_], View, Int, Long) => Unit) {
    adapterView.setOnItemClickListener(
      new AdapterView.OnItemClickListener {
        def onItemClick(
          parent: AdapterView[_],
          view: View,
          pos: Int,
          id: Long
        ) {
          f(parent, view, pos, id)
        }
      })
  }

  /** Sets the item long click listener on an adapter view by wrapping the
    * function into an `AdapterView.OnItemLongClickListener`.
    *
    * @param f the function to wrap into the listener object.  It takes
    *          four arguments: 1) the adapter view where the long click
    *          happened, 2) the view within the adapter view that was long
    *          clicked, 3) the position of the view in the adapter, and 4)
    *          the row ID of the item that was long clicked.  The function
    *          should evaluate to true if it consumed the long click.
    */
  def onItemLongClick(f: (AdapterView[_], View, Int, Long) => Boolean) {
    adapterView.setOnItemLongClickListener(
      new AdapterView.OnItemLongClickListener {
        def onItemLongClick(
          parent: AdapterView[_],
          view: View,
          pos: Int,
          id: Long
        ) = {
          f(parent, view, pos, id)
        }
      })
  }
}
