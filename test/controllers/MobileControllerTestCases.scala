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
//import org.specs2.mutable.script.Specification

class MobileControllerTestCases extends Specification with Mockito {
  
  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand=Brand("nokia",date)
  val model=MobileModels("N72",6)
  val mobileUser = Mobile(
      "gs", "nokia", "glaxacy", "12345678901234", "12-05-2013", "+91 9839839830",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd","12-17-2013","gaurav.png")
  
  val mockedMobileServiceObject = mock[MobileServiceComponent]
  val MobileController = new MobileController(mockedMobileServiceObject)
  

 /* test("MobileControllerTesting: mobileRegistrationForm") {
    val result = MobileController.mobileRegistrationForm(FakeRequest())//registerForm(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }*/
      
  "MobileControllerTesting: mobileRegistrationForm" in {
    running(FakeApplication()) {
      when(mockedMobileServiceObject.mobileRegistration(any[Mobile])).thenReturn(Right(mobileUser))
      val result =MobileController.mobileRegistration(FakeRequest())
      //val result =MobileController.mobileRegistration(FakeRequest().withFormUrlEncodedBody("userName"->"gs","mobileName"->"Nokia","mobileModel"->"N79","imeiMeid"->"12345678901234","purchaseDate"->"12-12-2012","contactNo"->"+91 8285845697","email"->"gaurav@gb.com","regType"->"stolen","mobileStatus"->"pending","description"->"abc dre","regDate"->"12-12-2013","document"->"abc.jpg").withHeaders(CONTENT_TYPE -> "application/x-www-form-urlencoded"))
        /*userController.register(FakeRequest().withFormUrlEncodedBody("name" -> "test", "email" ->
        "test@gmail.com", "twitterId" -> "@satendrakumar06", "password" -> "password", "confirmPassword" ->
        "password").withHeaders(CONTENT_TYPE -> "application/x-www-form-urlencoded"))*/
        status(result) must equalTo(OK)
     // contentType(result) must beSome("text/html")
   }
  }
  /*"MobileControllerTesting: mobileRegistrationForm" in {
    val result = MobileController.mobileRegistrationForm(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }*/
}