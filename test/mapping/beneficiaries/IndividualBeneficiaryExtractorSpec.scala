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

import base.SpecBaseHelpers
import generators.Generators
import models.http.{AddressType, DisplayTrustIdentificationType, DisplayTrustIndividualDetailsType, PassportType}
import models.pages.RoleInCompany
import models.{FullName, InternationalAddress, MetaData, UKAddress}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.beneficiaries.individual._
import utils.Constants.GB

import java.time.LocalDate

class IndividualBeneficiaryExtractorSpec extends FreeSpec with MustMatchers with EitherValues with Generators with SpecBaseHelpers {

  def generateIndividual(index: Int) = DisplayTrustIndividualDetailsType(
    lineNo = Some(s"$index"),
    bpMatchStatus = Some("01"),
    name = FullName(s"First Name $index", None, s"Last Name $index"),
    dateOfBirth = index match {
      case 0 => Some(LocalDate.parse("1970-02-01"))
      case _ => None
    },
    countryOfResidence = index match {
      case 0 => Some(GB)
      case 1 => Some("DE")
      case _ => None
    },
    countryOfNationality = index match {
      case 0 => Some(GB)
      case 1 => Some("DE")
      case _ => None
    },
    legallyIncapable = index match {
      case 0 => Some(false)
      case 1 => Some(true)
      case _ => None
    },
    vulnerableBeneficiary = Some(true),
    beneficiaryType = index match {
      case 0 => Some(RoleInCompany.Director)
      case 1 => Some(RoleInCompany.Employee)
      case 2 => Some(RoleInCompany.NA)
    },
    beneficiaryDiscretion = index match {
      case 0 => Some(false)
      case _ => None
    },
    beneficiaryShareOfIncome = index match {
      case 0 => Some("98")
      case _ => None
    },
    identification = Some(
      DisplayTrustIdentificationType(
        safeId = Some("8947584-94759745-84758745"),
        nino = index match {
          case 0 => Some(s"${index}234567890")
          case _ => None
        },
        passport = index match {
          case 2 => Some(PassportType("KSJDFKSDHF6456545147852369QWER", LocalDate.of(2020,2,2), "DE"))
          case _ => None
        },
        address = index match {
          case 1 => Some(AddressType(s"line $index", "line2", None, None, None, "DE"))
          case 2 => Some(AddressType(s"line $index", "line2", None, None, Some("NE11NE"), GB))
          case _ => None
        }
      )
    ),
    entityStart = "2019-11-26"
  )

  val individualExtractor : IndividualBeneficiaryExtractor =
    injector.instanceOf[IndividualBeneficiaryExtractor]

