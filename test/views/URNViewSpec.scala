/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views

import forms.URNFormProvider
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.URNView

class URNViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "urn"

  val form = new URNFormProvider()()

  "URNView view" must {

    val view = viewFor[URNView](Some(emptyUserAnswersForUtr))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, Call("POST", ""))(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix)
  }
}
