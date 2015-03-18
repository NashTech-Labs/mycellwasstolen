package model.domains
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
object Domain {
  /**
   *  Provides all types of Status
   */
  object Status extends Enumeration {
    val pending = Value("pending")
    val approved = Value("approved")
    val proofdemanded = Value("proofdemanded")
  }
  /**
   *  implicit convert Enumerations into String and vice-versa
   *
   */
  implicit val mobileStatusMapper = MappedColumnType.base[Status.Value, String](

    { enuStatus => enuStatus.toString() },
    {
      strStatus =>
        strStatus match {
          case "pending"       => Status(0)
          case "approved"      => Status(1)
          case "proofdemanded" => Status(2)
        }
    })

  case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
    lazy val prev = Option(page - 1).filter(_ >= 0)
    lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
  }

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

  case class Brand(
    name: String,
    date: String,
    id: Option[Int] = None)

  case class MobileModels(
    mobileModel: String,
    mobileName: Int,
    id: Option[Int] = None)

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

  class Mobiles(tag: Tag) extends Table[Mobile](tag, "mobiles") {
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

  class Brands(tag: Tag) extends Table[Brand](tag, "brands") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O DBType ("VARCHAR(30)"))
    def date: Column[String] = column[String]("date", O.NotNull)
    def * : scala.slick.lifted.ProvenShape[Brand] = (name, date, id) <> (Brand.tupled, Brand.unapply)
  }
  val brands = TableQuery[Brands]
  val autoKeyBrands = brands returning brands.map(_.id)

  class MobileModel(tag: Tag) extends Table[MobileModels](tag, "mobilesmodel") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def brandId: Column[Int] = column[Int]("brandId")
    def modelName: Column[String] = column[String]("modelName", O DBType ("VARCHAR(30)"))
    def * : scala.slick.lifted.ProvenShape[MobileModels] = (
      modelName, brandId, id) <> (MobileModels.tupled, MobileModels.unapply)
    def mobilebrand: Object = foreignKey("SUP_FK", brandId, brands)(_.id.get, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)
  }
  val mobileModel = TableQuery[MobileModel]
  val autoKeyModels = mobileModel returning mobileModel.map(_.id)

  case class User(
    email: String,
    password: String)
}
