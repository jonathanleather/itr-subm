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
import common.{CSAuditConstants, AAAuditConstants}
import connectors.AuthConnector
import metrics.MetricsEnum
import model.Error
import models.{AASubmissionDataForAuditModel, CSSubmissionDataForAuditModel}
import play.api.Logger
import play.api.libs.json._
import services.{AuditService, SubmissionService}
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.math._

object SubmissionController extends SubmissionController{
  val submissionService: SubmissionService= SubmissionService
  override val authConnector: AuthConnector = AuthConnector
  val auditService = AuditService
}

trait SubmissionController extends BaseController with Authorisation {

  val submissionService: SubmissionService
  val auditService : AuditService

  def submitAA(tavcReferenceId: String): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    authorised {
      case Authorised => {
        if (acknowledgementReferenceCheck(request.body)) {
          val acknowledgementRef = generateAcknowledgementRef(tavcReferenceId)
          val bodyWithRef = insertAcknowledgementRef(request.body.as[JsObject], acknowledgementRef)

          val timerContext = AuditService.metrics.startTimer(MetricsEnum.TAVC_SUBMISSION)
          val auditData = bodyWithRef.as[AASubmissionDataForAuditModel]

          submissionService.submitAA(bodyWithRef, tavcReferenceId).recover{
            case exception: Exception => {
              val auditHttpResponse = handleSubmissionException(exception)
              auditService.sendTAVCAdvancedAssuranceEvent(auditData, tavcReferenceId, auditHttpResponse, acknowledgementRef)
              auditService.logSubscriptionResponseAA(auditHttpResponse, "SubmissionController", "submitAA", tavcReferenceId)
              val stopContext = timerContext.stop()
              throw exception
            }
          } map { responseReceived =>
            auditService.sendTAVCAdvancedAssuranceEvent(auditData, tavcReferenceId, responseReceived, acknowledgementRef)
            auditService.logSubscriptionResponseAA(responseReceived, "SubmissionController", "submitAA", tavcReferenceId)
            val stopContext = timerContext.stop()
            Status(responseReceived.status)(responseReceived.body)
          }
        }
        else {
          Future.successful(BadRequest(Json.toJson(Error(
            message = "Request to submit application failed with validation errors:" +
              "acknowledgementReference should not be present in Json request"))))
        }
      }
      case NotAuthorised => {
        Logger.warn(s"[SubmissionController] [submitAA] - Received an unauthorised request. tavcReferenceId is $tavcReferenceId")
        Future.successful(Forbidden)
      }
    }
  }

  def submitCS(tavcReferenceId: String): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    authorised {
      case Authorised => {
        if (acknowledgementReferenceCheck(request.body)) {
          val acknowledgementRef = generateAcknowledgementRef(tavcReferenceId)
          val bodyWithRef = insertAcknowledgementRef(request.body.as[JsObject], acknowledgementRef)

          val timerContext = AuditService.metrics.startTimer(MetricsEnum.TAVC_SUBMISSION_CS)
          val auditData = bodyWithRef.as[CSSubmissionDataForAuditModel]

          submissionService.submitCS(bodyWithRef, tavcReferenceId) map { responseReceived =>
            auditService.sendTAVCSubmitComplianceStatementEvent(auditData, tavcReferenceId, responseReceived, acknowledgementRef)
            auditService.logSubscriptionResponseCS(responseReceived, "SubmissionController", "submitCS", tavcReferenceId)
            val stopContext = timerContext.stop()
            Status(responseReceived.status)(responseReceived.body)
          }
        }
        else {
          Future.successful(BadRequest(Json.toJson(Error(
            message = "Request to submit application failed with validation errors:" +
              "acknowledgementReference should not be present in Json request"))))
        }
      }
      case NotAuthorised => {
        Logger.warn(s"[SubmissionController] [submitAA] - Received an unauthorised request. tavcReferenceId is $tavcReferenceId")
        Future.successful(Forbidden)
      }
    }
  }



  def getAASubmissionDetails(tavcReferenceNumber: String):Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => {
        Logger.info(s"[SubmissionController][getAASubmissionSummary]")
        submissionService.getAASubmissionDetails(tavcReferenceNumber) map { responseReceived =>
          Status(responseReceived.status)(responseReceived.body)
        }
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  /** acknowledgementReference should not be present. It's generated and inserted. **/
  private def acknowledgementReferenceCheck(requestBody: JsValue): Boolean = {
    (requestBody \ "acknowledgementReference").asOpt[String] match {
      case Some(data) => false
      case None => true
    }
  }

  /** Randomly generate acknowledgementReference, must be between 1 and 32 characters long **/
  private def generateAcknowledgementRef(tavcReferenceId: String): String = {
    val ackRef = tavcReferenceId concat (System.currentTimeMillis / 1000).toString
    ackRef.substring(0, min(ackRef.length(), 31));
  }

  /** Inject the generated acknowledgementReference into the Json Request **/
  private def insertAcknowledgementRef(requestBody: JsObject, acknowledgementRef: String): JsValue = {
    val ref = JsObject(Seq(("acknowledgementReference", JsString(acknowledgementRef))))
    (ref ++ requestBody).as[JsValue]
  }


  private def handleSubmissionException(exception: Exception): HttpResponse = {
    exception match {
      case exception: BadRequestException => HttpResponse(exception.responseCode)
      case exception: NotFoundException => HttpResponse(exception.responseCode)
      case Upstream4xxResponse(message, status, _, _) => HttpResponse(status)
      case Upstream5xxResponse(message, status, _) => HttpResponse(status)
      case _ => throw exception
    }
  }
}
