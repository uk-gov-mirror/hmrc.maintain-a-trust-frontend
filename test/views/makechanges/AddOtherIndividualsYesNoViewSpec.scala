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

package views.makechanges

import controllers.makechanges.routes
import forms.YesNoFormProvider
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.makechanges.AddOtherIndividualsYesNoView

class AddOtherIndividualsYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addOtherIndividuals"
  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)
  lazy val onSubmit: Call = routes.AddOtherIndividualsYesNoController.onSubmit()

  "AddOtherIndividualsYesNo view" must {

    val view = viewFor[AddOtherIndividualsYesNoView](Some(emptyUserAnswersForUtr))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, messageKeyPrefix)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix)

    behave like pageWithASubmitButton(applyView(form))

  }
}
