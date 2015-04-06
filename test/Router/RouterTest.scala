package Router

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

  "respond to the index Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "redirect to contact us" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/contact-us"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "redirect to blog" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/blog"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "redirect to FAQs" in {

    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/faq"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "redirect to discussion forum" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/discussionforum"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "submit mobile registration " in {
    running(FakeApplication()) {
      val result = route(FakeRequest(POST, "/mobileRegistration").withFormUrlEncodedBody(
       "userName" -> "sushil",
      "brandId" -> "1",
      "mobileModelId" -> "1",
      "imeiMeid" -> "864465028854206",
      "otherImeiMeid" -> "864465028854206",
      "purchaseDate" -> "12-03-2013",
      "contactNo" -> "8375919908",
      "email" -> "s@gmail.com",
      "regType" -> "stolen",
      "document" -> "s.jpg",
      "description" -> "test",
      "otherMobileBrand" -> "nokia",
      "otherMobileModel" -> "assha")).get
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

   // Java script routes
  "JavascriptRoutes Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/javascriptRoutes"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/javascript")
    }
  }
  
  "redirect mobile status form" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/mobileStatusForm"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

   // Admin Pages
  "redirect to login page with error flash" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/login").withFlash("error"->"message"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }
  
  // Admin Pages
  "redirect to login page with success flash" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/login").withFlash("success"->"message"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "authenticate login" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(POST, "/authenticate").withFormUrlEncodedBody("email" -> "admin", "password" -> "knol2013").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(303)
redirectLocation(result) must beSome.which(_ == "/admin/mobiles?status=pending")      
    }
  }

  "login authentication failed" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(POST, "/authenticate").withFormUrlEncodedBody("email" -> "test", "password" -> "pass").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(400)
    }
  }

  // Admin page
  "logout Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/logout"))
      status(result) must equalTo(303)
      redirectLocation(result) must beSome.which(_ == "/login")
    }
  }
  
  "redirect to audit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/admin/auditpage"))
      status(result) must equalTo(303)
    }
  }
}
