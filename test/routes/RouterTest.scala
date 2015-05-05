package routes

import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Security
import play.api.cache.Cache
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Play.current
import model.repository.Brand
import model.repository.Model
import model.repository.Mobile
import utils.StatusUtil.Status
import model.repository.MobileRepository
import model.repository.BrandRepository
import model.repository.ModelRepository
import model.repository.AuditRepository

class RouterTest extends Specification {

  // user routes

  "redirect to user home page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "redirect mobile registration " in {
    running(FakeApplication()) {
      val result = route(FakeRequest(POST, "/save_users").withFormUrlEncodedBody(
        "userName" -> "sushil",
        "brandId" -> "1",
        "modelId" -> "1",
        "imei" -> "864465028854206",
        "otherImei" -> "864465028854206",
        "contactNo" -> "8375919908",
        "email" -> "s@gmail.com",
        "regType" -> "stolen",
        "document" -> "s.jpg")).get
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "JavascriptRoutes Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/javascriptRoutes"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/javascript")
    }
  }

  // Admin routes

  "redirect to admin home page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/admin"))
      status(result) must equalTo(303)
    }
  }

  "redirect to requestsList page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/requests?status=pending"))
      status(result) must equalTo(303)
    }
  }

  "redirect to audit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/timestamps"))
      status(result) must equalTo(303)
    }
  }

  "demand proof a request" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/demand_proof_request?imeid=123456789012347&page=1"))
      status(result) must equalTo(303)
    }
  }

  "approving a request" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/approve?imeid=123456789012347&page=1"))
      status(result) must equalTo(303)
    }
  }

  "show pending requests" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/pending?imeid=123456789012347"))
      status(result) must equalTo(303)
    }
  }

  "change mobile status" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/change_mobile_status"))
      status(result) must equalTo(303)
    }
  }

  "mark registration as spam" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/mark_as_spam?imeid=123456789012347"))
      status(result) must equalTo(303)
    }
  }

  //Audit routes

  "get timestamp page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/timestamps"))
      status(result) must equalTo(303)
    }
  }

  "get registration record by year" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/registrations_record?year=2012"))
      status(result) must equalTo(303)
    }
  }

  "show timestamp for an IMEI page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(POST, "/timestamp/imei"))
      status(result) must equalTo(303)
    }
  }

  "show all timestamps " in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/timestamp/all"))
      status(result) must equalTo(303)
    }
  }

  "get monthly registrations" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/get_monthly_registrations?year=2015"))
      status(result) must equalTo(200)
    }
  }

  "get top lost brands" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/top_lost_brands"))
      status(result) must equalTo(303)
    }
  }

    "get registeration growth by year" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/registeration_growth_by_year"))
      status(result) must equalTo(303)
    }
  }
    
    "get top lost brands data by ajax" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/get_top_lost_brands_data?n=2"))
      status(result) must equalTo(200)
    }
  }
  
  "get per day registrations by ajax" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/get_per_day_registrations"))
      status(result) must equalTo(200)
    }
  }

}
