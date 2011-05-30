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

import org.scalatest.WordSpec
import org.mockito.Mockito._
import android.view.View
import org.mockito.Matchers.any
import org.scalatest.matchers.ShouldMatchers
import com.sattvik.baitha.{EnhancedViews, EnhancedView}

class EnhancedViewsSuite extends WordSpec with ShouldMatchers {
  "An ExtendedView" should {
    "provide an onClick method" in {
      // view to mock
      val mockedView = mock(classOf[View])

      // set the listener using the onClick method
      val enhancedView = new EnhancedView(mockedView)
      enhancedView onClick {_ =>}

      // verify the listener was set
      verify(mockedView).setOnClickListener(any(classOf[View.OnClickListener]))
    }
  }

  "The ExtendedViews trait" should {
    "provide implicit conversions" in {
      val mockedView = mock(classOf[View])
      val traitTester = new EnhancedViews {
        def test(v: View) {
          v onClick {_ =>}
        }
      }

      traitTester test mockedView

      verify(mockedView).setOnClickListener(any(classOf[View.OnClickListener]))
    }
  }
}