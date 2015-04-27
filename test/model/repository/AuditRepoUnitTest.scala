package model.repository

import org.scalatest.FunSuite
import model.repository._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils._
import model.repository.AuditRepository.audits
import scala.slick.driver.PostgresDriver.simple._

class AuditRepoTest extends FunSuite {
  val timeStamp = new java.sql.Timestamp(new java.util.Date().getTime)

  val auditTimestamp = Audit("123456789012345", timeStamp, Some(1))

  val mobileUser = Mobile(
    "sushil", 1, 1, "123456789012345", "123456789012677", "+91 9839839830",
    "gs@gmail.com", "stolen", StatusUtil.Status.pending, CommonUtils.getSqlDate(), "gaurav.png")

  //Tests insertion of a timeStamp
  test("AuditRepository: insertTimestamp ") {
    running(FakeApplication()) {
      val returnedValue = AuditRepository.insertTimestamp(auditTimestamp)
      assert(returnedValue === Right(Some(1)))
    }
  }

  test("AuditRepository: insertTimestamp -> failed") {
    running(FakeApplication()) {
      Connection.databaseObject().withSession { implicit session: Session =>
        audits.ddl.drop
      }
      val returnValueOnChange = AuditRepository.insertTimestamp(auditTimestamp)
      assert(returnValueOnChange.isLeft)
    }
  }

  //Test listing of all TimeStamp with an IMEID
  test("AuditRepository: getAllTimestampsByIMEID") {
    running(FakeApplication()) {
      val imeiInserted = "123456789012345"
      AuditRepository.insertTimestamp(auditTimestamp)
      val returnedValue = AuditRepository.getAllTimestampsByIMEID(imeiInserted)
      assert(returnedValue === List(auditTimestamp))
    }
  }

  //Test listing of all TimeStamps
  test("AuditRepository: getAllTimestamps") {
    running(FakeApplication()) {
      AuditRepository.insertTimestamp(auditTimestamp)
      val returnedValue = AuditRepository.getAllTimestamps
      assert(returnedValue === List(auditTimestamp))
    }
  }
  //Test getRecordByDate 
  test("AuditRepository: getRecordByDate") {
    running(FakeApplication()) {
      MobileRepository.insertMobileUser(mobileUser)
      val returnedValue = AuditRepository.getRegistrationRecordsByYear("2015")
      assert(returnedValue === List(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
  }

  //Test getTopNLostBrands 
  test("AuditRepository: getTopNLostBrands with Some Value of Mobile Record") {
    running(FakeApplication()) {
      BrandRepository.insertBrand(Brand("Sigma", Some(1)))
      //Insert a Model Record
      ModelRepository.insertModel((Model("Sigma454", 1)))
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser((mobileUser))
      val returnedValue = AuditRepository.getTopNLostBrands(1)
      assert(returnedValue === Some(List(("Sigma454", 1)), 1))
    }
  }

  //Test getTopNLostBrands 
  test("AuditRepository: getTopNLostBrands with No Mobile Record") {
    running(FakeApplication()) {
      //Get TopNLost Brands without having any records in table
      val returnedValue = AuditRepository.getTopNLostBrands(1)
      assert(returnedValue === None)
    }
  }

  //Test getPerDayRegistration 
  test("AuditRepository: getPerDayRegistration") {
    running(FakeApplication()) {
      BrandRepository.insertBrand(Brand("Sigma", Some(1)))
      //Insert a Model Record
      ModelRepository.insertModel((Model("Sigma454", 1)))
      //Insert a Mobile Record first
      MobileRepository.insertMobileUser((mobileUser))
      //Get per day registration
      val returnedValue = AuditRepository.getPerDayRegistration
      assert(returnedValue === Some(List((CommonUtils.getSqlDate().toString(), 1))))
    }
  }

  //Test getPerDayRegistration with No data 
  test("AuditRepository: getPerDayRegistration -Negative") {
    running(FakeApplication()) {
      val returnedValue = AuditRepository.getPerDayRegistration
      assert(returnedValue === None)
    }
  }
  
  //Test getRegistrationStartingYear 
  test("AuditRepository: getRegistrationStartingYear") {
    running(FakeApplication()) {
      BrandRepository.insertBrand(Brand("Sigma", Some(1)))
      //Insert a Model Record
      ModelRepository.insertModel((Model("Sigma454", 1)))
      //Insert a Mobile Record first
      MobileRepository.insertMobileUser((mobileUser))
      val returnedValue = AuditRepository.getRegistrationStartingYear
      assert(returnedValue === Some(2015))
    }
  }

  
  //Test getRegistrationStartingYear 
  test("AuditRepository: getRegistrationStartingYear -Negative") {
    running(FakeApplication()) {
      val returnedValue = AuditRepository.getRegistrationStartingYear
      assert(returnedValue === None)
    }
  }

}
