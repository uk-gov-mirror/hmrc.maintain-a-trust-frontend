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

package utils.print.sections

import models.UserAnswers
import pages.trustdetails._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class TrustDetailsPrinter @Inject()(converter: AnswerRowConverter) extends PrinterHelper {

  def print(userAnswers: UserAnswers)(implicit messages: Messages): Seq[AnswerSection] = {

    val rows: Seq[Option[AnswerRow]] = Seq(
      converter.stringQuestion(TrustNamePage, userAnswers, "trustName"),
      converter.dateQuestion(WhenTrustSetupPage, userAnswers, "whenTrustSetup"),
      converter.utr(userAnswers, "trustUniqueTaxReference")
    )

    Seq(answerSectionWithRows(rows, userAnswers.isTrustTaxable))
  }

  override def headingKey(isTaxable: Boolean): Option[String] = Some("trustsDetails")

}
