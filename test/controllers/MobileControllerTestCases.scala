
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
import model.repository._
import utils.StatusUtil.Status
import utils.Common
import utils.S3UtilComponent
import play.api.mvc.Request
import play.api.mvc.MultipartFormData
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files
import play.api.mvc.MultipartFormData.BadPart
import play.api.mvc.MultipartFormData.MissingFilePart
import play.api.test.FakeHeaders
import play.mvc.Result

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
  val s3util = mock[S3UtilComponent]
  val mockedMobileRepo = mock[MobileRepository]
  val mockedBrandRepo = mock[BrandRepository]
  val mockedModelRepo = mock[ModelRepository]
  val mockedAuditRepo = mock[AuditRepository]

  val mobileController = new MobileController(mockedMobileRepo, mockedBrandRepo, mockedModelRepo, mockedAuditRepo, mockedMail, s3util)

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
      val result = mobileController.brandRegisterForm(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: modelRegistrationForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      val result = mobileController.modelRegistrationForm(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: mobileRegistration" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val files = Seq[FilePart[TemporaryFile]](FilePart("file", "sushil.jpg", None, TemporaryFile("file", "spec")))
      val multipartBody = MultipartFormData(Map[String, Seq[String]](), files, Seq[BadPart](), Seq[MissingFilePart]())
      val fakeRequest = FakeRequest[MultipartFormData[Files.TemporaryFile]]("POST", "/mobileRegistration", FakeHeaders(), multipartBody)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedBrandRepo.getBrandById(1)) thenReturn (brandById)
      when(mockedMobileRepo.insertMobileUser(any[Mobile])).thenReturn(Right(Some(1)))
      val result = mobileController.mobileRegistration.apply(fakeRequest.withSession(Security.username -> username))
    status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: getMobileUser" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(mobileUser))
      when(mockedBrandRepo.getBrandById(mobileUser.brandId)) thenReturn (brandById)
      when(mockedModelRepo.getModelById(mobileUser.mobileModelId)).thenReturn(modelById)
      when(mockedAuditRepo.insertTimestamp(timestamp)) thenReturn (Right(Some(1)))
      val result = mobileController.getMobileUser("864465028854206")(FakeRequest().withSession(Security.username -> username))
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

  "MobileControllerTesting: isImeiExist -> nonexist imeid" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465029854205")) thenReturn (None)
      val result = mobileController.isImeiExist("864465029854205")(FakeRequest())
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
      val result = mobileController.saveBrand(FakeRequest())
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveBrand -> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Right(Some(1)))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in insert" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Right(None))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in insert" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Left("error"))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = mobileController.saveModel(FakeRequest())
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveModel-> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(newmodel)) thenReturn (Right(Some(1)))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "N72"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Left("error"))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5"))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Right(None))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5"))
      status(result) must equalTo(303)
    }
  }
}
