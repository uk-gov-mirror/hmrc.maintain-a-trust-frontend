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

package controllers.close.nontaxable

import connectors.{TrustConnector, TrustsStoreConnector}
import controllers.actions.Actions
import controllers.makechanges.MakeChangesQuestionRouterController
import forms.DateFormProvider
import pages.close.nontaxable.DateClosedPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.PlaybackRepository
import views.html.close.nontaxable.DateClosedView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DateClosedController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      playbackRepository: PlaybackRepository,
                                      actions: Actions,
                                      formProvider: DateFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: DateClosedView,
                                      trustConnector: TrustConnector,
                                      trustsStoreConnector: TrustsStoreConnector
                                    )(implicit ec: ExecutionContext)
  extends MakeChangesQuestionRouterController(trustConnector, trustsStoreConnector) {

  private val prefix: String = "dateClosed"

  def onPageLoad(): Action[AnyContent] = actions.verifiedForIdentifier.async {
    implicit request =>

      for {
        startDate <- trustConnector.getStartDate(request.userAnswers.identifier)
      } yield {
        val form = formProvider.withPrefixAndTrustStartDate(prefix, startDate)

        val preparedForm = request.userAnswers.get(DateClosedPage) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm))
      }
  }

  def onSubmit(): Action[AnyContent] = actions.verifiedForIdentifier.async {
    implicit request =>

      def render(startDate: LocalDate): Future[Result] = {
        val form = formProvider.withPrefixAndTrustStartDate(prefix, startDate)

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(DateClosedPage, value))
              _ <- playbackRepository.set(updatedAnswers)
            } yield Redirect(redirectToFirstUpdateQuestion)
        )
      }

      for {
        startDate <- trustConnector.getStartDate(request.userAnswers.identifier)
        result <- render(startDate)
      } yield result
  }
}
