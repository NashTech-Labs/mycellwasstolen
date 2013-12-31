package controllers

import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import org.specs2.mutable._
import model.domains.Domain._
import model.users.MobileServiceComponent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.FakeApplication
import model.domains.Domain.BrandForm
//import org.specs2.mutable.script.Specification

class MobileControllerTestCases extends Specification with Mockito {
  
  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand=Brand("nokia",date)
  val model=MobileModels("N72",6)
  val mobileUser = Mobile(
      "gs", "nokia", "glaxacy", "12345678901234", "12-05-2013", "+91 9839839830",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd","12-17-2013","gaurav.png","Sigma","Sigma454")
  
  val mockedMobileServiceObject = mock[MobileServiceComponent]
  val MobileController = new MobileController(mockedMobileServiceObject)
  val mobileNames: List[Brand] = List(brand)
  

 "MobileControllerTesting: mobileRegistrationForm" in {
      val result = MobileController.mobileRegistrationForm(FakeRequest())
      status(result) must equalTo(OK)
      //contentType(result) must beSome("text/html")
   }
  
  /*"MobileControllerTesting: mobileRegistration" in {
    running(FakeApplication()) {
      when(mockedMobileServiceObject.mobileRegistration(any[Mobile])).thenReturn(Right(mobileUser))
      val result =MobileController.mobileRegistration(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
   }
  }*/
  
 /* "MobileControllerTesting: mobileStatus" in {
    val result = MobileController.mobileStatus(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }*/
}