package model.repository

import java.sql.Timestamp
import java.util.Date

import scala.collection.mutable.ListBuffer
import scala.slick.driver
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ProvenShape

import model.repository._
import play.api.Logger
import utils._

/**
 * Define all data access layer methods of Auditing records
 */
trait AuditRepository extends AuditTable with MobileRepository {
  /**
   * Inserts new TimeStamps when an IMEI number is searched
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
   * Gets TimeStamp of a particular IMEI number
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
   * Get all TimeStamps
   * @return list of object of Audit instances
   */
  def getAllTimestamps: List[Audit] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getAllTimestamps")
      audits.list
    }
  }

  /**
   * @param year
   * @return Number of Registrations per Month for the given year
   * e.g.  List(1,2,3,4,5,6,7,8,0,23,12,23) for month JAN to DEC
   */
  def getRegistrationRecordsByYear(year: String): List[Int] = {
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

  /**
   * Returns List of top n lost brands with their lost_count from mobile table along with
   * total theft  e.g.
   * (List(('A',1),('B', 4),('C',50)('N',6)),numberOfRowsInMobile)
   * (n< number of brands)
   * @param n, number of brands
   */
  def getTopNLostBrands(n: Int): Option[(List[(String, Int)], Int)] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      mobiles.length.run > 0 match {
        case true => {
          val countQuery = for {
            (id, c) <- mobiles groupBy (_.brandId)
          } yield id -> c.map(_.modelId).length
          val brandQuery = for {
            mId <- models
            brandCount <- countQuery if (brandCount._1 === mId.id)
          } yield (mId.name, brandCount._2)
          val topNCount = brandQuery.list.sortBy { case (modelName, modelCount) => modelCount }.takeRight(n)
          val totalTheftCount = mobiles.length.run
          topNCount match {
            case topNCounts: List[(String, Int)] => Some(topNCount, totalTheftCount)
            case _                               => None
          }
        }
        case false => None
      }
    }
  }

  /**
   * Returns a tuple of (List(date, registrationCount)
   * first element of tuple signifies the date  of registrations and
   * the second element of the tuple signifies registration for the date
   */
  def getPerDayRegistration: Option[List[(java.sql.Date, Int)]] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      mobiles.length.run > 0 match {
        case true => {
          val countQuery = for {
            (date, dateCount) <- mobiles groupBy (_.registrationDate)
          } yield date -> dateCount.map(_.registrationDate).length
          val returnValue = countQuery.list.map({ case (date, dateCount) => (date, dateCount) })
          Some(returnValue)
        }
        case false => None
      }
    }
  }

  /**
   * Returns registration Starting Year
   */
  def getRegistrationStartingYear: Option[Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      mobiles.length.run > 0 match {
        case true => {
          val minQuery = for {
            (date) <- mobiles.list.map(mobile => mobile.regDate)
          } yield date
          val returnValue = minQuery.map(date => date.toString().take(4).toInt).min
          Some(returnValue)
        }
        case false => None
      }
    }
  }
}

/**
 * Defines schema of audits tablenew
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
 * has been made against particular IMEI with TimeStamp
 * @param mobuileIMEID, IMEI number of mobile
 * @param timestamp, date and time of audit
 * @param id, auto incremented id
 */
case class Audit(
  mobileIMEID: String,
  timestamp: Timestamp,
  id: Option[Int] = None)

/**
 * Represents audit form
 * @param imeiMeid, IMEI number of mobile
 */
case class AuditForm(imeiMeid: String)

/**
 * Object Wraps methods of the trait AuditRepository
 */
object AuditRepository extends AuditRepository
