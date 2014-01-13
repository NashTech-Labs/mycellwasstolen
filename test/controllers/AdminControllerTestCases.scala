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
import play.api.cache.Cache
import play.api.Play.current
import play.api.mvc.Security
import play.api.test.FakeHeaders
import play.api.mvc.WithHeaders

class AdminControllerTestCases extends Specification with Mockito {
  
  val mobileUser = Mobile(
    "gs", 1, 5, "12345678901234", "123456789012678" ,"12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454")
    
   val mobileWithBrand = ( Mobile(
    "gs", 1, 5, "12345678901234","123456789012678" , "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454"),"nokia","n90")
  
    val date = new java.sql.Date(new java.util.Date().getTime())
  val brand = Brand("nokia", date,Some(4))
  val model = MobileModels("N72", 6)
  val mobileNamesById :Option[Brand]= Some(brand)
  val mobilelist: List[Mobile] = List(mobileUser)
  val mobileNameWithMobile=List(mobileUser,"Nokia","lumia")
  val user=User("admin","knol2013")
  val cachedUser=User("admin","knol2013")
  val username="admin"
  val getAllMobilesWithBrand:List[(Mobile,String,String)]=List(mobileWithBrand)
  
  val mockedMobileServiceObject = mock[MobileServiceComponent]

  val AdminController = new AdminController(mockedMobileServiceObject)
  
  
  "AdminControllerTesting: mobiles" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getAllMobilesWithBrandAndModel("pending")) thenReturn (getAllMobilesWithBrand)
     when(mockedMobileServiceObject.getMobileNamesById(4)) thenReturn (mobileNamesById)
     when(mockedMobileServiceObject.getMobileModelById(6)) thenReturn (Some(model))
     val result = AdminController.mobiles("pending")(FakeRequest().withSession(Security.username -> username)).run
     status(result) must equalTo(OK)
     contentType(result) must beSome("text/html")
  }
  }

  "AdminControllerTesting: approve" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     when(mockedMobileServiceObject.changeStatusToApprove(mobileUser)) thenReturn (true)
     
    val result = AdminController.approve("12345678901234")(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/plain")
  }
  }
  
  "AdminControllerTesting: proofDemanded" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     when(mockedMobileServiceObject.changeStatusToDemandProof(mobileUser)) thenReturn (true)
     
    val result = AdminController.proofDemanded("12345678901234")(FakeRequest())
    status(result) must equalTo(OK)
    contentType(result) must beSome("text/plain")
  }
  }
  
  "AdminControllerTesting: sendMailForDemandProof" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     val result = AdminController.sendMailForDemandProof("12345678901234")(FakeRequest())
     status(result) must equalTo(OK)
    contentType(result) must beSome("text/plain")
  }
  }
  
  "AdminControllerTesting: changeMobileRegTypeForm" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
    // when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     val result = AdminController.changeMobileRegTypeForm(FakeRequest().withSession(Security.username -> username)).run
     status(result) must equalTo(OK)
     contentType(result) must beSome("text/html")
  }
  }
  
   "AdminControllerTesting: changeMobileRegType" in {
    
    running(FakeApplication()) {
     Cache.set(username, cachedUser)
     when(mockedMobileServiceObject.getMobileRecordByIMEID("12345678901234")) thenReturn (Some(mobileUser))
     when(mockedMobileServiceObject.changeRegTypeByIMEID(mobileUser)) thenReturn (true)
     val result = AdminController.changeMobileRegType("12345678901234")(FakeRequest())
     status(result) must equalTo(OK)
     contentType(result) must beSome("text/plain")
  }
  }

}