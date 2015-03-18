package model.repository
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import scala.slick.driver.PostgresDriver.simple._
import play.api.Logger
import model.repository._
import scala.slick.driver.PostgresDriver.simple._
import play.api.Logger

trait MobileRepository extends MobileTable {
  val PAGINATION_SIZE = 10
  //provide concrete implementation for all methods here
  def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      utils.Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyMobiles.insert(mobile))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }
  
  def getMobileRecordByIMEID(imeid: String): List[Mobile]
  def getMobilesName: List[Brand]
  def insertMobileName(brand: Brand): Either[String, Option[Int]]
  def getMobileNamesById(id: Int): List[Brand]
  def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int]
  def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int]
  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int]
  def getAllMobilesWithBrandAndModel(status: String, page: Int = 0, pageSize: Int = PAGINATION_SIZE): Page[(Mobile, String, String)]
  def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int]
  def deleteMobile(imeid: String): Either[String, Int]
}

trait MobileTable extends BrandTable with ModelTable{
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

case class MobileStatus(
  imeiMeid: String)
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

//object MobileRepository extends MobileRepository
