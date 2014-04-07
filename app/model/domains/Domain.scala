package model.domains

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ForeignKeyQuery

object Domain {

  /**
   *
   *
   */

  object Status extends Enumeration {
    val pending = Value("pending")
    val approved = Value("approved")
    val proofdemanded = Value("proofdemanded")
  }

  implicit val mobileStatusMapper = MappedTypeMapper.base[Status.Value, String](
    { enuStatus => enuStatus.toString() },
    {
      strStatus =>
        strStatus match {
          case "pending" => Status(0)
          case "approved" => Status(1)
          case "proofdemanded" => Status(2)
        }
    })


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
    otherImeiMeid : String,
    mobileStatus: String,
    purchaseDate: String,
    contactNo: String,
    email: String,
    regType: String,
    otherMobileBrand: String,
    otherMobileModel: String)

  case class MobileStatus(
    imeiMeid: String 
  )

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

  object Mobiles extends Table[Mobile]("mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username",O DBType ("VARCHAR(1000)"))
    def brandId: Column[Int] = column[Int]("mobile_brandId")
    def mobileModelId: Column[Int] = column[Int]("mobile_modelId")
    def imeiMeid: Column[String] = column[String]("imei_meid", O DBType ("VARCHAR(1000)"))
    def otherImeiMeid: Column[String] = column[String]("other_imei_meid", O DBType ("VARCHAR(1000)"))
    def purchaseDate: Column[String] = column[String]("purchase_date")

    def contactNo: Column[String] = column[String]("contact_no", O.NotNull, O DBType ("VARCHAR(1000)"))
    def email: Column[String] = column[String]("email", O DBType ("VARCHAR(1000)"))
    def regType: Column[String] = column[String]("type", O DBType ("VARCHAR(20)"))
    def mobileStatus: Column[Status.Value] = column[Status.Value]("status",  O DBType ("VARCHAR(50)"))
    def description: Column[String] = column[String]("description",  O DBType ("VARCHAR(1000)"))
    def registrationDate: Column[String] = column[String]("registration_date", O.NotNull)
    def document: Column[String] = column[String]("document", O DBType ("VARCHAR(1000)"))
    def otherMobileBrand: Column[String] = column[String]("otherMobileBrand", O DBType ("VARCHAR(1000)"))
    def otherMobileModel: Column[String] = column[String]("otherMobileModel", O DBType ("VARCHAR(1000)"))

    def * : scala.slick.lifted.MappedProjection[Mobile, (String, Int, Int, String, String, String, String, String, String, Status.Value, String, String, String, String, String, Option[Int])] =
      userName ~ brandId ~ mobileModelId ~ imeiMeid ~ otherImeiMeid ~ purchaseDate ~ contactNo ~ email ~
        regType ~ mobileStatus ~ description ~ registrationDate ~ document ~ otherMobileBrand ~ otherMobileModel ~ id <> (Mobile, Mobile unapply _)

    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Mobile, Option[Int]] =
      userName ~ brandId ~ mobileModelId ~ imeiMeid ~ otherImeiMeid ~ purchaseDate ~ contactNo ~
        email ~ regType ~ mobileStatus ~ description ~ registrationDate ~ document ~
        otherMobileBrand ~ otherMobileModel <> (
          { (username, brandId, mobileModelId, imeiMeid, otherImeiMeid, purchaseDate, contactNo, email,
            regType, mobileStatus, description, registrationDate, document, otherMobileBrand, otherMobileModel) =>
            Mobile(username, brandId, mobileModelId, imeiMeid, otherImeiMeid, purchaseDate, contactNo, email,
              regType, mobileStatus, description, registrationDate, document, otherMobileBrand, otherMobileModel)
          },
          { mobileregistration: Mobile =>
            Some((mobileregistration.userName, mobileregistration.brandId, mobileregistration.mobileModelId, mobileregistration.imeiMeid, mobileregistration.otherImeiMeid,
              mobileregistration.purchaseDate, mobileregistration.contactNo,
              mobileregistration.email, mobileregistration.regType, mobileregistration.mobileStatus,
              mobileregistration.description, mobileregistration.regDate, mobileregistration.document, mobileregistration.otherMobileBrand,
              mobileregistration.otherMobileModel))
          }) returning id
  }

  object Brands extends Table[Brand]("brands") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name",O DBType ("VARCHAR(30)"))
    def date: Column[String] = column[String]("date", O.NotNull)

    def * : scala.slick.lifted.MappedProjection[Brand, (String, String, Option[Int])] = name ~ date ~ id <> (Brand, Brand unapply _)

    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Brand, Option[Int]] = name ~ date <> ({
      (name, brand) => Brand(name, brand)
    },
      {
        brand: Brand => Some(brand.name, brand.date)
      }) returning id
  }

  object MobileModel extends Table[MobileModels]("mobilesmodel") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobilesnameid: Column[Int] = column[Int]("mobilesnameid")
    def model: Column[String] = column[String]("model", O DBType ("VARCHAR(30)"))

    def * : scala.slick.lifted.MappedProjection[MobileModels, (String, Int, Option[Int])] =
      model ~ mobilesnameid ~ id <> (MobileModels, MobileModels unapply _)

    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[MobileModels, Option[Int]] = mobilesnameid ~ model <> ({
      (mobilesnameid, mobilemodel) => MobileModels(mobilesnameid.toString, mobilemodel.toInt)
    },
      {
        mobilemodel: MobileModels => Some((mobilemodel.mobileName, mobilemodel.mobileModel))
      }) returning id

    def brandFkey: ForeignKeyQuery[Brands.type, Brand] = foreignKey("mobilemodal_brand_fkey", mobilesnameid, Brands)(_.id.get)
  }

  case class User(
    email: String,
    password: String)
}
