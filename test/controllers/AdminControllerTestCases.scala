
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

class AdminControllerTestCases extends Specification with Mockito {

  val mobileUser = Mobile(
    "sushil", 1, 1, "12345678901234", "123456789012678", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "nokia", "E5")

  val mobileWithBrand = (Mobile(
    "gs", 1, 1, "12345678901234", "123456789012678", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "nokia", "E5"), "nokia", "E5")

  val user = User("admin", "knol2013")
  val cachedUser = User("admin", "knol2013")

  val mockedMobilRepo = mock[MobileRepository]

  val adminController = new AdminController(mockedMobilRepo)

  "AdminControllerTesting: mobiles" in {
    running(FakeApplication()) {
      when(mockedMobilRepo.insertMobileUser(mobileUser)).thenReturn(Right(Some(4)))
      val result = AdminController.mobiles("pending")(FakeRequest().withSession(Security.username -> "admin")).run
      status(result) must equalTo(3)
      contentType(result) must beSome("text/html")
    }
  }
}
