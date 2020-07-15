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

package controllers.print

import java.time.{LocalDate, LocalDateTime}

import base.SpecBase
import models.http.NameType
import models.pages.WhatIsNext
import models.pages.WhatIsNext.{CloseTrust, MakeChanges}
import models.{AgentDeclaration, UKAddress, UserAnswers}
import pages.beneficiaries.charity._
import pages.close.DateLastAssetSharedOutPage
import pages.declaration.AgentDeclarationPage
import pages.{SubmissionDatePage, TVNPage, WhatIsNextPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.print.PrintPlaybackHelper
import views.html.print.{PrintMaintainDeclaredAnswersView, PrintMaintainFinalDeclaredAnswersView}

class PrintMaintainDeclaredAnswersControllerSpec extends SpecBase {

  val fakeTvn = "XC TRN 000 000 4912"
  val fakeAgencyName = "Agency Name"
  val fakeTelephoneNumber = "01234567890"
  val fakeCrn = "123456"

  "PlaybackDeclaredAnswersController Controller" must {

    def playbackAnswers(whatIsNext: WhatIsNext) = UserAnswers("internalId")
      .set(WhatIsNextPage, whatIsNext).success.value
      .set(TVNPage, fakeTvn).success.value
      .set(AgentDeclarationPage, AgentDeclaration(NameType("John", None, "Smith"), fakeAgencyName, fakeTelephoneNumber, fakeCrn, None)).success.value
      .set(SubmissionDatePage, LocalDateTime.of(2020, 1, 27, 0, 0)).success.value

      .set(CharityBeneficiaryNamePage(0), "Charity Beneficiary 1").success.value
      .set(CharityBeneficiaryDiscretionYesNoPage(0), true).success.value
      .set(CharityBeneficiaryShareOfIncomePage(0), "10").success.value
      .set(CharityBeneficiaryAddressYesNoPage(0), true).success.value
      .set(CharityBeneficiaryAddressUKYesNoPage(0), true).success.value
      .set(CharityBeneficiaryAddressPage(0), UKAddress("line1", "line2", None, None, "NE11NE")).success.value

      .set(CharityBeneficiaryNamePage(1), "Charity Beneficiary 2").success.value
      .set(CharityBeneficiaryDiscretionYesNoPage(1), false).success.value
      .set(CharityBeneficiaryAddressYesNoPage(1), false).success.value

    "return OK and the correct view for a GET when making changes" in {

      val answers = playbackAnswers(MakeChanges)

      val entities = injector.instanceOf[PrintPlaybackHelper].people(answers)

      val trustDetails = injector.instanceOf[PrintPlaybackHelper].trustDetails(answers)

      val application = applicationBuilder(userAnswers = Some(answers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.PrintMaintainDeclaredAnswersController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PrintMaintainDeclaredAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          entities,
          trustDetails,
          fakeTvn,
          fakeCrn,
          "27 January 2020",
          isAgent = true
        )(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when closing" in {

      val answers = playbackAnswers(CloseTrust)
        .set(DateLastAssetSharedOutPage, LocalDate.parse("2019-02-03")).success.value

      val entities = injector.instanceOf[PrintPlaybackHelper].people(answers)

      val trustDetails = injector.instanceOf[PrintPlaybackHelper].trustDetails(answers)

      val closeDate = injector.instanceOf[PrintPlaybackHelper].closeDate(answers)

      val application = applicationBuilder(userAnswers = Some(answers), AffinityGroup.Agent).build()

      val request = FakeRequest(GET, routes.PrintMaintainDeclaredAnswersController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PrintMaintainFinalDeclaredAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          closeDate,
          entities,
          trustDetails,
          fakeTvn,
          fakeCrn,
          "27 January 2020",
          isAgent = true
        )(fakeRequest, messages).toString

      application.stop()
    }
  }

}