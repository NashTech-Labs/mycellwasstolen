package automation

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

class ModelInsertionTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"
  "Testing Add  Mobile Model" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl + "/login")
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("email").sendKeys("test")
      driver.findElementById("password").sendKeys("test")
      driver.findElementByCssSelector(".btn.btn-success").click
      driver.findElementById("forms").click
      driver.findElementById("addNewBrand").click
      driver.findElementById("name").sendKeys("nokia")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementByCssSelector("BODY").getText.contains("Brand successfully added")
      driver.findElementById("addNewModel").click
      new Select(driver.findElementById("brandName")).selectByVisibleText("nokia")
      driver.findElementById("modelName").sendKeys("Asha 200")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementByCssSelector("BODY").getText().contains("Mobile Model successfully added")
    }
  }
}
