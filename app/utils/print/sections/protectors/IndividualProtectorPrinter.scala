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

package utils.print.sections.protectors

import models.{FullName, UserAnswers}
import pages.QuestionPage
import pages.protectors.individual._
import play.api.i18n.Messages
import play.api.libs.json.{JsArray, JsPath}
import sections.Protectors
import utils.print.sections.{AnswerRowConverter, Printer}
import viewmodels.AnswerRow

import javax.inject.Inject

class IndividualProtectorPrinter @Inject()(converter: AnswerRowConverter) extends Printer[FullName] {

  override def answerRows(index: Int, userAnswers: UserAnswers, name: String)
                         (implicit messages: Messages): Seq[Option[AnswerRow]] = Seq(
    converter.fullNameQuestion(IndividualProtectorNamePage(index), userAnswers, "individualProtectorName", name),
    converter.yesNoQuestion(IndividualProtectorDateOfBirthYesNoPage(index), userAnswers, "individualProtectorDateOfBirthYesNo", name),
    converter.dateQuestion(IndividualProtectorDateOfBirthPage(index),userAnswers, "individualProtectorDateOfBirth", name),
    converter.yesNoQuestion(IndividualProtectorNINOYesNoPage(index), userAnswers, "individualProtectorNINOYesNo", name),
    converter.ninoQuestion(IndividualProtectorNINOPage(index), userAnswers, "individualProtectorNINO", name),
    converter.yesNoQuestion(IndividualProtectorAddressYesNoPage(index), userAnswers, "individualProtectorAddressYesNo", name),
    converter.yesNoQuestion(IndividualProtectorAddressUKYesNoPage(index), userAnswers, "individualProtectorAddressUkYesNo", name),
    converter.addressQuestion(IndividualProtectorAddressPage(index), userAnswers, "individualProtectorAddress", name),
    converter.yesNoQuestion(IndividualProtectorPassportIDCardYesNoPage(index), userAnswers, "individualProtectorPassportIDCardYesNo", name),
    converter.passportOrIdCardQuestion(IndividualProtectorPassportIDCardPage(index), userAnswers, "individualProtectorPassportIDCard", name)
  )

  override def namePath(index: Int): JsPath = IndividualProtectorNamePage(index).path

  override val section: QuestionPage[JsArray] = Protectors

  override val sectionKey: String = "protector"

}
