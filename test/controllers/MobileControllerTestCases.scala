
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
import model.repository.MobileDetail

class MobileControllerTestCases extends Specification with Mockito {

  val date = new java.sql.Date(new java.util.Date().getTime())
  val newbrand = Brand("nokia", "12-17-2013", Some(1))
  val brand = List(newbrand)
  val brandById: Option[Brand] = Some(newbrand)
  val newmodel = Model("N72", 1)
  val model = List(newmodel)
  val modelById: Option[Model] = Some(newmodel)
  val user = User("admin", "knol2013")
  val username = "admin"

  val mobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "864465028854206", "12-03-2013", "+91 8375919908",
    "sushil@gmail.com", "stolen", Status.pending, "test", "12-17-2015", "ss.png", "nokia", "E5")

  val timestamp = new Audit("864465028854206", "12-17-2015", Some(1))

  val mockedMail = mock[Common]
  val mockedMobileRepo = mock[MobileRepository]
  val mockedBrandRepo = mock[BrandRepository]
  val mockedModelRepo = mock[ModelRepository]
  val mockedAuditRepo = mock[AuditRepository]

  val mobileController = new MobileController(mockedMobileRepo, mockedBrandRepo, mockedModelRepo, mockedAuditRepo, mockedMail)

  "MobileControllerTesting: mobileRegistrationForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      val result = mobileController.mobileRegistrationForm(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: mobileRegistrationSecureForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      val result = mobileController.mobileRegistrationSecureForm(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: brandRegisterForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = mobileController.brandRegisterForm(FakeRequest().withSession(Security.username -> username)).run
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

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
      when(mockedBrandRepo.getBrandById(mobileUser.brandId)) thenReturn (brandById)
      when(mockedModelRepo.getModelById(mobileUser.mobileModelId)).thenReturn(modelById)
      val mobileDetail = MobileDetail(mobileUser.userName, brandById.get.name, modelById.get.name, mobileUser.imeiMeid, mobileUser.otherImeiMeid,
          mobileUser.mobileStatus.toString(), mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email,
          mobileUser.regType, mobileUser.otherMobileBrand, mobileUser.otherMobileModel)
      when(mockedAuditRepo.insertTimestamp(timestamp)) thenReturn (Right(Some(1)))
      val result = mobileController.getMobileUser("864465028854206")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("application/json")
    }
  }

  "MobileControllerTesting: getModels" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedModelRepo.getAllModelByBrandId(1)) thenReturn (model)
      val result = mobileController.getModels(1)(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("application/json")
    }
  }

  "MobileControllerTesting: mobileStatus" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = mobileController.mobileStatus(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: isImeiExist -> existed imeid" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854206")) thenReturn (Some(mobileUser))
      val result = mobileController.isImeiExist("864465028854206")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "MobileControllerTesting: isImeiExist -> invalid imeid" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854201")) thenReturn (Some(mobileUser))
      val result = mobileController.isImeiExist("864465028854201")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "MobileControllerTesting: saveBrand -> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(newbrand)) thenReturn (Right(Some(1)))
      val result = mobileController.saveBrand(FakeRequest())
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveBrand -> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(newbrand)) thenReturn (Right(Some(1)))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in inserting brand" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(newbrand)) thenReturn (Left("error"))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(newmodel)) thenReturn (Right(Some(1)))
      val result = mobileController.saveModel(FakeRequest())
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveModel-> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(newmodel)) thenReturn (Right(Some(1)))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("mobileName" -> "1", "mobileModel" -> "E5"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(newmodel)) thenReturn (Left("error"))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("mobileName" -> "1", "mobileModel" -> "E"))
      status(result) must equalTo(303)
    }
  }

}
