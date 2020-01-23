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

package views.status

import uk.gov.hmrc.auth.core.AffinityGroup
import views.behaviours.ViewBehaviours
import views.html.status.TrustClosedView

class TrustClosedViewSpec extends ViewBehaviours {

  val utr = "0987654321"

  "TrustClosed view" must {

    val view = viewFor[TrustClosedView](Some(emptyUserAnswers))

    val applyView = view.apply(AffinityGroup.Agent, utr)(fakeRequest, messages)

    behave like normalPage(
      applyView,
      "trustClosed",
      "p1",
      "p2",
      "contact.link",
      "p3",
      "p4",
      "return.link"
    )

    "display the correct subheading" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, messages("trustClosed.subheading", utr))
    }

  }

}