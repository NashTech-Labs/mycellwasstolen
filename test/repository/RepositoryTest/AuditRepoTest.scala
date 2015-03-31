package repository.RepositoryTest

import org.scalatest.FunSuite
import model.repository.Audit
import play.api.test.FakeApplication
import play.api.test.Helpers._
import model.repository.AuditRepository
import java.util.Calendar

class AuditRepoTest extends FunSuite {
  val calender = Calendar.getInstance
  val now: java.util.Date = calender.getTime
  val timeStamp = new java.sql.Timestamp(now.getTime())

  val auditTimestamp = Audit("123456789012345", timeStamp, Some(1))

  //Tests insertion of a timeStamp
  test("AuditRepository: insert an Audit Record with Imeid and timestamp") {
    running(FakeApplication()) {
      val returnedValue = AuditRepository.insertTimestamp(auditTimestamp)
      assert(returnedValue === Right(Some(1)))
    }
  }

  //Test listing of all TimeStamp with an IMEID
  test("AuditRepository: list timestamps by an IMEID") {
    running(FakeApplication()) {
      val imeiInserted = "123456789012345"
      AuditRepository.insertTimestamp(auditTimestamp)
      val returnedValue = AuditRepository.getAllTimestampsByIMEID(imeiInserted)
      assert(returnedValue === List(auditTimestamp))
    }
  }

  //Test listing of all TimeStamps
  test("AuditRepository: list all timestamps") {
    running(FakeApplication()) {
      AuditRepository.insertTimestamp(auditTimestamp)
      AuditRepository.insertTimestamp(auditTimestamp)
      val returnedValue = AuditRepository.getAllTimestamps
      assert(returnedValue === List(auditTimestamp, Audit("123456789012345", timeStamp, Some(2))))
    }
  }

}