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

package controllers

import base.SpecBase
import models.UTR
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AgentNotAuthorisedView

class AgentNotAuthorisedControllerSpec extends SpecBase {

  "AgentNotAuthorised Controller" must {

    "return OK and the correct view for a GET" in {

      val answers = emptyUserAnswersForUtr

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.AgentNotAuthorisedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AgentNotAuthorisedView]

      val utr = "1234567890"

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(utr, UTR)(request, messages).toString

      application.stop()
    }
  }
}
