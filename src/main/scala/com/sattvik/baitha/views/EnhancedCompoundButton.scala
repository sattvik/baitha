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

import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener

/** A wrapper that adds a convenience method for using a function to handle
  * OnCheckChanged events.
  *
  * @param button the button on which to set the listener
  *
  * @author Daniel Solano Gómez */
class EnhancedCompoundButton(button: CompoundButton) {
  /** Calls the given function when the button's checked state changes.
    *
    * @param f this function takes two arguments:
    *          1. The compound button whose state has changed
    *          2. The new checked state for the button
    */
  def onCheckChange(f: (CompoundButton, Boolean) => Unit) {
    button.setOnCheckedChangeListener(
      new OnCheckedChangeListener {
        def onCheckedChanged(source: CompoundButton, checked: Boolean) {
          f(source, checked)
        }
      }
    )
  }
}
