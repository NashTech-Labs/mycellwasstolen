package controllers

import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.Security
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import model.repository.{ Mobile, Brand, Model, Audit, User }
import utils.StatusUtil.Status
import model.repository.{ MobileRepository, BrandRepository, ModelRepository, AuditRepository }
import utils.Common

class MobileControllerTestCases extends Specification with Mockito {

  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand = List(Brand("nokia", "12-17-2013", Some(1)))
  val brandById: Option[Brand] = Some(Brand("nokia", "12-17-2013", Some(1)))
  val model = List(Model("N72", 1))
  val modelById: Option[Model] = Some(Model("N72", 1))
  val user = User("admin", "knol2013")
  val username = "admin"
  val mobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "864465028854206", "12-03-2013", "+91 8375919908",
    "sushil@gmail.com", "stolen", Status.pending, "test", "12-17-2015", "ss.png", "nokia", "E5")
    
  val timestamp= new Audit("864465028854206","12-17-2015",Some(1))  

  val mockedMail = mock[Common]
  val mockedMobileRepo = mock[MobileRepository]
  val mockedBrandRepo = mock[BrandRepository]
  val mockedModelRepo = mock[ModelRepository]
  val mockedAuditRepo = mock[AuditRepository]

  val mobileController = new MobileController(mockedMobileRepo, mockedBrandRepo, mockedModelRepo, mockedAuditRepo, mockedMail)
  "MobileControllerTesting: modelRegistrationForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      val result = mobileController.modelRegistrationForm(FakeRequest().withSession(Security.username -> username)).run
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: mobileRegistration" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedBrandRepo.getBrandById(1)) thenReturn (brandById)
      when(mockedMobileRepo.insertMobileUser(mobileUser)).thenReturn(Right(Some(1)))
      val result = mobileController.mobileRegistration(FakeRequest().withSession(Security.username -> username)).run
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: getMobileUser" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(mobileUser))
      when(mockedBrandRepo.getBrandById(1)) thenReturn (brandById)
      when(mockedModelRepo.getModelById(1)).thenReturn(modelById)
      when(mockedAuditRepo.insertTimestamp(timestamp)) thenReturn(Right(Some(1)))
      val result = mobileController.getMobileUser("864465028854206")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("application/json")
    }
  }

}
