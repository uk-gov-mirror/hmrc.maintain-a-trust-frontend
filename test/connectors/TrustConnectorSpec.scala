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

import java.time.LocalDate

import base.SpecBaseHelpers
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import models.http.DeclarationResponse.InternalServerError
import models.http._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Inside, MustMatchers, OptionValues}
import play.api.http.Status
import play.api.libs.json.{JsBoolean, Json}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

class TrustConnectorSpec extends FreeSpec with MustMatchers
  with OptionValues with Generators with SpecBaseHelpers with WireMockHelper with ScalaFutures with Inside {
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private def playbackUrl(utr: String) : String = s"/trusts/$utr/transformed"
  private def declareUrl(utr: String) : String = s"/trusts/declare/$utr"

  "TrustConnector" - {

    "playback data must" - {

      "return TrustFound response" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        val utr = "10000000008"

        server.stubFor(
          get(urlEqualTo(playbackUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody("""{
                            |
                            |  "responseHeader": {
                            |    "status": "In Processing",
                            |    "formBundleNo": "1"
                            |  }
                            |}""".stripMargin)
            )
        )

        val result  = Await.result(connector.playback(utr),Duration.Inf)
        result mustBe Processing

        application.stop()
      }

      "return NoContent response" in {

        val utr = "6666666666"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(playbackUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.NO_CONTENT)))

        val result  = Await.result(connector.playback(utr),Duration.Inf)
        result mustBe SorryThereHasBeenAProblem

        application.stop()
      }

      "return NotFound response" in {

        val utr = "10000000008"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(playbackUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)))

        val result  = Await.result(connector.playback(utr),Duration.Inf)
        result mustBe UtrNotFound

        application.stop()
      }

      "return ServiceUnavailable response" in {

        val utr = "10000000008"

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(playbackUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.SERVICE_UNAVAILABLE)))

        val result  = Await.result(connector.playback(utr), Duration.Inf)
        result mustBe TrustServiceUnavailable

        application.stop()
      }

      "must return playback data inside a Processed trust" in {
        val utr = "1000000007"
        val payload = Source.fromFile(getClass.getResource("/display-trust.json").getPath).mkString

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(playbackUrl(utr)))
            .willReturn(okJson(payload))
        )

        val processed = Await.result(connector.playback(utr), Duration.Inf)

        inside(processed) {
          case Processed(data, bundleNumber) =>

            bundleNumber mustBe "000012345678"

            data.matchData.utr mustBe "1000000007"

            data.correspondence.name mustBe "Trust of Brian Cloud"

            data.declaration.name mustBe NameType("Agent", None, "Agency")

            data.trust.entities.leadTrustee.leadTrusteeInd.value.name mustBe NameType("Lead", None, "Trustee")

            data.trust.details.startDate mustBe LocalDate.of(2016, 4, 6)

            data.trust.entities.trustees.value.head.trusteeInd.value.lineNo mustBe Some("1")
            data.trust.entities.trustees.value.head.trusteeInd.value.identification.value.nino.value mustBe "JS123456A"
            data.trust.entities.trustees.value.head.trusteeInd.value.entityStart mustBe "2019-02-28"

            data.trust.entities.settlors.value.settlorCompany.value.head.name mustBe "Settlor Org 01"

            data.trust.entities.protectors.value.protectorCompany.head.lineNo mustBe Some("1")
            data.trust.entities.protectors.value.protectorCompany.head.name mustBe "Protector Org 01"
            data.trust.entities.protectors.value.protectorCompany.head.entityStart mustBe "2019-03-05"

            data.trust.assets.propertyOrLand.head.buildingLandName.value mustBe "Land of Brian Cloud"
        }

        application.stop()
      }
    }

    "declare no change must" - {

      "return TVN on success" in {
        val utr = "1000000007"
        val response = Json.parse(
          """
            |{
            | "tvn": "2345678"
            |}
            |""".stripMargin)

        val payload = Json.parse(
          s"""
             |{
             | "name": {
             |   "firstName": "John",
             |   "lastName": "Smith"
             | },
             | "address": {
             |   "line1": "Line 1",
             |   "line2": "Line 2",
             |   "postCode": "NE981ZZ",
             |   "country": "GB"
             | }
             |}
             |""".stripMargin)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          post(urlEqualTo(declareUrl(utr)))
            .willReturn(okJson(Json.stringify(response)).withStatus(Status.OK))
        )

        val result = Await.result(connector.declare(utr, payload), Duration.Inf)

        result mustEqual TVNResponse("2345678")

      }

      "return an error for non-success response" in {
        val utr = "1000000007"
        val payload = Json.parse(
          s"""
             |{
             | "name": {
             |   "firstName": "John",
             |   "lastName": "Smith"
             | },
             | "address": {
             |   "line1": "Line 1",
             |   "line2": "Line 2",
             |   "postCode": "NE981ZZ",
             |   "country": "GB"
             | }
             |}
             |""".stripMargin)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          post(urlEqualTo(declareUrl(utr)))
            .willReturn(
              aResponse()
                .withStatus(Status.SERVICE_UNAVAILABLE)))

        val result = Await.result(connector.declare(utr, payload), Duration.Inf)

        result mustEqual InternalServerError
      }
    }

    "get whether protectors already exist must" - {

      "Return true or false when the request is successful" in {

        val utr = "1000000008"
        val json = JsBoolean(true)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(s"/trusts/$utr/transformed/protectors-already-exist"))
            .willReturn(okJson(json.toString))
        )

        val processed = connector.getDoProtectorsAlreadyExist(utr)

        whenReady(processed) {
          result =>
            result.value mustBe true
        }

        application.stop()
      }
    }

    "get whether other individuals already exist must" - {

      "return true or false when the request is successful" in {

        val utr = "1000000008"
        val json = JsBoolean(true)

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustConnector]

        server.stubFor(
          get(urlEqualTo(s"/trusts/$utr/transformed/other-individuals-already-exist"))
            .willReturn(okJson(json.toString))
        )

        val processed = connector.getDoOtherIndividualsAlreadyExist(utr)

        whenReady(processed) {
          result =>
            result.value mustBe true
        }

        application.stop()
      }
    }
  }

}
