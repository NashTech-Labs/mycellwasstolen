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
 * Trait: MobileRepository provides all concrete implementation of 
 * Mobile repository   
 */

trait MobileRepository extends MobileTable {
  import utils.DBUtils.Status
  val PAGINATION_SIZE = 10
  /**
   * Inserts a Mobile Registration Record
   * @param: Mobile: Object of Case class Mobile
   */
  def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyMobiles.insert(mobile))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Returns Mobile Record Id by IMEID
   * @param: IMEID: IMEID of registered mobile
   */
  def getMobileRecordByIMEID(imeid: String): List[Mobile] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileRecordByIMEID" + imeid)
      mobiles.filter(_.imeiMeid === imeid).list
    }
  }

  /**
   * Returns List of mobile Brands
   *
   */

  def getMobilesName: List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobilesName")
      brands.list
    }
  }

  /**
   * Inserts a Brand record
   * @param: Brand: Object of Case class Mobile
   */
  def insertMobileName(brand: Brand): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyBrands.insert(brand))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert mobile name" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   *  Returns List of mobile Brands
   *  @param: BrandId: Brand Id of the Mobile
   */

  def getMobileNamesById(id: Int): List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + id)
      brands.filter(_.id === id).list
    }
  }

  /**
   * Changes the status of Mobile registration to "approved"
   * @param: Mobile: Object of Case class Mobile
   */
  def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int] =
    {

      Connection.databaseObject().withSession {
        implicit session: Session =>
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
   * Changes the status of Mobile registration to "profDemanded"
   * @param: Mobile: Object of Case class Mobile
   */
  def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int] =
    {
      Connection.databaseObject().withSession {
        implicit session: Session =>
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
   * @param: Mobile: Object of Case class Mobile
   */

  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
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
   *  Retrieving all brands and models 
   *  @returns: Mobile Registration Page 
   *  
   */

  def getAllMobilesWithBrandAndModel(status: String, page: Int = 0, pageSize: Int = PAGINATION_SIZE): Page[(Mobile, String, String)] = {
    Connection.databaseObject withSession { implicit session: Session =>
      val offset = pageSize * page
      Logger.info("Calling getAllMobilesWithBrandAndModel with " + Status.withName(status))

      val query =
        (for {
          mobile <- mobiles if (mobile.mobileStatus === Status.withName(status))
          brand <- brands if (brand.id === mobile.brandId)
          mobileModel <- mobileModel if (mobileModel.id === mobile.mobileModelId)

        } yield (mobile, brand.name, mobileModel.modelName)).drop(offset).take(pageSize)

      val totalRows = mobiles.filter(_.mobileStatus === Status.withName(status)).list.size
      val result = query.list
      println(totalRows)
      Page(result, page, offset, totalRows)
    }
  }

  /**
   * Changes Mobile registration status to pending
   * @param: Mobile: Object of Case class Mobile
   */
  def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
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
   */

  def deleteMobile(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
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

/**
 * Provides Table Query Value for Trait: MobileRepository
 */

trait MobileTable extends BrandTable with ModelTable {
  import utils.DBUtils.Status
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
    def mobilebrand: Object = foreignKey("SUP_FK", brandId, brands)(_.id.get)
    def mobilemodel: Object = foreignKey("SUP_FK", mobileModelId, mobileModel)(_.id.get)
  }
  val mobiles = TableQuery[Mobiles]
  val autoKeyMobiles = mobiles returning mobiles.map(_.id)

}
import utils.DBUtils.Status

//Reperents the Mobile registration Record
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

//Reperents the Mobile Details Record
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

//Reperents Registered Mobile Status in the database
case class MobileStatus(
  imeiMeid: String)
  
//Reperents Mobile registration Form  
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

  
//Represents a brand name  
case class BrandForm(
  name: String)

case class MobilesNameForm(
  mobileName: String)

case class MobilesModelForm(
  mobileName: String,
  mobileModel: String)
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

/**
 * Companion Object extending the Same trait: 
 * 
 */

object MobileRepository extends MobileRepository
