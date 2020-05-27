/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.make_changes

import base.SpecBase
import connectors.TrustsStoreConnector
import controllers.makechanges.routes
import forms.YesNoFormProvider
import models.CompletedMaintenanceTasks
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.UTRPage
import pages.makechanges._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.makechanges.UpdateOtherIndividualsYesNoView

import scala.concurrent.Future

class UpdateOtherIndividualsYesNoControllerSpec extends SpecBase {

  val formProvider = new YesNoFormProvider()
  val form = formProvider.withPrefix("updateOtherIndividuals")

  val mockConnector = mock[TrustsStoreConnector]

  lazy val updateOtherIndividualsYesNoRoute = routes.UpdateOtherIndividualsYesNoController.onPageLoad().url

  "UpdateOtherIndividualsYesNoController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, updateOtherIndividualsYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[UpdateOtherIndividualsYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(AddOrUpdateOtherIndividualsYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, updateOtherIndividualsYesNoRoute)

      val view = application.injector.instanceOf[UpdateOtherIndividualsYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true))(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to individual declaration when valid data is submitted and no has been selected for all the questions" in {

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value
        .set(UpdateTrusteesYesNoPage, false).success.value
        .set(UpdateBeneficiariesYesNoPage, false).success.value
        .set(UpdateSettlorsYesNoPage, false).success.value
        .set(AddOrUpdateProtectorYesNoPage, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, updateOtherIndividualsYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.declaration.routes.IndividualDeclarationController.onPageLoad().url

      application.stop()
    }

    "redirect to overview when valid data is submitted, yes has been selected for update trustees question and no has been selected for the rest" in {

      val utr = "0987654321"

      val userAnswers = emptyUserAnswers
        .set(UTRPage, utr).success.value
        .set(UpdateTrusteesYesNoPage, true).success.value
        .set(UpdateBeneficiariesYesNoPage, false).success.value
        .set(UpdateSettlorsYesNoPage, false).success.value
        .set(AddOrUpdateProtectorYesNoPage, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[TrustsStoreConnector].toInstance(mockConnector))
          .build()

      val request =
        FakeRequest(POST, updateOtherIndividualsYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

      when(mockConnector.set(any(), any())(any(), any())).thenReturn(Future.successful(CompletedMaintenanceTasks()))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value must include(
        s"/maintain-a-trust/overview"
      )

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, updateOtherIndividualsYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[UpdateOtherIndividualsYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(fakeRequest, messages).toString

      application.stop()
    }

  }
}
