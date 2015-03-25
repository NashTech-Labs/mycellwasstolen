package controllers

import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import org.specs2.mutable._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.cache.Cache
import play.api.Play.current
import play.api.mvc.Security
import play.api.test.FakeHeaders
import model.repository._
import utils.StatusUtil.Status
import utils.Common

class AdminControllerTestCases extends Specification with Mockito {

  val mobileUser = Mobile(
    "sushil", 1, 1, "12345678901234", "123456789012678", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "nokia", "E5")
  val mobileWithBrand = (Mobile(
    "gs", 1, 1, "12345678901234", "123456789012678", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "nokia", "E5"), "nokia", "E5")
  val getAllMobilesWithBrand: List[(Mobile, String, String)] = List(mobileWithBrand)
  val user = User("admin", "knol2013")

  val mockedMail=mock[Common]
  val mockedMobilRepo = mock[MobileRepository]
  val adminController = new AdminController(mockedMobilRepo,mockedMail)

  "AdminControllerTesting: mobiles" in {
    running(FakeApplication()) {
      Cache.set("admin", user)
      when(mockedMobilRepo.insertMobileUser(mobileUser)).thenReturn(Right(Some(1)))
      when(mockedMobilRepo.getAllMobilesUserWithBrandAndModel(Status.pending.toString())).thenReturn(getAllMobilesWithBrand)
      val result = adminController.mobiles(Status.pending.toString())(FakeRequest().withSession(Security.username -> "admin")).run
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html")
    }
  }
  
   "AdminControllerTesting: approve" in {
    running(FakeApplication()) {
     Cache.set("admin", user)
     when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
     when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(Some(mobileUser))
     mockedMail.sendMail(mobileUser.imeiMeid + " <" + mobileUser.email + ">",
                "Registration Confirmed on MCWS", Common.approvedMessage(mobileUser.imeiMeid))
     val result = adminController.approve("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin")).run
    status(result) must equalTo(303)
  }
  }
   
   "AdminControllerTesting: proofDemanded" in {
    running(FakeApplication()) {
     Cache.set("admin", user)
     when(mockedMobilRepo.changeStatusToApproveByIMEID("12345678901234")).thenReturn(Right(1))
     when(mockedMobilRepo.getMobileUserByIMEID("12345678901234")).thenReturn(Some(mobileUser))
     mockedMail.sendMail(mobileUser.imeiMeid + " <" + mobileUser.email + ">",
                "Registration Confirmed on MCWS", Common.demandProofMessage(mobileUser.imeiMeid))
     val result = adminController.proofDemanded("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin")).run
    status(result) must equalTo(303)
  }
  }
   
   "AdminControllerTesting: pending" in {
    running(FakeApplication()) {
     Cache.set("admin", user)
     when(mockedMobilRepo.changeStatusToPendingByIMEID("12345678901234")).thenReturn(Right(1))
     val result = adminController.proofDemanded("12345678901234", Status.approved.toString())(FakeRequest().withSession(Security.username -> "admin")).run
    status(result) must equalTo(303)
  }
  }
   
}
