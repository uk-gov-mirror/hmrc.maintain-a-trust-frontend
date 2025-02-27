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

package controllers.makechanges

import base.SpecBase
import forms.YesNoFormProvider
import models.UserAnswers
import models.pages.WhatIsNext
import pages.WhatIsNextPage
import pages.makechanges.UpdateTrusteesYesNoPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.makechanges.UpdateTrusteesYesNoView

class UpdateTrusteesYesNoControllerSpec extends SpecBase {

  val formProvider = new YesNoFormProvider()
  val prefix: String = "updateTrustees"
  val form = formProvider.withPrefix(prefix)
  lazy val updateTrusteesYesNoRoute = routes.UpdateTrusteesYesNoController.onPageLoad().url

  val baseAnswers: UserAnswers = emptyUserAnswersForUtr
    .set(WhatIsNextPage, WhatIsNext.MakeChanges).success.value

  "UpdateTrusteesYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, updateTrusteesYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[UpdateTrusteesYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, prefix)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers.set(UpdateTrusteesYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, updateTrusteesYesNoRoute)

      val view = application.injector.instanceOf[UpdateTrusteesYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), prefix)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, updateTrusteesYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.UpdateBeneficiariesYesNoController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, updateTrusteesYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[UpdateTrusteesYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, prefix)(request, messages).toString

      application.stop()
    }

  }
}
