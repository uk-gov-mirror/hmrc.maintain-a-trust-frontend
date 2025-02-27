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

package utils.print.sections.beneficiaries

import models.UserAnswers
import pages.QuestionPage
import pages.beneficiaries.large._
import play.api.i18n.Messages
import play.api.libs.json.{JsArray, JsPath}
import sections.beneficiaries.LargeBeneficiaries
import utils.print.sections.{EntitiesPrinter, AnswerRowConverter, EntityPrinter}
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class LargeBeneficiaryPrinter @Inject()(converter: AnswerRowConverter) extends EntitiesPrinter[JsArray] with EntityPrinter[String] {

  override def printSection(index: Int, userAnswers: UserAnswers)
                           (implicit messages: Messages): Option[AnswerSection] = {
    printAnswerRows(index, userAnswers)
  }

  override def answerRows(index: Int, userAnswers: UserAnswers, name: String)
                         (implicit messages: Messages): Seq[Option[AnswerRow]] = Seq(
    converter.stringQuestion(LargeBeneficiaryNamePage(index), userAnswers, "largeBeneficiaryName"),
    converter.yesNoQuestion(LargeBeneficiaryCountryOfResidenceYesNoPage(index), userAnswers, "largeBeneficiaryCountryOfResidenceYesNo", name),
    converter.yesNoQuestion(LargeBeneficiaryCountryOfResidenceInTheUkYesNoPage(index), userAnswers, "largeBeneficiaryCountryOfResidenceUkYesNo", name),
    converter.countryQuestion(LargeBeneficiaryCountryOfResidenceInTheUkYesNoPage(index), LargeBeneficiaryCountryOfResidencePage(index), userAnswers, "largeBeneficiaryCountryOfResidence", name),
    converter.yesNoQuestion(LargeBeneficiaryAddressYesNoPage(index), userAnswers, "largeBeneficiaryAddressYesNo", name),
    converter.yesNoQuestion(LargeBeneficiaryAddressUKYesNoPage(index), userAnswers, "largeBeneficiaryAddressUKYesNo", name),
    converter.addressQuestion(LargeBeneficiaryAddressPage(index), userAnswers, "largeBeneficiaryAddress", name),
    converter.stringQuestion(LargeBeneficiaryUtrPage(index), userAnswers, "largeBeneficiaryUtr", name),
    converter.descriptionQuestion(LargeBeneficiaryDescriptionPage(index), userAnswers, "largeBeneficiaryDescription", name),
    converter.numberOfBeneficiariesQuestion(LargeBeneficiaryNumberOfBeneficiariesPage(index), userAnswers, "largeBeneficiaryNumberOfBeneficiaries")
  )

  override def namePath(index: Int): JsPath = LargeBeneficiaryNamePage(index).path

  override def section: QuestionPage[JsArray] = LargeBeneficiaries

  override def headingKey(isTaxable: Boolean): Option[String] = None

  override val subHeadingKey: Option[String] = Some("largeBeneficiary")
}
