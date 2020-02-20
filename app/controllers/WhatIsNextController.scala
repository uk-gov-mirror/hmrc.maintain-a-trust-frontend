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

import controllers.actions.AuthenticateForPlayback
import forms.WhatIsNextFormProvider
import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import models.Enumerable
import models.pages.WhatIsNext
import models.requests.DataRequest
import pages.{UTRPage, WhatIsNextPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.WhatIsNextView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsNextController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      playbackRepository: PlaybackRepository,
                                      actions: AuthenticateForPlayback,
                                      formProvider: WhatIsNextFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: WhatIsNextView,
                                      config: FrontendAppConfig
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = actions.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhatIsNextPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.verifiedForUtr.async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsNextPage, value))
            _ <- playbackRepository.set(updatedAnswers)
          } yield value match {
            case WhatIsNext.DeclareTheTrustIsUpToDate =>
              redirectToDeclaration()

            case WhatIsNext.MakeChanges if (config.maintainTrusteeEnabled) =>
              redirectToMaintainTrustees()

            case _ =>
              Redirect(controllers.routes.FeatureNotAvailableController.onPageLoad())
          }
        }
      )
  }

  private def redirectToMaintainTrustees()(implicit request: DataRequest[AnyContent]) = {
    request.userAnswers.get(UTRPage) map {
      utr =>
        val url = s"${config.maintainATrusteeFrontendUrl}/$utr"
        Redirect(Call("GET", url))
    } getOrElse {
      Redirect(routes.UTRController.onPageLoad())
    }
  }

  private def redirectToDeclaration()(implicit request: DataRequest[AnyContent]) = {
    request.user.affinityGroup match {
      case Agent =>
        Redirect(controllers.declaration.routes.AgencyRegisteredAddressUkYesNoController.onPageLoad())
      case _ =>
        Redirect(controllers.declaration.routes.IndividualDeclarationController.onPageLoad())
    }
  }
}
