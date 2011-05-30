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

import android.view.View
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{OneInstancePerTest, WordSpec}
import com.sattvik.baitha.EnhancedViews

class EnhancedViewsSuite
    extends WordSpec with ShouldMatchers with OneInstancePerTest {
  val mockedView = mock(classOf[View])

  "ExtendedViews" when {
    "as a Trait" should {
      "provide an onClick method" in {
        val traitTester = new
                EnhancedViews {
          def test(v: View) {
            v onClick {_ =>}
          }
        }

        traitTester test mockedView

        verify(mockedView)
            .setOnClickListener(any(classOf[View.OnClickListener]))
      }
    }
    "as an object" should {
      import com.sattvik.baitha.EnhancedViews._

      "provide an onClick function" in {
        val mockedView = mock(classOf[View])
        mockedView onClick {_ =>}

        verify(mockedView)
            .setOnClickListener(any(classOf[View.OnClickListener]))
      }
    }
  }
}