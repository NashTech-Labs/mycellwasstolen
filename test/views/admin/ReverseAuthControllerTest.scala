package views.admin;

import org.specs2.mutable._

import controllers.ReverseAdminController
import controllers.ReverseApplication
import controllers.ReverseAuditController
import controllers.ReverseAuthController
import controllers.ReverseMobileController
import play.api.test.Helpers._
import play.api.test.WithApplication
import play.api.test.WithApplication

class ReverseAuthControllerTest extends Specification {
  //ReverseAuthController

  "get login URL via ReverseAuthcontroller" in new WithApplication {
    val testController = new ReverseAuthController()
    testController.login().url must contain("login")

    val refTest = new controllers.ref.ReverseAuthController()
    refTest.login().toString must contain("login")

  }

  "get authenticate URL via ReverseAuthcontroller" in new WithApplication {
    val testController = new ReverseAuthController()
    testController.authenticate().url must contain("authenticate")

    val refTest = new controllers.ref.ReverseAuthController()
    refTest.authenticate().toString must contain("authenticate")

  }

  "get logout URL via ReverseAuthcontroller" in new WithApplication {
    val testController = new ReverseAuthController()
    testController.logout().url must contain("logout")

    val refTest = new controllers.ref.ReverseAuthController()
    refTest.logout().toString must contain("logout")
  }

}
