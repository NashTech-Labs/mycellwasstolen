package model.repository
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import scala.slick.driver.PostgresDriver.simple._
import play.api.Logger
import model.repository._
import scala.slick.driver.PostgresDriver.simple._
import play.api.Logger
import utils.Connection

/**
 * MobileRepository provides all concrete implementation of
 * user mobile services
 */

trait MobileRepository extends MobileTable {
  import utils.StatusUtil.Status

  /**
   * Inserts new Mobile Registration Record
   * @param: Mobile: Object of Mobile case class
   * @return, id of new inserted mobile
   */
  def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insetMobileUser")
        Right(autoKeyMobiles.insert(mobile))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Returns Mobile object
   * @param: IMEID, IMEID of registered mobile
   */
  def getMobileUserByIMEID(imeid: String): Option[Mobile] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileRecordByIMEID" + imeid)
      (for { mobile <- mobiles if ((mobile.imeiMeid === imeid) || (mobile.otherImeiMeid === imeid)) } yield mobile).firstOption
    }
  }

  /**
   * Changes the status of registered mobile to "approved"
   * @param: mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        val updateQuery = for {
          mobile <- mobiles if (mobile.imeiMeid === mobileUser.imeiMeid)
        } yield mobile.mobileStatus
        Logger.info("updateQuery data:" + updateQuery.updateStatement)
        updateQuery.update(mobileUser.mobileStatus)
        Right(updateQuery.update(mobileUser.mobileStatus))
      } catch {
        case ex: Exception =>
          Logger.info("Error in update user method: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }

  /**
   * Changes the status of registered mobile to "profDemanded"
   * @param: mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
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
   * Change registration type (Stolen or Clean)
   * @param: mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
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
   *  @param: status, (approved,pending and demandedProof)
   *  @returns: list of mobile brand and model name
   */
  def getAllMobilesUserWithBrandAndModel(status: String): List[(Mobile, String, String)] = {
    Connection.databaseObject withSession { implicit session: Session =>
      Logger.info("Calling getAllMobilesWithBrandAndModel with " + Status.withName(status))
      (for {
        mobile <- mobiles if (mobile.mobileStatus === Status.withName(status))
        brand <- brands if (brand.id === mobile.brandId)
        model <- models if (model.id === mobile.mobileModelId)
      } yield (mobile, brand.name, model.name)).list
    }
  }

  /**
   * Change new registered mobile to status to pending
   * @param: mobileUser, Object of Mobile
   * @return id of updated record
   */
  def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
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
   * Deletes a mobile Record
   * @param: IMEID, IMEID of registered mobile
   * @return id of deleted record
   */
  def deleteMobileUser(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      try {
        Logger.info("Delet mobile user:" + imeid)
        Right(mobiles.filter(_.imeiMeid === imeid).delete)
      } catch {
        case ex: Exception =>
          Logger.info("Error in delete mobile: " + ex.printStackTrace())
          Left(ex.getMessage())
      }
    }
  }
}

// Mapping of mobile Table
trait MobileTable extends BrandTable with ModelTable {
  import utils.StatusUtil.Status
  private[MobileTable] class Mobiles(tag: Tag) extends Table[Mobile](tag, "mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username", O DBType ("VARCHAR(1000)"))
    def brandId: Column[Int] = column[Int]("mobile_brandId")
    def mobileModelId: Column[Int] = column[Int]("mobile_modelId")
    def imeiMeid: Column[String] = column[String]("imei_meid", O DBType ("VARCHAR(1000)"))
    def otherImeiMeid: Column[String] = column[String]("other_imei_meid", O DBType ("VARCHAR(1000)"))
    def purchaseDate: Column[String] = column[String]("purchase_date")
    def contactNo: Column[String] = column[String]("contact_no", O.NotNull, O DBType ("VARCHAR(1000)"))
    def email: Column[String] = column[String]("email", O DBType ("VARCHAR(1000)"))
    def regType: Column[String] = column[String]("type", O DBType ("VARCHAR(20)"))
    def mobileStatus: Column[Status.Value] = column[Status.Value]("status", O DBType ("VARCHAR(50)"))
    def description: Column[String] = column[String]("description", O DBType ("VARCHAR(3000)"))
    def registrationDate: Column[String] = column[String]("registration_date", O.NotNull)
    def document: Column[String] = column[String]("document", O DBType ("VARCHAR(1000)"))
    def otherMobileBrand: Column[String] = column[String]("otherMobileBrand", O DBType ("VARCHAR(1000)"))
    def otherMobileModel: Column[String] = column[String]("otherMobileModel", O DBType ("VARCHAR(1000)"))
    def * : scala.slick.lifted.ProvenShape[Mobile] = (userName, brandId, mobileModelId, imeiMeid, otherImeiMeid, purchaseDate, contactNo, email,
      regType, mobileStatus, description, registrationDate, document, otherMobileBrand, otherMobileModel, id) <> ((Mobile.apply _).tupled, Mobile.unapply)
    def mobileIndex: scala.slick.lifted.Index = index("idx_email", (imeiMeid, email), unique = true)
  }
  val mobiles = TableQuery[Mobiles]
  val autoKeyMobiles = mobiles returning mobiles.map(_.id)

}
import utils.StatusUtil.Status
//Represents the Mobile registration Record
case class Mobile(
  userName: String,
  brandId: Int,
  mobileModelId: Int,
  imeiMeid: String,
  otherImeiMeid: String,
  purchaseDate: String,
  contactNo: String,
  email: String,
  regType: String,
  mobileStatus: Status.Value,
  description: String,
  regDate: String,
  document: String,
  otherMobileBrand: String,
  otherMobileModel: String,
  id: Option[Int] = None)

//Represents the Mobile Details Record
case class MobileDetail(
  userName: String,
  mobileName: String,
  mobileModel: String,
  imeiMeid: String,
  otherImeiMeid: String,
  mobileStatus: String,
  purchaseDate: String,
  contactNo: String,
  email: String,
  regType: String,
  otherMobileBrand: String,
  otherMobileModel: String)

//Represents Registered Mobile Status in the database
case class MobileStatus(imeiMeid: String)

//Represents Mobile registration Form  
case class MobileRegisterForm(
  userName: String,
  brandId: Int,
  mobileModelId: Int,
  imeiMeid: String,
  otherImeiMeid: String,
  purchaseDate: String,
  contactNo: String,
  email: String,
  regType: String,
  document: String,
  description: String,
  otherMobileBrand: String,
  otherMobileModel: String)

case class MobilesNameForm(mobileName: String)
case class User(email: String, password: String)

/**
 * Companion Object extending the Same trait:
 */
object MobileRepository extends MobileRepository
