package controllers

import model.domains.Domain._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.Logger
import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.mockito.Mockito._
import org.specs2.mutable.script.Specification

class MobileControllerTestCases extends Specification with Mockito {
  
  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand=Brand("nokia",date)
  val model=MobileModels("N72",6)
  val mobileUser = Mobile(
      "gs", "nokia", "glaxacy", "12345678901234", new java.sql.Date(new java.util.Date().getTime()), "983131313",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd")
  
  test("MobileControllerTesting: mobileRegistrationForm") {
    val result = MobileController.mobileRegistrationForm(FakeRequest())//registerForm(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }
}