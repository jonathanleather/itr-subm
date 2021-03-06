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

package services

import connectors.SubmissionDESConnector
import play.api.Logger
import uk.gov.hmrc.play.http._

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{Json, JsValue}
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }


object SubmissionService extends SubmissionService{
  val submissionDESConnector: SubmissionDESConnector = SubmissionDESConnector
}

trait SubmissionService {

  val submissionDESConnector: SubmissionDESConnector

  def submitAA(jsonValue:JsValue, tavcReferenceId:String)
              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    submissionDESConnector.submit(jsonValue, tavcReferenceId)
  }

  def submitCS(jsonValue:JsValue, tavcReferenceId:String)
              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    submissionDESConnector.submit(jsonValue, tavcReferenceId)
  }

  def getReturnsSummary(tavcReferenceId:String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    submissionDESConnector.getReturnsSummary(tavcReferenceId)
  }
}
