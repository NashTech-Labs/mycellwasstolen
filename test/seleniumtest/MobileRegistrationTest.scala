package seleniumtest

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

class MobileRegistrationTest extends Specification {
  val port = 19001
  val baseUrl = "http://localhost:19001"

   "Testing Home Page" in {
    running(TestServer(port), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)
      browser.title() must equalTo("Welcome to MCWS")
    }
  }

  "Testing Add Mobile Brand" in {
    running(TestServer(port), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)
      browser.title() must equalTo("Welcome to MCWS")
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
    }
  }

  "Testing Add  Mobile Model" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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
      driver.findElementByCssSelector("BODY").getText().contains("Mobile Model successfully added")

    }
  }

  "Testing Stolen Mobile Registration" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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
      driver.findElementByCssSelector("BODY").getText().contains("Mobile registered successfully")

    }
  }

  "Testing Secure Registration" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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
      driver.findElementById("securePhone").click
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
      driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS)
      driver.findElementByCssSelector("BODY").getText().contains("Mobile registered successfully")

    }
  }

  "Change Registration Type from stolen to clean" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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
      driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS)
      driver.findElementById("menuItem").click
      driver.findElementById("changeCellStatus").click
      driver.findElementById("imeiMeid").sendKeys("123456789012347")
      driver.findElementByCssSelector(".btn.btn-primary").click
      driver.findElementById("changeRegType").click
      driver.findElementByCssSelector("BODY").getText().contains("registration type changed")
    }
  }

  "Proof Demanding a Mobile User" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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

  "Approving a request" in {
    running(TestServer(port, FakeApplication(additionalConfiguration = inMemoryDatabase())), HTMLUNIT) { browser =>
      val driver = new FirefoxDriver
      driver.manage().window().maximize()
      driver.get(baseUrl)
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
      driver.findElementById("adminPanel").click
      driver.findElementById("email").sendKeys("admin")
      driver.findElementById("password").sendKeys("knol2013")
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
      driver.findElementById("approve").click
      driver.findElementByCssSelector("BODY").getText().contains("Mobile has been approved successfully!")
    }
  }


}
  

