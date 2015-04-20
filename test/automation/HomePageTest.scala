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

class HomePageTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"

  "Testing Home Page" in {
    running(TestServer(port), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)
      browser.title() must equalTo("Welcome to MCWS")
    }
  }
}
*/