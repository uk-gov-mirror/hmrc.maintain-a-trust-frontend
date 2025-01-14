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

package connectors

import config.FrontendAppConfig

import javax.inject.Inject
import models.{CompletedMaintenanceTasks, FeatureResponse, UserAnswers}
import play.api.libs.json.{JsBoolean, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class TrustsStoreConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val trustLockedUrl: String = config.trustsStoreUrl + "/claim"

  private def maintainTasksUrl(utr: String) = s"${config.trustsStoreUrl}/maintain/tasks/$utr"

  private def featuresUrl(feature: String) = s"${config.trustsStoreUrl}/features/$feature"

  def get(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[TrustClaim]] = {
    http.GET[Option[TrustClaim]](trustLockedUrl)(TrustClaim.httpReads(utr), hc, ec)
  }

  def set(utr: String, userAnswers: UserAnswers)
         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[CompletedMaintenanceTasks] = {
    CompletedMaintenanceTasks.from(userAnswers) match {
      case Some(x) =>
        http.POST[JsValue, CompletedMaintenanceTasks](maintainTasksUrl(utr), Json.toJson(x))
      case None =>
        Future.failed(new RuntimeException("Unable to set tasks status"))
    }
  }

  def getStatusOfTasks(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[CompletedMaintenanceTasks] = {
    http.GET[CompletedMaintenanceTasks](maintainTasksUrl(utr))
      .recover {
        case _ => CompletedMaintenanceTasks()
      }
  }

  def getFeature(feature: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FeatureResponse] = {
    http.GET[FeatureResponse](featuresUrl(feature))
  }

  def setFeature(feature: String, state: Boolean)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.PUT[JsValue, HttpResponse](featuresUrl(feature), JsBoolean(state))
  }

}
