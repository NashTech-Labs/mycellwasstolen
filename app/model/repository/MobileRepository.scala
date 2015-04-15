package model.repository

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ProvenShape
import utils.Connection
import model.repository._
import play.api.Logger
import utils._
import scala.collection.mutable.ListBuffer
import utils.StatusUtil.Status
import java.sql.Date
import scala.slick.lifted.ForeignKeyQuery

/**
 * Define all data access layer methods of MobileRegistration
 */

trait MobileRepository extends MobileTable {

  /**
   * Inserts new Mobile Registration Record
   * @param Mobile: Object of Mobile case class
   * @return id of new inserted mobile
   */
  def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insetMobileUser")
        Right(autoKeyMobiles.insert(mobile))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in mobile registration" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Returns Mobile object
   * @param IMEID, IMEID of registered mobile
   */
  def getMobileUserByIMEID(imeid: String): Option[Mobile] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileRecordByIMEID" + imeid)
      val query = (for { mobile <- mobiles if ((mobile.imei === imeid) || (mobile.otherImei === imeid)) } yield mobile)
      Logger.warn("Query generated is - " + query.selectStatement)
      (for { mobile <- mobiles if ((mobile.imei === imeid) || (mobile.otherImei === imeid)) } yield mobile).firstOption
    }
  }

  /**
   * Changes the status of registered mobile to "approved"
   * @param mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToApproveByIMEID(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        Logger.info("Calling getMobileRecordByIMEID" + imeid)
        Right(mobiles.filter(_.imei === imeid).map(_.mobileStatus).update(utils.StatusUtil.Status.approved))
      } catch {
        case ex: Exception =>
          Logger.info("Error in changeStatusToApprovedByIMEID: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }

  /**
   * Changes the status of registered mobile to "profDemanded"
   * @param mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToDemandProofByIMEID(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        Logger.info("Calling getMobileRecordByIMEID" + imeid)
        Right(mobiles.filter(_.imei === imeid).map(_.mobileStatus).update(utils.StatusUtil.Status.proofdemanded))
      } catch {
        case ex: Exception =>
          Logger.info("Error in changeStatusToDemandProofByIMEID: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }

  /**
   * Change registration type (Stolen or Clean)
   * @param mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        val updateQuery = mobiles.filter { mobile => mobile.imei === mobileUser.imei }
        Logger.info("updateQuery data:" + updateQuery)
        Right(updateQuery.update(mobileUser))
      } catch {
        case ex: Exception =>
          Logger.info("Error in update user method: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }

  /**
   *  Retrieving all mobile user with brand and model based on status
   *  @param status, (approved,pending and demandedProof)
   *  @return list of mobile brand and model name
   */
  def getAllMobilesUserWithBrandAndModel(status: String): List[(Mobile, String, String)] = {
    Connection.databaseObject withSession { implicit session: Session =>
      Logger.info("Calling getAllMobilesWithBrandAndModel with " + Status.withName(status))
      (for {
        mobile <- mobiles if (mobile.mobileStatus === Status.withName(status))
        brand <- brands if (brand.id === mobile.brandId)
        model <- models if (model.id === mobile.modelId)
      } yield (mobile, brand.name, model.name)).list
    }
  }

  /**
   * Change new registered mobile to status to pending
   * @param mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToPendingByIMEID(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        Logger.info("Calling getMobileRecordByIMEID" + imeid)
        Right(mobiles.filter(_.imei === imeid).map(_.mobileStatus).update(utils.StatusUtil.Status.pending))
      } catch {
        case ex: Exception =>
          Logger.info("Error in changeStatusToPendingByIMEID: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }

  /**
   * Deletes a mobile Record
   * @param IMEID, IMEID of registered mobile
   * @return id of deleted record
   */
  def deleteMobileUser(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        Logger.info("Delet mobile user:" + imeid)
        Right(mobiles.filter(_.imei === imeid).delete)
      } catch {
        case ex: Exception =>
          Logger.info("Error in delete mobile: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }
}

/**
 * Defines schema of Mobile table
 */
trait MobileTable extends BrandTable with ModelTable {
  import utils.StatusUtil.Status
  private[repository] class Mobiles(tag: Tag) extends Table[Mobile](tag, "mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username", O DBType ("VARCHAR(1000)"))
    def brandId: Column[Int] = column[Int]("mobile_brandId")
    def modelId: Column[Int] = column[Int]("mobile_modelId")
    def imei: Column[String] = column[String]("imei_meid", O DBType ("VARCHAR(1000)"))
    def otherImei: Column[String] = column[String]("other_imei_meid", O DBType ("VARCHAR(1000)"))
    def contactNo: Column[String] = column[String]("contact_no", O.NotNull, O DBType ("VARCHAR(1000)"))
    def email: Column[String] = column[String]("email", O DBType ("VARCHAR(1000)"))
    def regType: Column[String] = column[String]("type", O DBType ("VARCHAR(20)"))
    def mobileStatus: Column[Status.Value] = column[Status.Value]("status", O DBType ("VARCHAR(50)"))
    def registrationDate: Column[Date] = column[Date]("registration_date", O.NotNull)
    def document: Column[String] = column[String]("document", O DBType ("VARCHAR(1000)"))
    def * : scala.slick.lifted.ProvenShape[Mobile] = (userName, brandId, modelId, imei, otherImei, contactNo, email,
      regType, mobileStatus, registrationDate, document,id) <> ((Mobile.apply _).tupled, Mobile.unapply)
    def mobileIndex: scala.slick.lifted.Index = index("idx_imei", (imei), unique = true)

    def fkeyBrand: ForeignKeyQuery[Brands, Brand] = foreignKey("brandId_FK", brandId, brands)(_.id.get, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)

    def fkeyModel: ForeignKeyQuery[Models, Model] = foreignKey("ModelId_FK", modelId, models)(_.id.get, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)
  }
  val mobiles = TableQuery[Mobiles]
  val autoKeyMobiles = mobiles returning mobiles.map(_.id)

}

/**
 * Represents the Mobile registration Record
 */
case class Mobile(
  userName: String,
  brandId: Int,
  modelId: Int,
  imei: String,
  otherImei: String,
  contactNo: String,
  email: String,
  regType: String,
  mobileStatus: Status.Value,
  regDate: Date,
  document: String,
  id: Option[Int] = None)

/**
 * Represents the Mobile Details Record
 */
case class MobileDetail(
  userName: String,
  brandName: String,
  modelName: String,
  imei: String,
  otherImei: String,
  mobileStatus: String,
  contactNo: String,
  email: String,
  regType: String)

/**
 * Represents Registered Mobile Status in the database
 */
case class MobileStatus(imeiMeid: String)

/**
 * Represents Mobile registration Form
 */
case class MobileRegisterForm(
  userName: String,
  brandId: Int,
  modelId: Int,
  imei: String,
  otherImei: String,
  contactNo: String,
  email: String,
  regType: String,
  document: String)

/**
 * Represents a Models name
 */
case class MobilesNameForm(mobileName: String)

/**
 * Represents a user with username and password
 */
case class User(email: String, password: String)

/**
 * Wraps the method of trait:MobileRepository
 */
object MobileRepository extends MobileRepository
