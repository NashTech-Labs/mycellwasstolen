package controllers

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import model.repository.User
//import com.amazonaws.services.identitymanagement.model.User

class ApplicationTestCases extends Specification with Mockito {
  
  "ApplicationController: index" in {
    running(FakeApplication()) {
      val user = User("test@gmail.com", "testpass")
      val result = Application.index(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "ApplicationController: javascriptRoutes" in {
    running(FakeApplication()) {
      val result = Application.javascriptRoutes(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/javascript")
    }
  }
}
