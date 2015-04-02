package repository.RepositoryTest

import org.scalatest.FunSuite
import model.repository._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils._

class AuditRepoTest extends FunSuite {
  val timeStamp = new java.sql.Timestamp(new java.util.Date().getTime)

  val auditTimestamp = Audit("123456789012345", timeStamp, Some(1))
  
   val mobileUser = Mobile(
    "sushil", 1, 1, "123456789012345", "123456789012677", CommonUtils.getSqlDate(), "+91 9839839830",
    "gs@gmail.com", "stolen", StatusUtil.Status.pending, "test", CommonUtils.getSqlDate(), "gaurav.png", "nokia", "E5")

  //Tests insertion of a timeStamp
  test("AuditRepository: insertTimestamp ") {
    running(FakeApplication()) {
      val returnedValue = AuditRepository.insertTimestamp(auditTimestamp)
      assert(returnedValue === Right(Some(1)))
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

  test("AuditRepository: getRecordByDate") {
    running(FakeApplication()) {
      MobileRepository.insertMobileUser(mobileUser)
      val returnedValue = AuditRepository.getRecordByDate("2015")
      assert(returnedValue === List(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
  }
}
