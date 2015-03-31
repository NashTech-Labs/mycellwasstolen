package controllers

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.specs2.mutable._
import model.repository._
import play.api.Play.current
import play.api.cache.Cache
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.StatusUtil.Status
import play.api.test.FakeRequest
import play.api.mvc.Security
import java.util.Date
import utils.S3UtilComponent
import utils.MailUtil
import java.util.Calendar

class AdminControllerTestCases extends Specification with Mockito {

  val stolenMobileUser = Mobile(
    "sushil", 1, 1, "864465028854206", "123456789012677", new java.sql.Date(new java.util.Date().getTime()), "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "test", new java.sql.Date(new java.util.Date().getTime()), "gaurav.png", "nokia", "E5")

  val stolenMobileUser1 = Mobile(
    "sushil", 1, 1, "864465028854206", "123456789012677", new java.sql.Date(new java.util.Date().getTime()), "+91 9839839830",
    "gs@gmail.com", "Clean", Status.pending, "test", new java.sql.Date(new java.util.Date().getTime()), "gaurav.png", "nokia", "E5")

  val cleanMobileUser = Mobile(
    "sushil", 1, 1, "12345678901234", "123456789012678", new java.sql.Date(new java.util.Date().getTime()), "+91 9839839830",
    "gs@gmail.com", "Clean", Status.pending, "test", new java.sql.Date(new java.util.Date().getTime()), "gaurav.png", "nokia", "E5")

  val mobileWithBrand = (Mobile(
    "gs", 1, 1, "864465028854206", "123456789012677", new java.sql.Date(new java.util.Date().getTime()), "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "test", new java.sql.Date(new java.util.Date().getTime()), "gaurav.png", "nokia", "E5"), "nokia", "E5")

  val getAllMobilesWithBrand: List[(Mobile, String, String)] = List(mobileWithBrand)
  val calender = Calendar.getInstance
  val now:java.util.Date = calender.getTime
  val timeStamp = new java.sql.Timestamp(now.getTime())
  val audit = List(Audit("864465028854206", timeStamp , Some(1)))
  val user = User("admin", "knol2013")
  val mockedMail = mock[MailUtil]
  val mockedS3Util = mock[S3UtilComponent]
  val mockedMobilRepo = mock[MobileRepository]
  val mockedAuditRepo = mock[AuditRepository]
  val adminController = new AdminController(mockedMobilRepo, mockedAuditRepo, mockedMail,mockedS3Util)

  "AdminControllerTesting: mobiles" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.insertMobileUser(stolenMobileUser)).thenReturn(Right(Some(1)))
      when(mockedMobilRepo.getAllMobilesUserWithBrandAndModel(Status.pending.toString())).thenReturn(getAllMobilesWithBrand)
      val result = adminController.mobiles(Status.pending.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: approve-> of stolen mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      mockedMail.sendMail(stolenMobileUser.imeiMeid + " <" + stolenMobileUser.email + ">",
        "Registration Confirmed on MCWS", mockedMail.approvedMessage(stolenMobileUser.imeiMeid))
      val result = adminController.approve("864465028854206", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve-> of clean mobile" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(Some(cleanMobileUser))
      mockedMail.sendMail(cleanMobileUser.imeiMeid + " <" + cleanMobileUser.email + ">",
        "Registration Confirmed on MCWS", mockedMail.approvedMessage(cleanMobileUser.imeiMeid))
      val result = adminController.approve("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve -> with invalid imeid id" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Left("error"))
      val result = adminController.approve("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: approve -> with vaid imeid data but data not find after approved" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(None)
      val result = adminController.approve("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      mockedMail.sendMail(stolenMobileUser.imeiMeid + " <" + stolenMobileUser.email + ">",
        "Registration Confirmed on MCWS", mockedMail.demandProofMessage(stolenMobileUser.imeiMeid))
      val result = adminController.proofDemanded("864465028854206", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded-> with invalid imei id" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Left("error"))
      val result = adminController.proofDemanded("864465028854206", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(303)
    }
  }

  "AdminControllerTesting: proofDemanded-> with valid imei id but data not find after proofDemanded" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.changeStatusToDemandProofByIMEID("864465028854206")).thenReturn(Right(1))
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(None)
      val result = adminController.proofDemanded("864465028854206", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin"))
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

  "AdminControllerTesting: changeMobileRegType" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.getMobileUserByIMEID("864465028854206")).thenReturn(Some(stolenMobileUser))
      when(mockedMobilRepo.changeRegTypeByIMEID(stolenMobileUser1)).thenReturn(Right(1))
      val result = adminController.changeMobileRegType("864465028854206")(FakeRequest().withSession(Security.username -> "admin"))
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

  "AdminControllerTesting: auditPage" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      val result = adminController.auditPage(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: getTimestampByIMEI -> with invalid form data" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedAuditRepo.getAllTimestampsByIMEID("864465028854206")).thenReturn(audit)
      val result = adminController.getTimestampByIMEI(FakeRequest())
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: getTimestampByIMEI -> with valid form data" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedAuditRepo.getAllTimestampsByIMEID("864465028854206")).thenReturn(List())
      val result = adminController.getTimestampByIMEI(FakeRequest().withFormUrlEncodedBody("imeiMeid" -> "864465028854206").withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }

  "AdminControllerTesting: getAllTimestamp" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedAuditRepo.getAllTimestamps).thenReturn(audit)
      val result = adminController.getAllTimestamp(FakeRequest().withSession(Security.username -> "admin"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }
}
