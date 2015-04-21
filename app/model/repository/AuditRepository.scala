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

  /**
   * Returns List of top n lost brands with their lost_count from mobile table e.g.
   * List(('A',1),('B', 4),('C',50).. ('N',N))
   * (n< number of brands)
   * @param n, number of brands
   */
  def getTopNLostBrands(n: Int): Option[List[(String,Float)]] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      //Get modelId with count from Mobile table as tuple value
      mobiles.length.run>0 match{
        case true =>{
        	val countQuery = for {
        		(id, c) <- mobiles groupBy (_.modelId)
        	} yield id -> c.map(_.modelId).length
        	//Joint  this (modelId,count(modelId)) tuple with model table to fetch brandId of each model  
        	val brandQuery = for{
        		mId <- models
        		brands <- brands if(mId.brandId === brands.id)
        			brandCount <- countQuery if (brandCount._1 === mId.id) 
        	} yield (mId.name,brandCount._2)
        	//Sort the list by Highest count of models and select from highest to lowest
        	val topNCount = brandQuery.list.sortBy( (x)=> x._2).drop(brandQuery.list.size - n)
        	val sumOfTopNCounts = topNCount.map( x => x._2).sum
        	println("Top N is of : "+sumOfTopNCounts)
        	val totalTheftCount = mobiles.length.run
        	val otherModelsCount = totalTheftCount - sumOfTopNCounts
        	val otherCountTuple = ("Others",otherModelsCount)
        	val topNValuesWithOthers = otherCountTuple::topNCount
        	val floatValues = topNValuesWithOthers.map(x=> (x._1,(x._2.toFloat/totalTheftCount.toFloat)*100)) 
        	floatValues.foreach(println) 
        	floatValues match{
        	case x:List[(String,Float)] => Some(floatValues)
        	case _ => None
        	}
        }
        case false => None
      }
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
