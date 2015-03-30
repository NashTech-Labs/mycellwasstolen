 package model.repository
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import utils.Connection
import model.repository._
import play.api.Logger
import java.util.Date

trait AuditRepository extends AuditTable {
  /*
   * Inserts new timestamp when an imei number check
   * @param timestamp, object of Audit
   * @return auto generated id
   */
  def insertTimestamp(timestamp: Audit): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insertTimestamp")
        Right(autoKeyAudits.insert(timestamp))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Gets timestamp of a particular imei number
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
   * Get all timestamps records
   * @return list of object of Audit instances
   */
  def getAllTimestamps: List[Audit] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getAllTimestamps")
      audits.list
    }
  }

}

trait AuditTable {
  private[AuditTable] class Audits(tag: Tag) extends Table[Audit](tag, "audits") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobileIMEID: Column[String] = column[String]("mobile_imeid", O.NotNull)
    def timestamp: Column[String] = column[String]("timestamp", O.NotNull)
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
  timestamp: String,
  id: Option[Int] = None)

/**
 * Represents audit form
 */
case class AuditForm(imeiMeid: String)

object AuditRepository extends AuditRepository
