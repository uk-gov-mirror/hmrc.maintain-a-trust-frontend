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

package utils

import models.pages.RoleInCompany
import models.pages.RoleInCompany.NA
import models.{Address, Description, InternationalAddress, PassportOrIdCardDetails, UKAddress}
import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import play.twirl.api.HtmlFormat.escape
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.language.LanguageUtils
import utils.countryoptions.CountryOptions

import java.time.{LocalDate => JavaDate}
import javax.inject.Inject
import scala.util.Try

class CheckAnswersFormatters @Inject()(languageUtils: LanguageUtils)
                                      (implicit countryOptions: CountryOptions) {

  def formatDate(date: JavaDate)(implicit messages: Messages): String = {
    val convertedDate: JodaDate = new JodaDate(date.getYear, date.getMonthValue, date.getDayOfMonth)
    languageUtils.Dates.formatDate(convertedDate)
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      escape(messages("site.yes"))
    } else {
      escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): Html = {
    val formatted = Try(Nino(nino.toUpperCase).formatted).getOrElse(nino.toUpperCase)
    escape(formatted)
  }

  def country(code: String)(implicit messages: Messages): Html =
    escape(countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse(""))

  def currency(value: String): Html = escape(s"£$value")

  def percentage(value: String): Html = escape(s"$value%")

  def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        address.line4.map(escape),
        Some(escape(address.postcode))
      ).flatten

    breakLines(lines)
  }

  def internationalAddress(address: InternationalAddress)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        Some(country(address.country))
      ).flatten

    breakLines(lines)
  }

  def addressFormatter(address: Address)(implicit messages: Messages): Html = {
    address match {
      case a:UKAddress => ukAddress(a)
      case a:InternationalAddress => internationalAddress(a)
    }
  }

  def passportOrIDCard(passportOrIdCard: PassportOrIdCardDetails)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(country(passportOrIdCard.country)),
        Some(escape(passportOrIdCard.cardNumber)),
        Some(escape(formatDate(passportOrIdCard.expiryDate)))
      ).flatten

    breakLines(lines)
  }

  def formatRoleInCompany(answer: RoleInCompany)(implicit messages: Messages): Html = {
    answer match {
      case NA => escape(messages("individualBeneficiary.roleInCompany.checkYourAnswersLabel.na"))
      case _ => formatEnum("individualBeneficiary.roleInCompany", answer)
    }
  }

  def formatEnum[T](key: String, answer: T)(implicit messages: Messages): Html = {
    escape(messages(s"$key.$answer"))
  }

  def description(description: Description): Html = {
    val lines = Seq(
      Some(HtmlFormat.escape(description.description)),
      description.description1.map(HtmlFormat.escape),
      description.description2.map(HtmlFormat.escape),
      description.description3.map(HtmlFormat.escape),
      description.description4.map(HtmlFormat.escape)
    ).flatten

    breakLines(lines)
  }

  private def breakLines(lines: Seq[Html]): Html = {
    Html(lines.mkString("<br />"))
  }

}
