/*package automation

import scala.slick.driver.PostgresDriver.simple._
import org.specs2.mutable.Specification
import play.api.test.WithServer
import play.api.libs.ws.WS
import play.api.test.Helpers._
import play.api.test.Helpers.await
import play.api.test.TestServer
import play.api.test.WithServer
import utils.Connection
import java.util.concurrent.TimeUnit
import org.openqa.selenium.support.ui.Select
import play.api.test.FakeApplication
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class BrandInsertionTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"

  "Testing Add Mobile Brand" in {
    running(TestServer(port), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl + "/login")
      browser.$("#email").text("test")
      browser.$("#password").text("test")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
    }
  }
}
*/