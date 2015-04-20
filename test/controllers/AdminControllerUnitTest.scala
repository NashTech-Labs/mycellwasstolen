package controllers

import java.util.Date
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import model.repository._
import play.api.Play.current
import play.api.cache.Cache
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils._
import play.api.test.FakeRequest
import play.api.mvc.Security

class AdminControllerTestCases extends Specification with Mockito {

  val stolenMobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "123456789012677", "+91 9839839830",
    "gs@gmail.com", "stolen", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png")

  val stolenMobileUser1 = Mobile(
    "sushil", 1, 1, "864465028854206", "123456789012677", "+91 9839839830",
    "gs@gmail.com", "Clean", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png")

  val cleanMobileUser = Mobile(
    "sushil", 1, 1, "12345678901234", "123456789012678", "+91 9839839830",
    "gs@gmail.com", "Clean", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png")

  val cleanMobileUser1 = Mobile(
    "sushil", 1, 1, "12345678901234", "123456789012678", "+91 9839839830",
    "gs@gmail.com", "stolen", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png")

  val mobileWithBrand = (Mobile(
    "gs", 1, 1, "864465028854206", "123456789012677", "+91 9839839830",
    "gs@gmail.com", "stolen", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png"), "nokia", "E5")

  val getAllMobilesWithBrand: List[(Mobile, String, String)] = List(mobileWithBrand)
  val audit = List(Audit("864465028854206", new java.sql.Timestamp(new java.util.Date().getTime), Some(1)))
  val user = User("admin", "knol2013")
  val username = "admin"
  val date = new java.sql.Date(new java.util.Date().getTime())
  val newbrand = Brand("nokia", Some(1))
  val brand = List(newbrand)
  val brandById: Option[Brand] = Some(newbrand)
  val newmodel = Model("N72", 1)
  val model = List(newmodel)
  val modelById: Option[Model] = Some(newmodel)


  val mockedMail = mock[MailUtil]
  val mockedS3Util = mock[S3UtilComponent]
  val mockedMobilRepo = mock[MobileRepository]
  val mockedAuditRepo = mock[AuditRepository]
  val mockedBrandRepo = mock[BrandRepository]
  val mockedModelRepo = mock[ModelRepository]
  val adminController = new AdminController(mockedMobilRepo, mockedBrandRepo, mockedModelRepo, mockedAuditRepo, mockedMail, mockedS3Util)

  "MobileControllerTesting: index" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = adminController.index(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }
  
  "MobileControllerTesting: brandRegisterForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = adminController.brandRegisterForm(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "MobileControllerTesting: modelRegistrationForm" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      val result = adminController.modelRegistrationForm(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: requestList" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.insertMobileUser(stolenMobileUser)).thenReturn(Right(Some(1)))
      when(mockedMobilRepo.getAllMobilesUserWithBrandAndModel(StatusUtil.Status.pending.toString())).thenReturn(getAllMobilesWithBrand)
      val result = adminController.requestsList(StatusUtil.Status.pending.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: approve-> of stolen mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      mockedMail.sendMail(stolenMobileUser.imei + " <" + stolenMobileUser.email + ">",
        "Registration Confirmed on MCWS", mockedMail.approvedMessage(stolenMobileUser.imei))
      val result = adminController.approve("864465028854206", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve-> of clean mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(Some(cleanMobileUser))
      mockedMail.sendMail("s@gmail.com", "test", mockedMail.approvedMessage(cleanMobileUser.imei))
      val result = adminController.approve("12345678901234", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve -> with invalid imeid id" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Left("error"))
      val result = adminController.approve("12345678901234", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve -> with vaid imeid data but data not find after approved" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(None)
      val result = adminController.approve("12345678901234", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      mockedMail.sendMail(stolenMobileUser.imei + " <" + stolenMobileUser.email + ">",
        "Registration Confirmed on MCWS", mockedMail.demandProofMessage(stolenMobileUser.imei))
      val result = adminController.proofDemanded("864465028854206", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded-> with invalid imei id" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Left("error"))
      val result = adminController.proofDemanded("864465028854206", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded-> with valid imei id but data not find after proofDemanded" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(None)
      val result = adminController.proofDemanded("864465028854206", StatusUtil.Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: pending" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToPendingByIMEID("864465028854206")).thenReturn(Right(1))
      val result = adminController.pending("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: pending -> failed" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToPendingByIMEID("864465028854206")).thenReturn(Left("error"))
      val result = adminController.pending("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: changeMobileRegTypeForm" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      val result = adminController.changeMobileRegTypeForm(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: changeMobileRegType of stolen mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      when(mockedMobilRepo.changeRegTypeByIMEID(stolenMobileUser1)).thenReturn(Right(1))
      val result = adminController.changeMobileRegType("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: changeMobileRegType of clean mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(Some(cleanMobileUser))
      when(mockedMobilRepo.changeRegTypeByIMEID(cleanMobileUser1)).thenReturn(Right(1))
      val result = adminController.changeMobileRegType("12345678901234")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: changeMobileRegType -> with invalid mobile data" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      when(mockedMobilRepo.changeRegTypeByIMEID(stolenMobileUser1)).thenReturn(Left("error"))
      val result = adminController.changeMobileRegType("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: deleteMobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      when(mockedMobilRepo.deleteMobileUser("864465028854206")).thenReturn(Right(1))
      val result = adminController.deleteMobile("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: deleteMobile -> of invalid record" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854207")).thenReturn(None)
      val result = adminController.deleteMobile("864465028854207")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }

  "AdminControllerTesting: deleteMobile -> error in delete record" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      when(mockedMobilRepo.deleteMobileUser("864465028854206")).thenReturn(Left("error"))
      val result = adminController.deleteMobile("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
    }
  }
  
  "MobileControllerTesting: saveBrand -> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = adminController.saveBrand(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveBrand -> with valid form data and brand not exist" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn(List(Brand("sony")))
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Right(Some(1)))
      val result = adminController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }
  
  "MobileControllerTesting: saveBrand -> brand already exist" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn(List(Brand("nokia")))
      val result = adminController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveBrand -> error in insert brand" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn(List(Brand("sony")))
      when(mockedBrandRepo.insertBrand(any[Brand])) thenReturn (Left("error"))
      val result = adminController.saveBrand(FakeRequest().withFormUrlEncodedBody("name" -> "nokia").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
       when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.getAllModelByBrandId(1)) thenReturn(List(Model("N73",1)))
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Right(Some(1)))
      val result = adminController.saveModel(FakeRequest().withSession(Security.username -> username))
      status(result) must equalTo(400)
    }
  }

  "MobileControllerTesting: saveModel-> with valid form data" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.getAllModelByBrandId(1)) thenReturn(List(Model("N73",1)))
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Right(Some(1)))
      val result = adminController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "N72").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }
  
  "MobileControllerTesting: saveModel-> model already exist" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.getAllModelByBrandId(1)) thenReturn(List(Model("N72",1)))
      val result = adminController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "N72").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }

  "MobileControllerTesting: saveModel-> error in inserting model" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      when(mockedBrandRepo.getAllBrands) thenReturn (brand)
      when(mockedModelRepo.getAllModelByBrandId(1)) thenReturn(List(Model("N72",1)))
      when(mockedModelRepo.insertModel(any[Model])) thenReturn (Left("error"))
      val result = adminController.saveModel(FakeRequest().withFormUrlEncodedBody("brandName" -> "1", "modelName" -> "E5").withSession(Security.username -> username))
      status(result) must equalTo(303)
    }
  }
  
}
