package seleniumtest

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session
import org.specs2.mutable.Specification
import play.api.test.WithServer
import play.api.libs.ws.WS
import play.api.test.Helpers._
import play.api.test.Helpers.await
import play.api.test.TestServer
import play.api.test.WithServer
import utils.Connection
import model.domains.Domain._
import java.util.concurrent.TimeUnit

class MobileRegistrationTest extends Specification{

  val port = 19001
  val baseUrl = "http://localhost:19001"
  
 /* "Testing Home Page" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS")      
    }
  }
  
  "Testing Add Mobile" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS")  
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("Nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
    }
  }*/
  
  "Testing Add  Mobile Model" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS") 
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("Nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
      browser.$("#menuItem").click
      browser.$("#createMobileModel").click
      browser.waitUntil(20, TimeUnit.SECONDS, browser.$("#mobileName").click())
      browser.find("Nokia").click
//      browser.$("#mobileName").click().find("Nokia").click
      browser.$("#mobileModel").text("5233")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Mobile Model successfully added")
    }
  }
  
 /* "Testing Stolen Mobile Registration" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS")
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("Nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
      browser.$("#menuItem").click
      browser.$("#createMobileModel").click
      browser.$("#mobileName").click().find("Nokia").click
      browser.$("#mobileModel").text("5233")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Mobile Model successfully added")
      browser.$("#menuItem").click
      browser.$("#logout").click
      browser.$("#stolenPhone").click
      browser.$("#userName").text("Swati")
      browser.$("#email").text("swati@knoldus.com")
      browser.$("#imeiMeid").text("1234567890")
      browser.$("#contactNo").text("+91 1234567890")
      browser.$("#description").text("good mobile")
      browser.$("#mobileName").click().find("Nokia").click()
      browser.$("#mobileModel").click().find("Nokia").click()
      browser.$("#fileUpload").text("/home/swati/Desktop/index.jpeg")
      browser.$(".btn.btn-primary").click
      browser.url must equalTo(baseUrl + "/")
    }
  }
  
  "Testing Secure Mobile Registration" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS")
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("Nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
      browser.$("#menuItem").click
      browser.$("#createMobileModel").click
      browser.$("#mobileName").click().find("Nokia").click
      browser.$("#mobileModel").text("5233")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Mobile Model successfully added")
      browser.$("#menuItem").click
      browser.$("#logout").click
      browser.$("#securePhone").click
      browser.$("#userName").text("Swati")
      browser.$("#email").text("swati@knoldus.com")
      browser.$("#imeiMeid").text("1234567890")
      browser.$("#contactNo").text("+91 1234567890")
      browser.$("#description").text("good mobile")
      browser.$("#mobileName").click().find("Nokia").click()
      browser.$("#mobileModel").click().find("Nokia").click()
      browser.$("#fileUpload").text("/home/swati/Desktop/index.jpeg")
      browser.$(".btn.btn-primary").click
      browser.url must equalTo(baseUrl + "/")
    }
  }
  
  "Testing Mobile Status" in {
    running(TestServer(19001), FIREFOX) { browser =>
      browser.webDriver.manage().window().maximize()
      browser.goTo(baseUrl)      
      browser.title() must equalTo("Welcome to MCWS")
      browser.$("#adminPanel").click
      browser.$("#email").text("admin")
      browser.$("#password").text("knol2013")
      browser.$(".btn.btn-primary").click
      browser.$("#menuItem").click
      browser.$("#brandForm").click
      browser.$("#name").text("Nokia")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Brand successfully added")
      browser.$("#menuItem").click
      browser.$("#createMobileModel").click
      browser.$("#mobileName").click().find("Nokia").click
      browser.$("#mobileModel").text("5233")
      browser.$(".btn.btn-primary").click
      browser.$(".alert.alert-dismissable.alert-success").getText() must contain("Mobile Model successfully added")
      browser.$("#menuItem").click
      browser.$("#logout").click
      browser.$("#stolenPhone").click
      browser.$("#userName").text("Swati")
      browser.$("#email").text("swati@knoldus.com")
      browser.$("#imeiMeid").text("1234567890")
      browser.$("#contactNo").text("+91 1234567890")
      browser.$("#description").text("good mobile")
      browser.$("#mobileName").click().find("Nokia").click()
      browser.$("#mobileModel").click().find("Nokia").click()
      browser.$("#fileUpload").text("/home/swati/Desktop/index.jpeg")
      browser.$(".btn.btn-primary").click
      browser.url must equalTo(baseUrl + "/")
      browser.$("mobileStatus").click()
      browser.$("#imeiMeid").text("1234567890")
      browser.$(".btn.btn-primary").click
      browser.$("#mobile-status").getText() must contain("secure")
    }
  }*/
  
  def deleteTestData() {
    Connection.databaseObject.withSession { implicit session: Session =>
        (for { mobile <- Mobiles } yield mobile).delete
        (for { brand <- Brands } yield brand).delete
      	(for { mobileModel <- MobileModel } yield mobileModel).delete
    }
  }
}