  "Individual Beneficiary Extractor" - {

    "4mld taxable" - {
      "should not populate Country Of Residence pages" in {
        val individual = List(DisplayTrustIndividualDetailsType(
          lineNo = Some("1"),
          bpMatchStatus = Some("01"),
          name = FullName("First Name", None, "Last Name"),
          dateOfBirth = None,
          countryOfResidence = Some("FR"),
          countryOfNationality = Some("FR"),
          legallyIncapable = None,
          vulnerableBeneficiary = Some(false),
          beneficiaryType = None,
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None,
          identification = None,
          entityStart = "2019-11-26"
        ))

        val ua = emptyUserAnswersForUtr

        val extraction = individualExtractor.extract(ua, individual)

        extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)) mustNot be(defined)
      }
    }

    "5mld taxable" - {

      "when no individual" - {

        "must return user answers" in {

          val individual = Nil

          val ua = emptyUserAnswersForUtr

          val extraction = individualExtractor.extract(ua, individual)

          extraction mustBe 'left

        }

      }

      "when there are individuals" - {

        "fail if vulnerable missing" in {
          val individual = List(DisplayTrustIndividualDetailsType(
            lineNo = Some("1"),
            bpMatchStatus = Some("01"),
            name = FullName("First Name", None, "Last Name"),
            dateOfBirth = None,
            countryOfResidence = None,
            countryOfNationality = None,
            legallyIncapable = None,
            vulnerableBeneficiary = None,
            beneficiaryType = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            entityStart = "2019-11-26"
          ))

          val ua = emptyUserAnswersForUtr

          val extraction = individualExtractor.extract(ua, individual)

          extraction mustBe 'left
        }

        "with minimum data must return user answers updated" in {
          val individual = List(DisplayTrustIndividualDetailsType(
            lineNo = Some("1"),
            bpMatchStatus = Some("01"),
            name = FullName("First Name", None, "Last Name"),
            dateOfBirth = None,
            countryOfResidence = None,
            countryOfNationality = None,
            legallyIncapable = None,
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            entityStart = "2019-11-26"
          ))

          val ua = emptyUserAnswersForUtr.copy(is5mldEnabled = true, isUnderlyingData5mld = true)

          val extraction = individualExtractor.extract(ua, individual)

          extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
          extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)) mustNot be(defined)
        }

        "with full data must return user answers updated" in {
          val individuals = (for(index <- 0 to 2) yield generateIndividual(index)).toList

          val ua = emptyUserAnswersForUtr.copy(is5mldEnabled = true, isUnderlyingData5mld = true)

          val extraction = individualExtractor.extract(ua, individuals)

          extraction mustBe 'right

          extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name 0", None, "Last Name 0")
          extraction.right.value.get(IndividualBeneficiaryNamePage(1)).get mustBe FullName("First Name 1", None, "Last Name 1")
          extraction.right.value.get(IndividualBeneficiaryNamePage(2)).get mustBe FullName("First Name 2", None, "Last Name 2")

          extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")

          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)).get mustBe RoleInCompany.Director
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(1)).get mustBe RoleInCompany.Employee
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(2)).get mustBe RoleInCompany.NA

          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(2)).get mustBe true

          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)).get mustBe LocalDate.of(1970,2,1)
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)).get mustBe GB
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(1)).get mustBe "DE"
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(0)).get mustBe GB
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(1)).get mustBe "DE"
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(2)).get mustBe true

          extraction.right.value.get(IndividualBeneficiaryIncomePage(0)).get mustBe "98"
          extraction.right.value.get(IndividualBeneficiaryIncomePage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomePage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)).get mustBe "0234567890"
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(2)).get mustBe true

          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(2)).get mustBe true

          extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressPage(1)).get mustBe InternationalAddress("line 1", "line2", None, "DE")
          extraction.right.value.get(IndividualBeneficiaryAddressPage(2)).get mustBe UKAddress("line 2", "line2", None, None, "NE11NE")

          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(2)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(2)).get.country mustBe "DE"

          extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)).get mustBe "8947584-94759745-84758745"
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(1)).get mustBe "8947584-94759745-84758745"
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        }

      }
    }

    "non-taxable" - {

      "when no individual" - {

        "must return user answers" in {

          val individual = Nil

          val ua = emptyUserAnswersForUrn

          val extraction = individualExtractor.extract(ua, individual)

          extraction mustBe 'left

        }

      }

      "when there are individuals" - {

        "succeed if vulnerable missing" in {
          val individual = List(DisplayTrustIndividualDetailsType(
            lineNo = Some("1"),
            bpMatchStatus = Some("01"),
            name = FullName("First Name", None, "Last Name"),
            dateOfBirth = None,
            countryOfResidence = None,
            countryOfNationality = None,
            legallyIncapable = None,
            vulnerableBeneficiary = None,
            beneficiaryType = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            entityStart = "2019-11-26"
          ))

          val ua = emptyUserAnswersForUrn

          val extraction = individualExtractor.extract(ua, individual)

          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)) mustNot be(defined)
        }

        "with minimum data must return user answers updated" in {
          val individual = List(DisplayTrustIndividualDetailsType(
            lineNo = Some("1"),
            bpMatchStatus = Some("01"),
            name = FullName("First Name", None, "Last Name"),
            dateOfBirth = None,
            countryOfResidence = None,
            countryOfNationality = None,
            legallyIncapable = None,
            vulnerableBeneficiary = Some(false),
            beneficiaryType = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            identification = None,
            entityStart = "2019-11-26"
          ))

          val ua = emptyUserAnswersForUrn

          val extraction = individualExtractor.extract(ua, individual)

          extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
          extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)) mustNot be(defined)
        }

        "with full data must return user answers updated" in {
          val individuals = (for(index <- 0 to 2) yield generateIndividual(index)).toList

          val ua = emptyUserAnswersForUrn

          val extraction = individualExtractor.extract(ua, individuals)

          extraction mustBe 'right

          extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name 0", None, "Last Name 0")
          extraction.right.value.get(IndividualBeneficiaryNamePage(1)).get mustBe FullName("First Name 1", None, "Last Name 1")
          extraction.right.value.get(IndividualBeneficiaryNamePage(2)).get mustBe FullName("First Name 2", None, "Last Name 2")

          extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("0", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryMetaData(1)).get mustBe MetaData("1", Some("01"), "2019-11-26")
          extraction.right.value.get(IndividualBeneficiaryMetaData(2)).get mustBe MetaData("2", Some("01"), "2019-11-26")

          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)).get mustBe LocalDate.of(1970,2,1)
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)).get mustBe GB
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(1)).get mustBe "DE"
          extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(1)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(2)).get mustBe false

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(0)).get mustBe GB
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(1)).get mustBe "DE"
          extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)).get mustBe true
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(1)).get mustBe false
          extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomePage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryIncomePage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryAddressPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(1)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(2)) mustNot be(defined)
          extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(2)) mustNot be(defined)

          extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)).get mustBe "8947584-94759745-84758745"
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(1)).get mustBe "8947584-94759745-84758745"
          extraction.right.value.get(IndividualBeneficiarySafeIdPage(2)).get mustBe "8947584-94759745-84758745"

        }

      }

    }
  }

}
