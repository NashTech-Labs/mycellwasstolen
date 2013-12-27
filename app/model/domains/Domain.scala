package model.domains

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ForeignKeyQuery

object Domain {

  /**
   *  Question  object and mapping with question table in database
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
    mobileName: String,
    mobileModel: String,
    imeiMeid: String,
    purchaseDate: String,
    contactNo: String,
    email: String,
    regType: String,
    mobileStatus: Status.Value,
    description: String,
    regDate: String,
    document: String,
    otherMobileBrand:String,
    otherMobileModel:String,
    id: Option[Int] = None)

  case class MobileDetail(
    userName: String,
    mobileName: String,
    mobileModel: String,
    imeiMeid: String,
    purchaseDate: String,
    contactNo: String,
    email: String,
    regType: String,
    otherMobileBrand:String,
    otherMobileModel:String)

  case class MobileStatus(
    imeiMeid: String)

  case class Brand(
    name: String,
    date: java.sql.Date,
    id: Option[Int] = None)

  case class MobileModels(
    mobileModel: String,
    mobileName: Int,
    id: Option[Int] = None)

  case class MobileRegisterForm(
    userName: String,
    mobileName: String,
    mobileModel: String,
    imeiMeid: String,
    purchaseDate: String,
    contactNo: String,
    email: String,
    regType: String,
    document: String,
    description: String,
    otherMobileBrand:String,
    otherMobileModel:String)

  case class BrandForm(
    name: String)
 
   case class MobilesNameForm(
    mobileName:String)

   case class MobilesModelForm(
    mobileName: String,
    mobileModel: String)

  object Mobiles extends Table[Mobile]("mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileName: Column[String] = column[String]("mobile_name", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileModel: Column[String] = column[String]("mobile_model", O.NotNull, O DBType ("VARCHAR(100)"))
    def imeiMeid: Column[String] = column[String]("imei_meid", O.NotNull, O DBType ("VARCHAR(100)"))
    def purchaseDate: Column[String] = column[String]("purchase_date", O.NotNull)

    def contactNo: Column[String] = column[String]("contact_no", O.NotNull, O DBType ("VARCHAR(100)"))
    def email: Column[String] = column[String]("email", O.NotNull, O DBType ("VARCHAR(100)"))
    def regType: Column[String] = column[String]("type", O.NotNull, O DBType ("VARCHAR(20)"))
    def mobileStatus: Column[Status.Value] = column[Status.Value]("status", O.NotNull, O DBType ("VARCHAR(50)"))
    def description: Column[String] = column[String]("description", O.NotNull, O DBType ("VARCHAR(500)"))
    def registrationDate: Column[String] = column[String]("registration_date", O.NotNull)
    def document: Column[String] = column[String]("document", O.NotNull, O DBType ("VARCHAR(500)"))
    def otherMobileBrand: Column[String] = column[String]("otherMobileBrand", O.NotNull, O DBType ("VARCHAR(100)"))
    def otherMobileModel: Column[String] = column[String]("otherMobileModel", O.NotNull, O DBType ("VARCHAR(100)"))

    def * : scala.slick.lifted.MappedProjection[Mobile, (String, String, String, String, String, String, String, String, Status.Value, String, String, String, String, String, Option[Int])] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ regType ~ mobileStatus ~ description ~ registrationDate ~ document ~ otherMobileBrand~otherMobileModel~id <> (Mobile, Mobile unapply _)

    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Mobile, Option[Int]] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~
        email ~ regType ~ mobileStatus ~ description ~ registrationDate ~ document~otherMobileBrand~otherMobileModel<> (
          { (username, mobileName, mobileModel, imeiMeid, purchaseDate, contactNo, email, regType, mobileStatus, description, registrationDate, document,otherMobileBrand,otherMobileModel) =>
            Mobile(username, mobileName, mobileModel, imeiMeid, purchaseDate, contactNo, email, regType, mobileStatus, description, registrationDate, document,otherMobileBrand,otherMobileModel)
          },
          { mobileregistration: Mobile =>
            Some((mobileregistration.userName, mobileregistration.mobileName, mobileregistration.mobileModel, mobileregistration.imeiMeid,mobileregistration.purchaseDate, mobileregistration.contactNo,
              mobileregistration.email, mobileregistration.regType, mobileregistration.mobileStatus, mobileregistration.description, mobileregistration.regDate, mobileregistration.document,mobileregistration.otherMobileBrand,mobileregistration.otherMobileModel))
          }) returning id
  }

  object Brands extends Table[Brand]("brands") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull, O DBType ("VARCHAR(30)"))
    def date: Column[java.sql.Date] = column[java.sql.Date]("date", O.NotNull)

    def * : scala.slick.lifted.MappedProjection[Brand, (String, java.sql.Date, Option[Int])] = name ~ date ~ id <> (Brand, Brand unapply _)

    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Brand, Option[Int]] = name ~ date <> ({
      (name, brand) => Brand(name, brand)
    },
      {
        brand: Brand => Some(brand.name, brand.date)
      }) returning id
  }

  object MobileModel extends Table[MobileModels]("mobilesmodel") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobilesnameid: Column[Int] = column[Int]("mobilesnameid", O.NotNull)
    def model: Column[String] = column[String]("model", O.NotNull, O DBType ("VARCHAR(30)"))

    def * : scala.slick.lifted.MappedProjection[MobileModels, (String, Int, Option[Int])] =
      model ~ mobilesnameid ~id <> (MobileModels, MobileModels unapply _)

    def brandFkey: ForeignKeyQuery[Brands.type, Brand] = foreignKey("mobilemodal_brand_fkey", mobilesnameid, Brands)(_.id.get)
  }

  case class User(
    email:String,
    password:String)
}
