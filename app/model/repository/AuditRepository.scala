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

trait AuditTable {
  private[repository] class Audits(tag: Tag) extends Table[Audit](tag, "audits") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobileIMEID: Column[String] = column[String]("mobile_imeid", O.NotNull)
    def timestamp: Column[Timestamp] = column[Timestamp]("timestamp", O.NotNull)
    def * : scala.slick.lifted.ProvenShape[Audit] = (mobileIMEID, timestamp, id) <> (Audit.tupled, Audit.unapply)
  }
  val audits = TableQuery[Audits]
  val autoKeyAudits = audits returning audits.map(_.id)
}

/**
 * Represents audit object
 */
case class Audit(
  mobileIMEID: String,
  timestamp: Timestamp,
  id: Option[Int] = None)

/**
 * Represents audit form
 */
case class AuditForm(imeiMeid: String)

object AuditRepository extends AuditRepository
