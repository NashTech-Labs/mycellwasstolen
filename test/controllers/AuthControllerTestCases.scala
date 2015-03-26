package controllers

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentType
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import model.repository.MobileRepository

class AuthControllerTestCases extends Specification with Mockito {
  val mockedMobileRepo = mock[MobileRepository]
  val authController = new AuthController
  
  "login" in {
    val result = authController.login(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }

  "authenticate" in {
    running(FakeApplication()) {
      val result = authController.authenticate(FakeRequest())
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html") 
    }
  }

  "logout" in {
    running(FakeApplication()) {
      val result = authController.logout(FakeRequest())
      status(result) must equalTo(303)
    }
  }
}
