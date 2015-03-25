/*
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

  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand = Brand("nokia", "12-17-2013")
  val model = Model("E5", 1)
  val mobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "864465028854206", "12-05-2013", "+91 8375919908",
    "ss@gmail.com", "stolen", Status.pending, "test", "12-17-2013", "sushil.png", "nokia", "E5")

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

  "posting mobile registration " in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(POST, "/mobileRegistration"))
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  *//**
   * Java script routes
   *//*
  "JavascriptRoutes Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/javascriptRoutes"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/javascript")
    }
  }
  
  "redirect mobile status form" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/mobileStatus"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }


  *//**
   * Admin Pages
   *//*

  "redirect to login" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/login"))
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
      contentType(result) must be(None)
    }
  }
  
  "redirect to audit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/admin/auditpage"))
      status(result) must equalTo(303)
      contentType(result) must be(None)

    }
  }

}
*/