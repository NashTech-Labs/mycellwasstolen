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

class ProofDemandingFromUserTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"

  "Proof Demanding a Mobile User" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl + "/login")
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("email").sendKeys("test")
      driver.findElementById("password").sendKeys("test")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementById("menuItem").click
      driver.findElementById("brandForm").click
      driver.findElementById("name").sendKeys("nokia")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementByCssSelector(".panel-title").getText.contains("Brand successfully added")
      driver.findElementById("menuItem").click
      driver.findElementById("createMobileModel").click
      new Select(driver.findElementById("brandName")).selectByVisibleText("nokia")
      driver.findElementById("modelName").sendKeys("Asha 200")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementById("stolenPhone").click
      driver.findElementById("userName").sendKeys("manish")
      new Select(driver.findElementById("brandId")).selectByVisibleText("nokia")
      new Select(driver.findElementById("mobileModelId")).selectByVisibleText("Asha 200")
      driver.findElementById("email").sendKeys("reseamanish@gmail.com")
      driver.findElementById("imeiMeid").sendKeys("123456789012347")
      driver.executeScript("""document.getElementById("purchaseDate").value="03/03/2014";""")
      driver.findElementById("contactNo").sendKeys("+91 1234567890")
      driver.findElementById("description").sendKeys("selenium test desc")
      driver.findElementById("fileUpload").sendKeys("/home/knoldus/Pictures/selenium.png")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("menuItem").click
      driver.findElementById("listOfRequest").click
      driver.findElementById("demandProof").click
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementByCssSelector("BODY").getText().contains("A Proof has been demanded from this user!")
    }
  }
}
