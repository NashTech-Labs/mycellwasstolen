package controllers

import java.sql.Timestamp

import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable._

import model.repository._
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.Files
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import play.api.mvc.MultipartFormData.BadPart
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.MultipartFormData.MissingFilePart
import play.api.mvc.Security
import play.api.test.FakeApplication
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils._

class MobileControllerTestCases extends Specification with Mockito {

  val date = new java.sql.Date(new java.util.Date().getTime())
  val newbrand = Brand("nokia", Some(1))
  val brand = List(newbrand)
  val brandById: Option[Brand] = Some(newbrand)
  val newmodel = Model("N72", 1)
  val model = List(newmodel)
  val modelById: Option[Model] = Some(newmodel)
  val utildate=CommonUtils.getSqlDate()
  val user = User("admin", "knol2013")
  val username = "admin"

  val mobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "864465028854206", CommonUtils.getSqlDate(), "+91 8375919908",
    "sushil@gmail.com", "stolen", StatusUtil.Status.pending, "test", CommonUtils.getSqlDate(), "ss.png", "nokia", "E5")

  val timestamp = new Audit("864465028854206", new Timestamp(new java.util.Date().getTime), Some(1))

  val mockedMail = mock[MailUtil]
  val mockedCommonUtil=mock[CommonUtils]
  val s3util = mock[S3UtilComponent]
  val mockedMobileRepo = mock[MobileRepository]
  val mockedBrandRepo = mock[BrandRepository]
  val mockedModelRepo = mock[ModelRepository]
  val mockedAuditRepo = mock[AuditRepository]
  val mobileController = new MobileController(mockedMobileRepo, mockedBrandRepo, mockedModelRepo, mockedAuditRepo, mockedMail, s3util,mockedCommonUtil)

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

  "MobileControllerTesting: mobileRegistration: with valid data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val files = Seq[FilePart[TemporaryFile]](FilePart("file", "sushil.jpg", None, TemporaryFile("file", "spec")))
      val validFormData = Map("userName" -> Seq("manish"),
        "brandId" -> Seq("1"),
        "mobileModelId" -> Seq("1"),
        "imeiMeid" -> Seq("123456789012347"),
        "otherImeiMeid" -> Seq("1234"),
        "purchaseDate" -> Seq("2015-03-31"),
        "contactNo" -> Seq("9958324567"),
        "email" -> Seq("reseamanish@gmail.com"),
        "regType" -> Seq("pending"),
        "document" -> Seq("sushil.jpg"),
        "description" -> Seq("test"),
        "otherMobileBrand" -> Seq("nodata "),
        "otherMobileModel" -> Seq("nodata"))
      val multipartBody = MultipartFormData(validFormData, files, Seq[BadPart](), Seq[MissingFilePart]())
      val fakeRequest = FakeRequest[MultipartFormData[Files.TemporaryFile]]("POST", "/mobileRegistration", FakeHeaders(), multipartBody)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedCommonUtil.getSqlDate())thenReturn(utildate)
      when(mockedMobileRepo.insertMobileUser(any[Mobile])).thenReturn(Right(Some(1)))
      when(mockedMobileRepo.getMobileUserByIMEID("123456789012347")).thenReturn(Some(mobileUser))
      val result = mobileController.mobileRegistration.apply(fakeRequest.withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }
  
  "MobileControllerTesting: mobileRegistration: with valid data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val files = Seq[FilePart[TemporaryFile]](FilePart("file", "sushil.jpg", None, TemporaryFile("file", "spec")))
      val validFormData = Map("userName" -> Seq("manish"),
        "brandId" -> Seq("1"),
        "mobileModelId" -> Seq("1"),
        "imeiMeid" -> Seq("123456789012347"),
        "otherImeiMeid" -> Seq("1234"),
        "purchaseDate" -> Seq("2015-03-31"),
        "contactNo" -> Seq("9958324567"),
        "email" -> Seq("reseamanish@gmail.com"),
        "regType" -> Seq("pending"),
        "document" -> Seq("sushil.jpg"),
        "description" -> Seq("test"),
        "otherMobileBrand" -> Seq("nodata "),
        "otherMobileModel" -> Seq("nodata"))
      val multipartBody = MultipartFormData(validFormData, files, Seq[BadPart](), Seq[MissingFilePart]())
      val fakeRequest = FakeRequest[MultipartFormData[Files.TemporaryFile]]("POST", "/mobileRegistration", FakeHeaders(), multipartBody)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedCommonUtil.getSqlDate())thenReturn(utildate)
      when(mockedMobileRepo.insertMobileUser(any[Mobile])).thenReturn(Right(Some(1)))
      when(mockedMobileRepo.getMobileUserByIMEID("123456789012347")).thenReturn(Some(mobileUser))
      val result = mobileController.mobileRegistration.apply(fakeRequest.withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: mobileRegistration: with bad data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val files = Seq[FilePart[TemporaryFile]](FilePart("file", "sushil.jpg", None, TemporaryFile("file", "spec")))
      val multipartBody = MultipartFormData(Map[String, Seq[String]](), files, Seq[BadPart](), Seq[MissingFilePart]())
      val fakeRequest = FakeRequest[MultipartFormData[Files.TemporaryFile]]("POST", "/mobileRegistration", FakeHeaders(), multipartBody)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedMobileRepo.insertMobileUser(any[Mobile])).thenReturn(Right(Some(1)))
      val result = mobileController.mobileRegistration.apply(fakeRequest.withSession(Security.username -> username))
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: checkMobileStatus" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(mobileUser))
      when(mockedBrandRepo.getBrandById(mobileUser.brandId)) thenReturn (brandById)
      when(mockedModelRepo.getModelById(mobileUser.mobileModelId)).thenReturn(modelById)
     val result = mobileController.checkMobileStatus("864465028854206","user")(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(200)
      contentType(result) must beSome("application/json")
    }
  }

  "MobileControllerTesting: checkMobileStatus: failed" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedMobileRepo.getMobileUserByIMEID("864465028854206")).thenReturn(None)
      when(mockedBrandRepo.getBrandById(mobileUser.brandId)) thenReturn (brandById)
      when(mockedModelRepo.getModelById(mobileUser.mobileModelId)).thenReturn(modelById)
      when(mockedAuditRepo.insertTimestamp(timestamp)) thenReturn (Right(Some(1)))
      val result = mobileController.checkMobileStatus("864465028854206","user")(FakeRequest().withSession(Security.username -> username))
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

  "MobileControllerTesting: isImeiExist -> valid imeid" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      mockedCommonUtil.validateImei("864465028854206")
      val result = mobileController.isImeiExist("864465028854206")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }
  
  "MobileControllerTesting: isImeiExist -> invalid imeid" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      mockedCommonUtil.validateImei("864465028854201")
      val result = mobileController.isImeiExist("864465028854201")(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "MobileControllerTesting: saveBrand -> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = mobileController.saveBrand(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveBrand -> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Right(Some(1)))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in insert" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Right(None))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in insert" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Left("error"))
      val result = mobileController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = mobileController.saveModel(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveModel-> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(newmodel)) thenReturn (Right(Some(1)))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "N72").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Left("error"))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Right(None))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> none return" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (List())
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Right(None))
      val result = mobileController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }
}
