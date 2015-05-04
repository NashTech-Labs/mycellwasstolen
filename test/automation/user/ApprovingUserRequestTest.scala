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

class ApprovingUserRequestTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"
  "Approving a request" in {
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
      driver.get(baseUrl + "/#registerImei")
      driver.findElementById("registerIMEI").click
      driver.findElementById("userName").sendKeys("test")
      new Select(driver.findElementById("brandId")).selectByVisibleText("nokia")
      new Select(driver.findElementById("modelId")).selectByVisibleText("Asha 200")
      driver.findElementById("imei").sendKeys("123456789012347")
      driver.findElementById("email").sendKeys("reseamanish@gmail.com")
      driver.findElementById("contactNo").sendKeys("+91 1234567890")
      driver.findElementById("fileUpload").sendKeys("/home/knoldus/Pictures/selenium.png")
      driver.findElementById("registerSubmit").click
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("registerSubmit").click
      driver.get(baseUrl + "/login")
      driver.findElementById("email").sendKeys("test")
      driver.findElementById("password").sendKeys("test")
      driver.findElementByCssSelector(".btn.btn-success").click
      driver.findElementById("data").click
      driver.findElementById("imeiRequests").click
      driver.findElementById("approve").click
      driver.findElementByCssSelector("BODY").getText().contains("Mobile has been approved successfully!")
    }
  }
}
