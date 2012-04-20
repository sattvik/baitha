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

/** The EnhancedView class decorates the View class by adding a number of
  * methods that make it easier to work with views.
  *
  * @author Daniel Solano Gómez */
class EnhancedView(view: View) {
  /** Sets the OnClickListener for the View to the given anonymous function. */
  def onClick(f: View => Unit) {
    view.setOnClickListener(
      new View.OnClickListener {
        override def onClick(v: View) {
          f(v)
        }
      })
  }
}
