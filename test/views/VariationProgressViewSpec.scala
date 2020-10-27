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

package views

import models.pages.Tag.{InProgress, UpToDate}
import sections.Protectors
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import viewmodels.tasks._
import viewmodels.{Link, Task}
import views.behaviours.{VariationsProgressViewBehaviours, ViewBehaviours}
import views.html.VariationProgressView

class VariationProgressViewSpec extends ViewBehaviours with VariationsProgressViewBehaviours {

  val expectedContinueUrl = controllers.declaration.routes.IndividualDeclarationController.onPageLoad().url

  "VariationProgress view" must {

    val utr = "utr"

    val mandatorySections = List(
      Task(Link(Settlors, ""), None),
      Task(Link(Trustees, ""), None),
      Task(Link(Beneficiaries, ""), None)
    )
    val optionalSections = List(
      Task(Link(NaturalPeople, ""),None))

    val group = Organisation

    val userAnswers = emptyUserAnswers

    val view = viewFor[VariationProgressView](Some(userAnswers))

    val applyView = view.apply(utr, mandatorySections, optionalSections, group, expectedContinueUrl, isAbleToDeclare = false, closingTrust = false)(fakeRequest, messages)

    behave like normalPageTitleWithCaption(applyView,
      "variationProgress",
      utr,
    "p1", "p2")

    behave like pageWithBackLink(applyView)

    behave like taskListHeading(applyView)

    behave like taskList(applyView, mandatorySections)
    behave like taskList(applyView, optionalSections)

  }

  "render summary" when {

    "all sections are completed" in {

        val utr = "utr"

        val mandatorySections = List(
          Task(Link(Settlors, "http://localhost:9795/maintain-a-trust/settlors/utr"), Some(UpToDate)),
          Task(Link(Trustees, "http://localhost:9792/maintain-a-trust/trustees/utr"), Some(UpToDate)),
          Task(Link(Beneficiaries, "http://localhost:9793/maintain-a-trust/beneficiaries/utr"), Some(UpToDate))
        )
        val optionalSections = List(
          Task(Link(Protectors, "http://localhost:9796/maintain-a-trust/protectors/utr"), Some(UpToDate)),
          Task(Link(NaturalPeople, "http://localhost:9799/maintain-a-trust/other-individuals/utr"), Some(UpToDate))
        )

        val group = Organisation

        val userAnswers = emptyUserAnswers

        val view = viewFor[VariationProgressView](Some(userAnswers))

        val applyView = view.apply(utr, mandatorySections, optionalSections, group, expectedContinueUrl, isAbleToDeclare = true, closingTrust = false)(fakeRequest, messages)

        val doc = asDocument(applyView)

        assertRenderedById(doc, "summary-heading")
        assertRenderedById(doc, "summary-paragraph")
        assertRenderedById(doc, "summary-heading-2")
        assertRenderedById(doc, "summary-paragraph-2")
        assertRenderedById(doc, "print-and-save")

      }
    }

}
