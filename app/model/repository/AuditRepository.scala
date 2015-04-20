package model.repository

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import utils._
import model.repository._
import play.api.Logger
import java.util.Date
import java.sql.Timestamp
import scala.collection.mutable.ListBuffer

/**
 * Define all data access layer methods of Auditing records
 */
trait AuditRepository extends AuditTable with MobileRepository {
  /*
   * Inserts new timestamp when an imei number check
   * @param timestamp, object of Audit
   * @return auto generated id
   */
  def insertTimestamp(audit: Audit): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insertTimestamp")
        Right(autoKeyAudits.insert(audit))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert audit record" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Gets time stamp of a particular imei number
   * @param imeid of mobile
   * @return list of Audit object
   */
  def getAllTimestampsByIMEID(imeid: String): List[Audit] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getAllTimestampsByIMEID" + imeid)
      audits.filter(_.mobileIMEID === imeid).list
    }
  }

  /**
   * Get all time stamps records
   * @return list of object of Audit instances
   */
  def getAllTimestamps: List[Audit] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getAllTimestamps")
      audits.list
    }
  }

  def getRecordByDate(year: String): List[Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      val empty: ListBuffer[Int] = ListBuffer()
      for (i <- 1 to 12) {
        if (i < 10) {
          val to = CommonUtils.getSqlDate(("0" + i.toString() + "/01/" + year))
          val from = CommonUtils.getSqlDate(("0" + i.toString() + "/31/" + year))
          val s = mobiles.filter { mobile => mobile.registrationDate >= to && mobile.registrationDate <= from }
          empty += s.list.length
        } else {
          val to = CommonUtils.getSqlDate((i.toString() + "/01/" + year))
          val from = CommonUtils.getSqlDate((i.toString() + "/31/" + year))
          val s = mobiles.filter { mobile => mobile.registrationDate >= to && mobile.registrationDate <= from }
          empty += s.list.length
        }
      }
      empty.toList
    }
  }
  
}

/**
 * Defines schema of audits table
 */
trait AuditTable {
  private[repository] class Audits(tag: Tag) extends Table[Audit](tag, "audits") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobileIMEID: Column[String] = column[String]("mobile_imeid", O.NotNull)
    def timestamp: Column[Timestamp] = column[Timestamp]("timestamp", O.NotNull)
    def * : scala.slick.lifted.ProvenShape[Audit] = (mobileIMEID, timestamp, id) <> (Audit.tupled, Audit.unapply)
  }
  //create audit table instance
  val audits = TableQuery[Audits]
  // create audit table instance with return auto generated id
  val autoKeyAudits = audits returning audits.map(_.id)
}

/**
 * Represents audit object which is used to report how many request
 * has been made against particular IMEI with timestamp
 * @param mobuileIMEID, imei number of mobile
 * @param timestamp, date and time of audit
 * @param id, auto incremented id
 */
case class Audit(
  mobileIMEID: String,
  timestamp: Timestamp,
  id: Option[Int] = None)

/**
 * Represents audit form
 * @param imeiMeid, imei number of mobile
 */
case class AuditForm(imeiMeid: String)

/**
 * Object Wraps methods of the trait AuditRepository
 */
object AuditRepository extends AuditRepository
