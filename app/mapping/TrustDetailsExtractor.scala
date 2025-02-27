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

package mapping

import mapping.PlaybackExtractionErrors.{FailedToExtractData, PlaybackExtractionError}
import models.UserAnswers
import models.http.{NonUKType, ResidentialStatusType, TrustDetailsType, UkType}
import models.pages.NonResidentType
import pages.trustdetails._
import play.api.Logging

import scala.util.{Failure, Success, Try}

class TrustDetailsExtractor extends ConditionalExtractor with Logging {

  def extract(answers: UserAnswers, data: TrustDetailsType): Either[PlaybackExtractionError, UserAnswers] = {
    val updated = answers
      .set(WhenTrustSetupPage, data.startDate)
      .flatMap(_.set(TrustTaxableYesNoPage, data.isTaxable))
      .flatMap(_.set(ExpressTrustYesNoPage, data.expressTrust))
      .flatMap(_.set(TrustUkResidentYesNoPage, data.trustUKResident))
      .flatMap(_.set(TrustUkPropertyYesNoPage, data.trustUKProperty))
      .flatMap(answers => extractGovernedBy(data.lawCountry, answers))
      .flatMap(answers => extractAdminBy(data.administrationCountry, answers))
      .flatMap(answers => extractResidentialType(data.residentialStatus, answers))

    updated match {
      case Success(a) =>
        Right(a)
      case Failure(exception) =>
        logger.warn(s"[UTR/URN: ${answers.identifier}] failed to extract data due to ${exception.getMessage}")
        Left(FailedToExtractData(TrustDetailsType.toString))
    }
  }

  private def extractGovernedBy(lawCountry: Option[String],
                                answers: UserAnswers): Try[UserAnswers] = {
    extractIfTaxable(answers) {
      lawCountry match {
        case Some(country) => answers
          .set(GovernedInsideTheUKPage, false)
          .flatMap(_.set(CountryGoverningTrustPage, country))
        case _ => answers
          .set(GovernedInsideTheUKPage, true)
      }
    }
  }

  private def extractAdminBy(administrationCountry: Option[String],
                             answers: UserAnswers): Try[UserAnswers] = {
    extractIfTaxable(answers) {
      administrationCountry match {
        case Some(country) => answers
          .set(AdministrationInsideUKPage, false)
          .flatMap(_.set(CountryAdministeringTrustPage, country))
        case _ => answers
          .set(AdministrationInsideUKPage, true)
      }
    }
  }

  private def extractResidentialType(residentialStatus: Option[ResidentialStatusType],
                                     answers: UserAnswers): Try[UserAnswers] = {
    extractIfTaxable(answers) {
      residentialStatus match {
        case Some(ResidentialStatusType(Some(uk), None)) => ukTrust(uk, answers)
        case Some(ResidentialStatusType(None, Some(nonUK))) => nonUKTrust(nonUK, answers)
        case _ => Success(answers)
      }
    }
  }

  private def ukTrust(uk: UkType, answers: UserAnswers): Try[UserAnswers] = {

    def extractOffShore(answers: UserAnswers): Try[UserAnswers] = uk.preOffShore match {
      case Some(country) => answers
        .set(TrustResidentOffshorePage, true)
        .flatMap(_.set(TrustPreviouslyResidentPage, country))
      case _ => answers
        .set(TrustResidentOffshorePage, false)
    }
    extractIfTaxable(answers) {
      answers
        .set(EstablishedUnderScotsLawPage, uk.scottishLaw)
        .flatMap(answers => extractOffShore(answers))
    }
  }

  private def nonUKTrust(nonUK: NonUKType, answers: UserAnswers): Try[UserAnswers] = {

    def inheritanceTaxAct(answers: UserAnswers): Try[UserAnswers] = nonUK.s218ihta84 match {
      case Some(value) => answers.set(InheritanceTaxActPage, value)
      case _ => Success(answers)
    }

    def agentOtherThanBarrister(answers: UserAnswers): Try[UserAnswers] = nonUK.agentS218IHTA84 match {
      case Some(value) => answers.set(AgentOtherThanBarristerPage, value)
      case _ => Success(answers)
    }

    def nonResidentType(answers: UserAnswers): Try[UserAnswers] = nonUK.trusteeStatus.map(NonResidentType.fromDES) match {
      case Some(value) => answers.set(NonResidentTypePage, value)
      case _ => Success(answers)
    }

    extractIfTaxable(answers) {
      answers
        .set(RegisteringTrustFor5APage, nonUK.sch5atcgga92)
        .flatMap(answers => inheritanceTaxAct(answers))
        .flatMap(answers => agentOtherThanBarrister(answers))
        .flatMap(answers => nonResidentType(answers))
    }
  }
}
