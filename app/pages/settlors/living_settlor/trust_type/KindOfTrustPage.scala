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

package pages.settlors.living_settlor.trust_type

import models.UserAnswers
import models.pages.KindOfTrust
import models.pages.KindOfTrust.Intervivos
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.settlors.Settlors

import scala.util.Try

case object KindOfTrustPage extends QuestionPage[KindOfTrust] {

  override def path: JsPath = Settlors.path \ toString

  override def toString: String = "kindOfTrust"

  override def cleanup(value: Option[KindOfTrust], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Intervivos) =>
        super.cleanup(value, userAnswers)
      case _ =>
        userAnswers.remove(HoldoverReliefYesNoPage)
    }
  }
}
