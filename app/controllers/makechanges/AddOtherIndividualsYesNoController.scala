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

package controllers.makechanges

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import connectors.TrustsStoreConnector
import controllers.actions._
import forms.YesNoFormProvider
import navigation.MakeChangesQuestionController
import pages.makechanges._
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import repositories.PlaybackRepository
import views.html.makechanges.AddOtherIndividualsYesNoView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddOtherIndividualsYesNoController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        playbackRepository: PlaybackRepository,
                                        actions: AuthenticateForPlayback,
                                        yesNoFormProvider: YesNoFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: AddOtherIndividualsYesNoView,
                                        config: FrontendAppConfig,
                                        trustStoreConnector: TrustsStoreConnector
                                     )(implicit ec: ExecutionContext)
  extends MakeChangesQuestionController(trustStoreConnector){

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("addOtherIndividuals")

  def onPageLoad(): Action[AnyContent] = actions.verifiedForUtr {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddOrUpdateOtherIndividualsYesNoPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddOrUpdateOtherIndividualsYesNoPage, value))
            _ <- playbackRepository.set(updatedAnswers)
            route <- decideNexRouteFromAnswers(updatedAnswers)
          } yield route
        }
    )
  }
}
