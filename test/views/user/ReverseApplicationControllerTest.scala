package views.admin;

import org.specs2.mutable._
import model.repository._
import play.api.test.Helpers._
import utils._
import play.api.test.WithApplication
import controllers.{ ReverseAdminController, ReverseMobileController, ReverseAuditController, ReverseApplication }
import controllers.ReverseAuthController
import play.api.test.WithApplication

class ReverseApplicationControllerTest extends Specification {

  //ReverseApplication

  "get index URL via ReverseApplication" in new WithApplication {
    val testController = new ReverseApplication()
    testController.index().url must contain("")

    val refTest = new controllers.ref.ReverseApplication()
    refTest.index().toString must contain("")
  }

  "get javascriptRoutes URL via ReverseApplication" in new WithApplication {
    val testController = new ReverseApplication()
    testController.javascriptRoutes().url must contain("")

    val refTest = new controllers.ref.ReverseApplication()
    refTest.javascriptRoutes().toString must contain("")
  }

}
