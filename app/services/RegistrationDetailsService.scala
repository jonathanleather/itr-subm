/*
 * Copyright 2016 HM Revenue & Customs
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

import connectors.RegistrationDetailsConnector
import play.Logger
import play.api.mvc.Results._
import play.api.mvc.Result
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

object RegistrationDetailsService extends RegistrationDetailsService {
  override lazy val registrationDetailsConnector = RegistrationDetailsConnector
}

trait RegistrationDetailsService {

  val registrationDetailsConnector: RegistrationDetailsConnector

  def getRegistrationDetails(safeID: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    registrationDetailsConnector.getRegistrationDetails(safeID).map {
      result => {
        Logger.info(s"[RegistrationDetailsService][getRegistrationDetails] - Response is" + result.status + " Json is " + result.json)
        Status(result.status)(result.json)
      }

    }.recover {
      case _ => {
        Logger.warn(s"[RegistrationDetailsService][getRegistrationDetails] - Failed to retrieve reg details")
        InternalServerError
      }
    }
  }

}