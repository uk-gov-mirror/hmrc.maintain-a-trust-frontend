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

package services

import models.http.DeclarationResponse.InternalServerError
import models.http.{DeclarationResponse, TVNResponse}
import models.requests.DataRequest
import models.{Declaration, UserAnswers}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class FakeDeclarationService extends DeclarationService {

  override def declareNoChange[A](utr: String, declaration: Declaration, request: DataRequest[A], arn: Option[String])
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[DeclarationResponse] = {
    Future.successful(TVNResponse("123456"))
  }

}

class FakeFailingDeclarationService extends DeclarationService {

  override def declareNoChange[A](utr: String, declaration: Declaration, request: DataRequest[A], arn: Option[String])
                              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[DeclarationResponse] = {
    Future.successful(InternalServerError)

  }

}
