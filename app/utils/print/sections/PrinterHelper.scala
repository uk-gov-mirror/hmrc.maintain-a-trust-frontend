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

import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

trait PrinterHelper {

  def prependHeadingToAnswerSections(answerSections: Seq[AnswerSection], isTaxable: Boolean)
                                    (implicit messages: Messages): Seq[AnswerSection] = {
    (answerSections.nonEmpty, headingKey(isTaxable)) match {
      case (true, Some(_)) => answerSectionWithRows(Seq(), isTaxable) +: answerSections
      case (true, _) => answerSections
      case _ => Nil
    }
  }

  def answerSectionWithRows(rows: Seq[Option[AnswerRow]], isTaxable: Boolean)
                           (implicit messages: Messages): AnswerSection = AnswerSection(
    headingKey = None,
    rows = rows.flatten,
    sectionKey = heading(isTaxable)
  )

  private def heading(isTaxable: Boolean)(implicit messages: Messages): Option[String] = headingKey(isTaxable) map { x =>
    messages(s"answerPage.section.$x.heading")
  }

  def headingKey(isTaxable: Boolean): Option[String]

}
