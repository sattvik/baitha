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

import android.view.View
import android.widget._
import com.sattvik.baitha.views._

/** The EnhancedViews trait adds an implicit conversion that makes working with
  * Views a bit easier.
  *
  * @author Daniel Solano Gómez */
trait EnhancedViews {
  import EnhancedViews._

  implicit def enhanceView(view: View): EnhancedView = {
    new EnhancedView(view)
  }

  implicit def enhanceRadioGroup(radioGroup: RadioGroup): EnhancedRadioGroup = {
    new EnhancedRadioGroup(radioGroup)
  }

  implicit def enhanceAdapterView(v: AdapterView[_]): EnhancedAdapterView = {
    new EnhancedAdapterView(v)
  }

  implicit def enhanceSeekBar(v: SeekBar): EnhancedSeekBar = {
    new EnhancedSeekBar(v)
  }
}

/** The companion object to the EnhancedViews trait.  This allows declaration
  * of the EnhancedView class without an implicit reference to an instance of
  * a class that has the EnhancedViews trait.
  *
  * @author Daniel Solano Gómez */
object EnhancedViews extends EnhancedViews
