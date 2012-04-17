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

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

import com.sattvik.baitha._
import com.sattvik.baitha.EnhancedPreferences.EnhancedEditor
import com.sattvik.baitha.TypedPreference._

import org.mockito.Matchers.any
import org.mockito.Mockito.{never, verify, when}
import org.mockito.ArgumentCaptor

/** Test suite for the `EnhancedPreferences` class.
  *
  * @author Daniel Solano Gómez */
class EnhancedPreferencesSpec
    extends WordSpec with ShouldMatchers with MockitoSugar {
  /** Basic fixture for the tests. */
  trait Fixture {
    val mockPrefs = mock[SharedPreferences]
    val prefs     = new EnhancedPreferences(mockPrefs)
    val prefKey   = "Foo"
  }

  /** Fixture for tests that do retrieval */
  trait GetPreferenceFixture[A] extends Fixture {
    def pref: TypedPreference[A]
    def verifyGetFrom()
  }

  /** Fixture for tests that use an editor. */
  trait EditorFixture extends Fixture {
    val mockEditor = mock[SharedPreferences.Editor]
    when(mockPrefs.edit()).thenReturn(mockEditor)

    def verifyCommit() = verify(mockEditor).commit()
    def verifyNoCommit() = verify(mockEditor, never()).commit()
  }

  /** Fixture for tests that do modification */
  trait PutPreferenceFixture[A] extends Fixture with EditorFixture {
    def pref: TypedPreference[A]
    def value: A
    def verifyPutInto()
  }

  /** All of the basic preference retrieval tests. */
  def preferenceGetBehaviours(fixture: GetPreferenceFixture[_]) {
    import fixture._
    "allow checking for existence" in {
      prefs.contains(pref)
      verify(mockPrefs).contains(prefKey)
    }

    "retrieve a value out of the preferences" in {
      prefs.get(pref)
      verifyGetFrom()
    }

    "retrieve None when getOption is used and there is no preference" in {
      when(mockPrefs.contains(prefKey)).thenReturn(false)
      prefs.getOption(pref) should equal(None)
    }

    "retrieve something when getOption is used and there is a preference" in {
      when(mockPrefs.contains(prefKey)).thenReturn(true)
      prefs.getOption(pref) should be('defined)
    }

    "get the preference using an extractor" in {
      val Pref = pref
      prefs match {
        case Pref(_) => // successful match
        case _       => fail("Match failed")
      }
    }
  }

  /** All of the basic preference modification tests. */
  def preferencePutBehaviours[A](newFixture: => PutPreferenceFixture[A]) {
    trait PutFixture {
      val fixture  = newFixture
      val putValue = fixture.value
      val pref     = fixture.pref

      def performTest(editor: EnhancedPreferences.EnhancedEditor)
    }
    trait CommitPutIntoFixture extends PutFixture {
      fixture.prefs.withEditor(performTest)
      fixture.verifyPutInto()
      fixture.verifyCommit()
    }
    trait CommitRemoveFixture extends PutFixture {
      fixture.prefs.withEditor(performTest)
      verify(fixture.mockEditor).remove(pref.key)
      fixture.verifyCommit()
    }
    trait AbortFixture extends PutFixture {
      fixture.prefs.withEditor(performTest)
      fixture.verifyNoCommit()
    }
    "put in a value" in {
      new CommitPutIntoFixture {
        def performTest(editor: EnhancedEditor) {
          editor.put(pref, putValue)
        }
      }
    }

    "remove the preference" in {
      new CommitRemoveFixture {
        def performTest(editor: EnhancedEditor) {
          editor.remove(pref)
        }
      }
    }

    "put in a value passed in as a defined option" in {
      new CommitPutIntoFixture {
        def performTest(editor: EnhancedEditor) {
          editor.put(pref, Some(putValue))
        }
      }
    }

    "remove the preference when given None" in {
      new CommitRemoveFixture {
        def performTest(editor: EnhancedEditor) {
          editor.put(pref, None)
        }
      }
    }

    "put the preference if given as a value" in {
      new CommitPutIntoFixture {
        def performTest(editor: EnhancedEditor) {
          val appliedValue = pref(putValue)
          editor.put(appliedValue)
        }
      }
    }

    "not commit if aborted after a put" in {
      new AbortFixture {
        def performTest(editor: EnhancedEditor) {
          editor.put(pref, putValue)
          editor.abort()
        }
      }
    }

    "not commit if aborted before a put" in {
      new AbortFixture {
        def performTest(editor: EnhancedEditor) {
          editor.abort()
          editor.put(pref, Some(putValue))
        }
      }
    }
  }

  "An EnhancedPreferences" when {
    "working with a boolean preference" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Boolean] {
          val pref = BooleanPreference(prefKey, true)

          def verifyGetFrom() {
            verify(mockPrefs).getBoolean(prefKey, true)
          }
        }
      )
    }
    "working with a boolean preference (without a default)" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Boolean] {
          val pref = TypedPreference[Boolean](prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getBoolean(prefKey, false)
          }
        }
      )

      behave like preferencePutBehaviours {
        new PutPreferenceFixture[Boolean] {
          val pref  = TypedPreference[Boolean](prefKey)
          val value = true

          def verifyPutInto() {verify(mockEditor).putBoolean(prefKey, value)}
        }
      }
    }

    "working with a float preference" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Float] {
          val pref = FloatPreference(prefKey, 1.0f)

          def verifyGetFrom() {
            verify(mockPrefs).getFloat(prefKey, 1.0f)
          }
        }
      )
    }
    "working with a float preference (without a default)" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Float] {
          val pref = TypedPreference[Float](prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getFloat(prefKey, 0.0f)
          }
        }
      )

      behave like preferencePutBehaviours {
        new PutPreferenceFixture[Float] {
          val pref  = TypedPreference[Float](prefKey)
          val value = 3.14f

          def verifyPutInto() {verify(mockEditor).putFloat(prefKey, value)}
        }
      }
    }

    "working with a int preference" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Int] {
          val pref = IntPreference(prefKey, 42)

          def verifyGetFrom() {
            verify(mockPrefs).getInt(prefKey, 42)
          }
        }
      )
    }
    "working with a int preference (without a default)" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Int] {
          val pref = TypedPreference[Int](prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getInt(prefKey, 0)
          }
        }
      )

      behave like preferencePutBehaviours {
        new PutPreferenceFixture[Int] {
          val pref  = TypedPreference[Int](prefKey)
          val value = 42

          def verifyPutInto() {verify(mockEditor).putInt(prefKey, value)}
        }
      }
    }

    "working with a long preference" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Long] {
          val pref = LongPreference(prefKey, 42L)

          def verifyGetFrom() {
            verify(mockPrefs).getLong(prefKey, 42L)
          }
        }
      )
    }
    "working with a long preference (without a default)" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Long] {
          val pref = TypedPreference[Long](prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getLong(prefKey, 0L)
          }
        }
      )

      behave like preferencePutBehaviours {
        new PutPreferenceFixture[Long] {
          val pref  = TypedPreference[Long](prefKey)
          val value = 42L

          def verifyPutInto() {verify(mockEditor).putLong(prefKey, value)}
        }
      }
    }

    "working with a string preference" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[String] {
          val pref = StringPreference(prefKey, "bar")

          def verifyGetFrom() {
            verify(mockPrefs).getString(prefKey, "bar")
          }
        }
      )
    }
    "working with a string preference (without a default)" should {
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[String] {
          val pref = TypedPreference[String](prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getString(prefKey, "")
          }
        }
      )

      behave like preferencePutBehaviours {
        new PutPreferenceFixture[String] {
          val pref  = TypedPreference[String](prefKey)
          val value = "bar"

          def verifyPutInto() {verify(mockEditor).putString(prefKey, "bar")}
        }
      }
    }

    "working with a complex preference type" should {
      case class Complex(real: Float, imaginary: Float)
      case class ComplexPreference(key: String)
          extends TypedPreference[Complex] {
        def getFrom(preferences: SharedPreferences) = {
          Complex(
            preferences.getFloat(key, 0.0f),
            preferences.getFloat(key + ".imaginary", 0.0f))
        }

        def putInto(editor: Editor, value: Complex) {
          editor.putFloat(key, value.real)
          editor.putFloat(key + ".imaginary", value.imaginary)
        }
      }
      behave like preferenceGetBehaviours(
        new GetPreferenceFixture[Complex] {
          val pref = ComplexPreference(prefKey)

          def verifyGetFrom() {
            verify(mockPrefs).getFloat(prefKey, 0.0f)
            verify(mockPrefs).getFloat(prefKey + ".imaginary", 0.0f)
          }
        }
      )
      behave like preferencePutBehaviours {
        new PutPreferenceFixture[Complex] {
          val pref  = ComplexPreference(prefKey)
          val value = Complex(-1.0f, 3.0f)

          def verifyPutInto() {
            verify(mockEditor).putFloat(prefKey, -1.0f)
            verify(mockEditor).putFloat(prefKey + ".imaginary", 3.0f)
          }
        }
      }
      "cannot use TypedReference apply method" in {
        val ex = intercept[IllegalArgumentException] {
          TypedPreference[Complex]("Foo")
        }
        ex.getMessage should include("supported")
      }
    }

    "working with nothing in particular" should {
      "commit, even in an empty block" in {
        new EditorFixture {
          prefs.withEditor {_ =>}
          verifyCommit()
        }
      }
      "clear and commit" in {
        new EditorFixture {
          prefs.withEditor {_.clear()}
          verify(mockEditor).clear()
          verifyCommit()
        }
      }
      "not commit when abort is called" in {
        new EditorFixture {
          prefs.withEditor {_.abort()}
          verifyNoCommit()
        }
      }
      "implicit conversion works" in {
        new Fixture {
          import EnhancedPreferences._

          mockPrefs.contains(BooleanPreference(prefKey))
        }
      }
      "can register a listener" in {
        new Fixture {
          val captor = ArgumentCaptor.forClass(
            classOf[SharedPreferences.OnSharedPreferenceChangeListener])
          val token = prefs.onChange() { (_, _) =>  }
          verify(mockPrefs).registerOnSharedPreferenceChangeListener(
            captor.capture())
          token should be theSameInstanceAs (captor.getValue)
        }
      }
      "can unregister a listener" in {
        new Fixture {
          val token = prefs.onChange() { (_, _) =>  }
          prefs.unregisterOnChange(token)
          verify(mockPrefs).unregisterOnSharedPreferenceChangeListener(
            token)
        }
      }
      "listeners do filtering" in {
        new Fixture {
          val pref = TypedPreference[Int](prefKey)

          val token = prefs.onChange(pref) { (tpref, value) =>
            val OkKey = prefKey
            tpref.key match {
              case OkKey => // expected
              case _ => fail("%s should not have been received".format(tpref))
            }
          }
          token.onSharedPreferenceChanged(null, "FOOBAR")
          token.onSharedPreferenceChanged(null, prefKey)
          token.onSharedPreferenceChanged(null, "BARFOO")
        }
      }
    }
  }
}