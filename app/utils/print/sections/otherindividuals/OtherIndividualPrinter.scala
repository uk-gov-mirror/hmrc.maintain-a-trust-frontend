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

package utils.print.sections.otherindividuals

import javax.inject.Inject
import models.UserAnswers
import pages.individual._
import play.api.i18n.Messages
import utils.print.sections.AnswerRowConverter
import viewmodels.AnswerSection

class OtherIndividualPrinter @Inject()(converter: AnswerRowConverter)
                                      (implicit messages: Messages) {

  def print(index: Int, userAnswers: UserAnswers): Seq[AnswerSection] = {
    userAnswers
      .get(OtherIndividualNamePage(index))
      .map(_.toString)
      .map { name =>
        Seq(
          AnswerSection(
            headingKey = Some(messages("answerPage.section.otherIndividual.subheading", index + 1)),
            Seq(
              converter.fullNameQuestion(OtherIndividualNamePage(index), userAnswers, "otherIndividualName"),
              converter.yesNoQuestion(OtherIndividualDateOfBirthYesNoPage(index), userAnswers, "otherIndividualDateOfBirthYesNo", name),
              converter.dateQuestion(OtherIndividualDateOfBirthPage(index), userAnswers, "otherIndividualDateOfBirth", name),
              converter.yesNoQuestion(OtherIndividualNationalInsuranceYesNoPage(index), userAnswers, "otherIndividualNINOYesNo", name),
              converter.ninoQuestion(OtherIndividualNationalInsuranceNumberPage(index), userAnswers, "otherIndividualNINO", name),
              converter.yesNoQuestion(OtherIndividualAddressYesNoPage(index), userAnswers, "otherIndividualAddressYesNo", name),
              converter.yesNoQuestion(OtherIndividualAddressUKYesNoPage(index), userAnswers, "otherIndividualAddressUKYesNo", name),
              converter.addressQuestion(OtherIndividualAddressPage(index), userAnswers, "otherIndividualAddress", name),
              converter.yesNoQuestion(OtherIndividualPassportIDCardYesNoPage(index), userAnswers, "otherIndividualPassportIDCardYesNo", name),
              converter.passportOrIdCardQuestion(OtherIndividualPassportIDCardPage(index), userAnswers, "otherIndividualPassportIDCard", name)
            ).flatten,
            sectionKey = None
          )
        )
      }.getOrElse(Nil)
  }

}