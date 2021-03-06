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


import common.Constants
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import play.api.http.Status._
import play.api.mvc.Result
import play.api.mvc.Results._


class EmployeeFullTimeEquivalenceServiceSpec extends UnitSpec{

  val negativeNumberMessage = "Negative Number"
  val invalidNumberMessage = "Invalid Number"

  val successResponse: Boolean => Result = bool => Status(OK)(Json.toJson(bool))
  val failedResponse: String => Result = reason => Status(BAD_REQUEST)(Json.toJson(Map("error"->"Invalid URL parameter",
    "reason" -> reason)))

  "EmployeeFullTimeEquivalenceService fullTimeEquivalenceLimit for SEIS" should {
    "return true when the number of fte's is on the limit" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "25") shouldBe successResponse(true)
    }
    "return true when the number of fte's is under the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "24.9999999999999") shouldBe successResponse(true)
    }
    "return fasle when the number of fte's is over the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "25.0000000000001") shouldBe successResponse(false)
    }
    "return true when the number of fte's is 0" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "0") shouldBe successResponse(true)
    }
    "return true when the number of fte's is greater than 0 on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "0.0000000000001") shouldBe successResponse(true)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's cannot be converted to a valid number" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "INVALID_NUMBER") shouldBe failedResponse(invalidNumberMessage)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's is negative" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeSEIS, "-0.00000001") shouldBe failedResponse(negativeNumberMessage)
    }
  }

  "EmployeeFullTimeEquivalenceService fullTimeEquivalenceLimit for EIS" should {
    "return true when the number of fte's is on the limit" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "250") shouldBe successResponse(true)
    }
    "return true when the number of fte's is under the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "249.9999999999999") shouldBe successResponse(true)
    }
    "return fasle when the number of fte's is over the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "250.0000000000001") shouldBe successResponse(false)
    }
    "return true when the number of fte's is 0" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "0") shouldBe successResponse(true)
    }
    "return true when the number of fte's is greater than 0 on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "0.0000000000001") shouldBe successResponse(true)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's cannot be converted to a valid number" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "INVALID_NUMBER") shouldBe failedResponse(invalidNumberMessage)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's is negative" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEIS, "-0.00000001") shouldBe failedResponse(negativeNumberMessage)
    }
  }

  "EmployeeFullTimeEquivalenceService fullTimeEquivalenceLimit for EIS with KI" should {
    "return true when the number of fte's is on the limit" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "500") shouldBe successResponse(true)
    }
    "return true when the number of fte's is under the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "499.9999999999999") shouldBe successResponse(true)
    }
    "return fasle when the number of fte's is over the limit on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "500.0000000000001") shouldBe successResponse(false)
    }
    "return true when the number of fte's is 0" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "0") shouldBe successResponse(true)
    }
    "return true when the number of fte's is greater than 0 on the edge" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "0.0000000000001") shouldBe successResponse(true)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's cannot be converted to a valid number" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "INVALID_NUMBER") shouldBe failedResponse(invalidNumberMessage)
    }
    "return INTERNAL_SERVER ERROR when the number of fte's is negative" in {
      EmployeeFullTimeEquivalenceService.checkFullTimeEquivalence(Constants.schemeTypeEISWithKI, "-0.00000001") shouldBe failedResponse(negativeNumberMessage)
    }
  }
}
