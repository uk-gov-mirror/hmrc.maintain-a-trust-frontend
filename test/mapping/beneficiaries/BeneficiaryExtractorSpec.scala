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
import mapping.PlaybackExtractionErrors.FailedToExtractData
import models.HowManyBeneficiaries.Over1
import models.http._
import models.{Description, FullName, MetaData, UKAddress}
import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import pages.beneficiaries.charity._
import pages.beneficiaries.classOfBeneficiary._
import pages.beneficiaries.company._
import pages.beneficiaries.individual._
import pages.beneficiaries.large._
import pages.beneficiaries.other._
import pages.beneficiaries.trust._
import utils.Constants.GB

class BeneficiaryExtractorSpec extends FreeSpec with MustMatchers
  with EitherValues with Generators with SpecBaseHelpers {

  val utr = "1234567890"

  val beneficiaryExtractor : BeneficiaryExtractor =
    injector.instanceOf[BeneficiaryExtractor]

  "Beneficiary Extractor" - {

    "when no beneficiary" - {

      "must return an error" in {

        val beneficiary = DisplayTrustBeneficiaryType(Nil, Nil, Nil, Nil, Nil, Nil, Nil)

        val ua = emptyUserAnswersForUtr

        val extraction = beneficiaryExtractor.extract(ua, beneficiary)

        extraction.left.value mustBe a[FailedToExtractData]

      }

    }

    "when there are beneficiaries of different type" - {

      "must return user answers updated" in {
        val beneficiary = DisplayTrustBeneficiaryType(
          individualDetails = List(
            DisplayTrustIndividualDetailsType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              name = FullName("First Name", None, "Last Name"),
              dateOfBirth = None,
              countryOfResidence = Some(GB),
              countryOfNationality = Some(GB),
              legallyIncapable = Some(false),
              vulnerableBeneficiary = Some(false),
              beneficiaryType = None,
              beneficiaryDiscretion = None,
              beneficiaryShareOfIncome = None,
              identification = None,
              entityStart = "2019-11-26"
            )
          ),
          company = List(
            DisplayTrustCompanyType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              organisationName = s"Company 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              countryOfResidence = Some(GB),
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = None,
                  address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), GB))
                )
              ),
              entityStart = "2019-11-26"
            )
          ),
          trust = List(
            DisplayTrustBeneficiaryTrustType(
              lineNo = Some(s"1"),
              bpMatchStatus = Some("01"),
              organisationName = s"Trust 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              countryOfResidence = Some(GB),
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = None,
                  address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), GB))
                )
              ),
              entityStart = "2019-11-26"
            )
          ),
          charity = List(
            DisplayTrustCharityType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              organisationName = s"Charity 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              countryOfResidence = Some(GB),
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = None,
                  address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), GB))
                )
              ),
              entityStart = "2019-11-26"
            )
          ),
          unidentified = List(
            DisplayTrustUnidentifiedType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              description = s"Class Of Beneficiary 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              entityStart = "2019-11-26"
            )
          ),
          large = List(
            DisplayTrustLargeType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              organisationName = "Large 1",
              countryOfResidence = Some(GB),
              description = s"Description",
              description1 = Some("Description 1"),
              description2 = None,
              description3 = None,
              description4 = None,
              numberOfBeneficiary = "100",
              identification = Some(
                DisplayTrustIdentificationOrgType(
                  safeId = Some("8947584-94759745-84758745"),
                  utr = None,
                  address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), GB))
                )
              ),
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              entityStart = "2019-11-26"
            )
          ),
          other = List(
            DisplayTrustOtherType(
              lineNo = Some("1"),
              bpMatchStatus = Some("01"),
              description = s"Other 1",
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some("10"),
              countryOfResidence = Some(GB),
              address = Some(AddressType(s"line 1", "line 2", None, None, Some("NE11NE"), GB)),
              entityStart = "2019-11-26"
            )
          )
        )

        val ua = emptyUserAnswersForUtr.copy(is5mldEnabled = true, isUnderlyingData5mld = true)

        val extraction = beneficiaryExtractor.extract(ua, beneficiary)

        extraction.right.value.get(CompanyBeneficiaryNamePage(0)).get mustBe "Company 1"
        extraction.right.value.get(CompanyBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(CompanyBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(CompanyBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(CompanyBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(CompanyBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(CompanyBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CompanyBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CompanyBeneficiarySafeIdPage(0)) must be(defined)

        extraction.right.value.get(TrustBeneficiaryNamePage(0)).get mustBe "Trust 1"
        extraction.right.value.get(TrustBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(TrustBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(TrustBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(TrustBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(TrustBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(TrustBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(TrustBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(TrustBeneficiarySafeIdPage(0)) must be(defined)

        extraction.right.value.get(CharityBeneficiaryNamePage(0)).get mustBe "Charity 1"
        extraction.right.value.get(CharityBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(CharityBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(CharityBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(CharityBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(CharityBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(CharityBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(CharityBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(CharityBeneficiarySafeIdPage(0)) must be(defined)

        extraction.right.value.get(OtherBeneficiaryDescriptionPage(0)).get mustBe "Other 1"
        extraction.right.value.get(OtherBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(OtherBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(OtherBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(OtherBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(OtherBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(OtherBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(ClassOfBeneficiaryDescriptionPage(0)).get mustBe "Class Of Beneficiary 1"
        extraction.right.value.get(ClassOfBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(ClassOfBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(ClassOfBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")

        extraction.right.value.get(IndividualBeneficiaryNamePage(0)).get mustBe FullName("First Name", None, "Last Name")
        extraction.right.value.get(IndividualBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(IndividualBeneficiaryRoleInCompanyPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryVulnerableYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryDateOfBirthYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryDateOfBirthPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryCountryOfNationalityPage(0)).get mustBe GB
        extraction.right.value.get(IndividualBeneficiaryMentalCapacityYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryIncomeYesNoPage(0)).get mustBe true
        extraction.right.value.get(IndividualBeneficiaryIncomePage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryNationalInsuranceYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryNationalInsuranceNumberPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryAddressYesNoPage(0)).get mustBe false
        extraction.right.value.get(IndividualBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryAddressPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryPassportIDCardYesNoPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiaryPassportIDCardPage(0)) mustNot be(defined)
        extraction.right.value.get(IndividualBeneficiarySafeIdPage(0)) mustNot be(defined)

        extraction.right.value.get(LargeBeneficiaryNamePage(0)).get mustBe "Large 1"
        extraction.right.value.get(LargeBeneficiaryCountryOfResidenceYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryCountryOfResidenceInTheUkYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryCountryOfResidencePage(0)).get mustBe GB
        extraction.right.value.get(LargeBeneficiaryDescriptionPage(0)).get mustBe Description("Description", Some("Description 1"), None, None, None)
        extraction.right.value.get(LargeBeneficiaryDiscretionYesNoPage(0)).get mustBe false
        extraction.right.value.get(LargeBeneficiaryShareOfIncomePage(0)).get mustBe "10"
        extraction.right.value.get(LargeBeneficiaryAddressYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressUKYesNoPage(0)).get mustBe true
        extraction.right.value.get(LargeBeneficiaryAddressPage(0)).get mustBe UKAddress("line 1", "line 2", None, None, "NE11NE")
        extraction.right.value.get(LargeBeneficiaryUtrPage(0)) mustNot be(defined)
        extraction.right.value.get(LargeBeneficiaryMetaData(0)).get mustBe MetaData("1", Some("01"), "2019-11-26")
        extraction.right.value.get(LargeBeneficiarySafeIdPage(0)) must be(defined)
        extraction.right.value.get(LargeBeneficiaryNumberOfBeneficiariesPage(0)).get mustBe Over1

      }

    }

  }

}
