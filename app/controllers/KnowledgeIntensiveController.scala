/*
 * Copyright 2017 HM Revenue & Customs
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

import auth.{Authorisation, Authorised, NotAuthorised}
import connectors.AuthConnector
import play.api.libs.json._
import services.KnowledgeIntensiveService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import play.api.mvc._

object KnowledgeIntensiveController extends KnowledgeIntensiveController {
  override val authConnector: AuthConnector = AuthConnector
}

trait KnowledgeIntensiveController extends BaseController with Authorisation {

  def checkKICosts(operatingCosts1stYear: Int,operatingCosts2ndYear: Int,operatingCosts3rdYear: Int,
                   rAndDCosts1stYear:Int, rAndDCosts2ndYear:Int, rAndDCosts3rdYear:Int): Action[AnyContent] = Action.async {
    implicit request => authorised {
      case Authorised => Future.successful(Ok(Json.toJson(KnowledgeIntensiveService.validateKICosts(operatingCosts1stYear,
        operatingCosts2ndYear, operatingCosts3rdYear, rAndDCosts1stYear, rAndDCosts2ndYear, rAndDCosts3rdYear))))
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  def checkSecondaryConditions(hasPercentageWithMasters: Boolean,
                               hasTenYearPlan: Boolean):  Action[AnyContent] = Action.async {
    implicit request => authorised {
      case Authorised => Future.successful(Ok(Json.toJson(KnowledgeIntensiveService.validateSecondaryKIConditions(hasPercentageWithMasters, hasTenYearPlan))))
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

}
