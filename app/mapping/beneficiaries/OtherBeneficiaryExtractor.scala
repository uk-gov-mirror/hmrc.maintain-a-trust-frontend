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

package mapping.beneficiaries

import models.http.DisplayTrustOtherType
import models.{Address, MetaData, UserAnswers}
import pages.QuestionPage
import pages.beneficiaries.other._

import scala.util.Try

class OtherBeneficiaryExtractor extends BeneficiaryPlaybackExtractor[DisplayTrustOtherType] {

  override def shareOfIncomeYesNoPage(index: Int): QuestionPage[Boolean] = OtherBeneficiaryDiscretionYesNoPage(index)
  override def shareOfIncomePage(index: Int): QuestionPage[String] = OtherBeneficiaryShareOfIncomePage(index)

  override def addressYesNoPage(index: Int): QuestionPage[Boolean] = OtherBeneficiaryAddressYesNoPage(index)
  override def ukAddressYesNoPage(index: Int): QuestionPage[Boolean] = OtherBeneficiaryAddressUKYesNoPage(index)
  override def addressPage(index: Int): QuestionPage[Address] = OtherBeneficiaryAddressPage(index)

  override def updateUserAnswers(answers: Try[UserAnswers],
                                 entity: DisplayTrustOtherType,
                                 index: Int): Try[UserAnswers] = {
    answers
      .flatMap(_.set(OtherBeneficiaryDescriptionPage(index), entity.description))
      .flatMap(answers => extractShareOfIncome(entity.beneficiaryShareOfIncome, index, answers))
      .flatMap(answers => extractOptionalAddress(entity.address, index, answers))
      .flatMap {
        _.set(
          OtherBeneficiaryMetaData(index),
          MetaData(
            lineNo = entity.lineNo.getOrElse(""),
            bpMatchStatus = entity.bpMatchStatus.fold(Some("98"))(x => Some(x)),
            entityStart = entity.entityStart
          )
        )
      }
  }
}
