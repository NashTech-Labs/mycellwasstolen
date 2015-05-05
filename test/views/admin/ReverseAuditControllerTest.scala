package views.admin;

import org.specs2.mutable._
import model.repository._
import play.api.test.Helpers._
import utils._
import play.api.test.WithApplication
import controllers.{ ReverseAdminController, ReverseMobileController, ReverseAuditController, ReverseApplication }
import controllers.ReverseAuthController
import play.api.test.WithApplication

class ReverseAuditControllerTest extends Specification {
  //ReverseAuditController

  "get timestamps URL via ReverseAuditController" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.timestampPage().url must contain("timestamps")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.timestampPage.toString must contain("")
  }

  "get timestampsByIMEI URL via ReverseAuditController" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.timestampsByIMEI.url must contain("imei")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.timestampsByIMEI.toString must contain("")
  }

  "get allTimestamps URL via ReverseAuditController" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.allTimestamps.url must contain("all")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.allTimestamps.toString must contain("")
  }

  "get registrationRecordsByYear URL via ReverseAuditController" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.registrationRecordsByYear("2015").url must contain("registrations_record")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.registrationRecordsByYear("2015").toString must contain("")
  }

  "get renderTopLostBrands URL via ReverseAuditController" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.renderTopLostBrands().url must contain("top_lost_brands")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.renderTopLostBrands().toString must contain("")
  }

  "get n top Lost brands data" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.topLostBrands(2).url must contain("get_top_lost_brands_data")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.topLostBrands(2).toString must contain("")
  }

  "get perDayRegistrationCount" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.getPerDayRegistrationCount().url must contain("get_per_day_registrations")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.getPerDayRegistrationCount().toString must contain("")
  }

  "get getRegistrationByYears" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.getRegistrationByYears.url must contain("registeration_growth_by_year")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.getRegistrationByYears.toString must contain("")
  }

  "get getMonthlyRegistration" in new WithApplication {
    val testController = new ReverseAuditController()
    testController.getMonthlyRegistration("2015").url must contain("get_monthly_registrations")

    val refTest = new controllers.ref.ReverseAuditController()
    refTest.getMonthlyRegistration("2015").toString must contain("")
  }

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
