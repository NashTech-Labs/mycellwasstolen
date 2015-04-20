package routes

import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Security
import play.api.cache.Cache
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Play.current
import model.repository.Brand
import model.repository.Model
import model.repository.Mobile
import utils.StatusUtil.Status
import model.repository.MobileRepository
import model.repository.BrandRepository
import model.repository.ModelRepository
import model.repository.AuditRepository

class RouterTest extends Specification {

  // user routes
  
  "redirect to user home page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }
  
  "redirect mobile registration " in {
    running(FakeApplication()) {
      val result = route(FakeRequest(POST, "/save_users").withFormUrlEncodedBody(
        "userName" -> "sushil",
        "brandId" -> "1",
        "modelId" -> "1",
        "imei" -> "864465028854206",
        "otherImei" -> "864465028854206",
        "contactNo" -> "8375919908",
        "email" -> "s@gmail.com",
        "regType" -> "stolen",
        "document" -> "s.jpg")).get
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "JavascriptRoutes Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/javascriptRoutes"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/javascript")
    }
  }

  "redirect mobile status form" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/check_mobile_status"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  // Admin routes
  
  "redirect to admin home page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/admin"))
      status(result) must equalTo(303)
    }
  }
  
  "redirect to requestsList page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/requests?status=pending"))
      status(result) must equalTo(303)
    }
  }
  
  "redirect to audit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/timestamps"))
      status(result) must equalTo(303)
    }
  }
  
}
