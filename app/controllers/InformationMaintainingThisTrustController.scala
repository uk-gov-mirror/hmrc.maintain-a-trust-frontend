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

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.actions.Actions
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.Session
import views.html.{AgentCannotAccessTrustYetView, InformationMaintainingThisTrustView}

@Singleton
class InformationMaintainingThisTrustController @Inject()(
                                                           actions: Actions,
                                                           val controllerComponents: MessagesControllerComponents,
                                                           maintainingTrustView: InformationMaintainingThisTrustView,
                                                           agentCannotAccessTrustYetView: AgentCannotAccessTrustYetView
                                                         )(implicit config: FrontendAppConfig)
  extends FrontendBaseController with I18nSupport {

  private val logger: Logger = Logger(getClass)

  def onPageLoad(): Action[AnyContent] = actions.verifiedForUtr {
    implicit request =>

      val utr = request.userAnswers.utr

      logger.info(s"[Session ID: ${Session.id(hc)}] showing information about this trust $utr")

      request.user.affinityGroup match {
        case Agent if !config.playbackEnabled => Ok(agentCannotAccessTrustYetView(utr))
        case _ => Ok(maintainingTrustView(utr))
      }
  }
}
