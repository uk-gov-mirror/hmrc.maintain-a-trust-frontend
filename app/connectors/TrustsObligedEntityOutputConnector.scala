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

package connectors

import config.FrontendAppConfig
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpVerbs.GET
import uk.gov.hmrc.http.HttpClient

import javax.inject.Inject
import scala.concurrent.Future

class TrustsObligedEntityOutputConnector @Inject()(http: HttpClient, ws: WSClient, config: FrontendAppConfig) {


  private def getPdfUrl(identifier: String) = s"${config.trustsObligedEntityOutputUrl}/trusts-obliged-entity-output/get-pdf/$identifier"


  def getPdf(identifier : String)(implicit hc : HeaderCarrier): Future[WSResponse] = {
    ws.url(getPdfUrl(identifier)).withMethod(GET).withHttpHeaders(hc.headers: _*).stream()
  }

}
