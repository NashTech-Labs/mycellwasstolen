package controllers

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.specs2.mutable._

import model.domains.Domain._
import model.users.MobileServiceComponent
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.Security
import play.api.mvc.WithHeaders
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._

class MobileControllerTestCases extends Specification with Mockito {

  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand = Brand("nokia", "12-17-2013",Some(4))
  val model = MobileModels("N72", 6)
  val user=User("admin","knol2013")
  val cachedUser=User("admin","knol2013")
  val username="admin"
  val mobileUser = Mobile(
    "gs", 1, 5, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454")

  val mockedMobileServiceObject = mock[MobileServiceComponent]

  val MobileController = new MobileController(mockedMobileServiceObject)
  val mobileNames: List[Brand] = List(brand)
  val mobileNamesById :Option[Brand]= Some(brand)

  "MobileControllerTesting: mobileRegistrationForm" in {
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
    when(mockedMobileServiceObject.getMobilesName()) thenReturn (mobileNames)
    val result = MobileController.mobileRegistrationForm(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/html")
  }
  }

  "MobileControllerTesting: mobileRegistrationSecureForm" in {
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobilesName) thenReturn (mobileNames)
      val result =MobileController.mobileRegistrationSecureForm(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
   }
  }
  
  "MobileControllerTesting: brandRegisterForm" in {
     running(FakeApplication()) {
     Cache.set(username, cachedUser)
     val result =MobileController.brandRegisterForm(FakeRequest().withSession(Security.username -> username)).run
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
   }
  }
  
  "MobileControllerTesting: createMobileModelForm" in {
     running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobilesName) thenReturn (mobileNames)
     val result = MobileController.createMobileModelForm(FakeRequest().withSession(Security.username -> username)).run
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
   }
  }
  
  "MobileControllerTesting: mobileRegistrationForm" in {
     running(FakeApplication()) {
     when(mockedMobileServiceObject.getMobilesName) thenReturn (mobileNames)
     when(mockedMobileServiceObject.getMobileNamesById(4)) thenReturn(mobileNamesById)
     when(mockedMobileServiceObject.mobileRegistration(any[Mobile])).thenReturn(Right(mobileUser))
    // val result = MobileController.mobileRegistration(FakeRequest().withFormUrlEncodedBody("username"->"test","mobileName"->"1","mobileModel"->"2","imeiMeid"->"12345678","purchaseDate"->"2013-12-23","contactNo"->"24234325","email"->"a@b.com","regType"->"stolen","mobileStatus"->"pending","description"->"fdf","regDate"->"2013-12-23","document"->"/home/gaurav/Desktop/12334542345578.png","otherMobileBrand"->"vix","otherMobileModel"->"v43").withHeaders(CONTENT_TYPE -> "application/x-www-form-urlencoded"))
     val result = MobileController.mobileRegistrationForm(FakeRequest())
     status(result) must equalTo(200)
     contentType(result) must beSome("text/html")
   }
  }
  
  "MobileControllerTesting: getImeiMeidList" in {
     running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     when(mockedMobileServiceObject.getMobileNamesById(4)) thenReturn (mobileNamesById)
     when(mockedMobileServiceObject.getMobileModelById(6)) thenReturn (Some(model))
     val result = MobileController.getImeiMeidList("12345678901234")(FakeRequest())
     status(result) must equalTo(OK)
     contentType(result) must beSome("application/json")
   }
  }
  
  "MobileControllerTesting: getMobileModels" in {
     running(FakeApplication()) {
     Cache.set(username, cachedUser)
     val result = MobileController.getImeiMeidList("12345678901234")(FakeRequest())
     status(result) must equalTo(OK)
     contentType(result) must beSome("application/json")
   }
  }
  
 "MobileControllerTesting: mobileStatus" in {
   running(FakeApplication()) {
     Cache.set(username, cachedUser)
    val result = MobileController.mobileStatus(FakeRequest())
    status(result) must equalTo(OK)
   contentType(result) must beSome("text/html")
 }
 }
  	
  "MobileControllerTesting: isImeiExist" in {
    when(mockedMobileServiceObject.isImeiExist("12345678901234")) thenReturn (true)
    val result = MobileController.isImeiExist("12345678901234")(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/plain")
  }
  
  
  "MobileControllerTesting: saveMobileName" in {
    running(FakeApplication()) {
      Cache.set(username, cachedUser)
      when(mockedMobileServiceObject.addMobileName(any[Brand])) thenReturn(Right(Some(5)))
      val result = MobileController.saveMobileName(FakeRequest())
      status(result) must equalTo(400)

    }
  }
  
  
  "MobileControllerTesting: createMobileModel" in {
    running(FakeApplication()) {
      Cache.set(username, cachedUser)
      when(mockedMobileServiceObject.getMobilesName) thenReturn (mobileNames)
      when(mockedMobileServiceObject.createMobileModel(any[MobileModels])) thenReturn(Right(model))
      val result = MobileController.createMobileModel(FakeRequest())
      status(result) must equalTo(400)

    }
  }

}