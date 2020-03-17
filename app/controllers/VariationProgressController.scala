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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.TrustsStoreConnector
import controllers.actions.AuthenticateForPlayback
import models.Enumerable
import models.pages.Tag
import models.pages.Tag.UpToDate
import navigation.DeclareNoChange
import pages.UTRPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import viewmodels.tasks.{Beneficiaries, NaturalPeople, Settlors, Trustees}
import viewmodels.{Link, Task}
import views.html.VariationProgressView

import scala.concurrent.{ExecutionContext, Future}


class VariationProgressController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      playbackRepository: PlaybackRepository,
                                      actions: AuthenticateForPlayback,
                                      view: VariationProgressView,
                                      val controllerComponents: MessagesControllerComponents,
                                      config: FrontendAppConfig,
                                      storeConnector: TrustsStoreConnector
                                    )(implicit ec: ExecutionContext) extends DeclareNoChange with I18nSupport with Enumerable.Implicits {



  def onPageLoad(): Action[AnyContent] = actions.verifiedForUtr.async {
    implicit request =>

      val notYetAvailable = controllers.makechanges.routes.UnavailableSectionsController.onPageLoad().url

      request.userAnswers.get(UTRPage) match {
        case Some(utr) =>

          storeConnector.getStatusOfTasks(utr) map {
            tasks =>

              val mandatorySections = List(
                Task(Link(Settlors, notYetAvailable), Some(UpToDate)),
                Task(Link(Trustees, config.maintainTrusteesUrl(utr)), Some(Tag.tagFor(tasks.trustees))),
                Task(Link(Beneficiaries, notYetAvailable), Some(UpToDate))
              )

              val optionalSections = List(
                Task(Link(NaturalPeople, notYetAvailable), Some(UpToDate))
              )

              val next = if (request.user.affinityGroup == Agent) {
                controllers.declaration.routes.AgencyRegisteredAddressUkYesNoController.onPageLoad().url
              } else {
                controllers.declaration.routes.IndividualDeclarationController.onPageLoad().url
              }

              Ok(view(utr,
                mandatorySections,
                optionalSections,
                request.user.affinityGroup,
                next,
                isAbleToDeclare = tasks.trustees
              ))

          }
        case _ =>
          Future.successful(Redirect(routes.UTRController.onPageLoad()))
      }
  }
}