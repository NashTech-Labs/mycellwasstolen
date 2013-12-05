package model.domains

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ForeignKeyQuery

object Domain {

  /**
   *  Question  object and mapping with question table in database
   *
   */
  case class Mobile(
    userName:String,
    mobileName:String,
    mobileModel:String,
    imeiMeid:String,
    purchaseDate: java.sql.Date,
    contactNo:String,
    email: String,
    regType: String,
    mobileStatus: Status.Value,
    description: String,
    id: Option[Int] = None)
    
  case class MobileDetail(
    userName:String,
    mobileName:String,
    mobileModel:String,
    imeiMeid:String,
    purchaseDate: java.sql.Date,
    contactNo:String,
    email: String,
    regType: String)
    
  case class MobileStatus(
    imeiMeid:String)
    
  case class MobilesName(
    mobileName:String,
    id:Option[Int]=None
    )
    
  case class MobileModels(
    mobileModel:String,
    id: Option[Int] = None)
    
    
  case class MobileRegisterForm(
      userName:String,
      mobileName:String,
      mobileModel:String,
      imeiMeid:String,
      purchaseDate:java.sql.Date,
      contactNo:String,
      email:String,
      regType:String,
      description:String)
      
   case class MobilesNameForm(
    mobileName:String
    )
  

  object Mobiles extends Table[Mobile]("mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileName: Column[String] = column[String]("mobile_name", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileModel: Column[String] = column[String]("mobile_model", O.NotNull, O DBType ("VARCHAR(100)"))
    def imeiMeid: Column[String] = column[String]("imei_meid", O.NotNull, O DBType ("VARCHAR(100)"))
    def purchaseDate: Column[java.sql.Date] = column[java.sql.Date]("purchase_date", O.NotNull)

    def contactNo: Column[String] = column[String]("contact_no", O.NotNull, O DBType ("VARCHAR(100)"))
    def email: Column[String] = column[String]("email", O.NotNull, O DBType ("VARCHAR(100)"))
    def regType: Column[String] = column[String]("type", O.NotNull, O DBType ("VARCHAR(20)"))
    def mobileStatus: Column[Status.Value] = column[Status.Value]("status", O.NotNull, O DBType ("VARCHAR(50)"))
    def description: Column[String] = column[String]("description", O.NotNull, O DBType ("VARCHAR(500)"))
    
    def * : scala.slick.lifted.MappedProjection[Mobile, (String, String, String, String, java.sql.Date, String, String, String, Status.Value, String, Option[Int])] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ regType ~ mobileStatus ~ description ~ id <> (Mobile, Mobile unapply _)
    
    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Mobile, Option[Int]] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~
      email ~ regType ~ mobileStatus ~ description<> (
        { (username, mobileName, mobileModel,imeiMeid, purchaseDate, contactNo, email, regType, mobileStatus, description) =>
          Mobile(username, mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , regType, mobileStatus, description)
        },
        { mobileregistration: Mobile =>
          Some((mobileregistration.userName,mobileregistration.mobileName, mobileregistration.mobileModel,mobileregistration.imeiMeid,
              mobileregistration.purchaseDate, mobileregistration.contactNo ,  mobileregistration.email , mobileregistration.regType, mobileregistration.mobileStatus, mobileregistration.description))
        }) returning id
  }

  object MobileName extends Table[MobilesName]("mobilesname") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def mobileName: Column[String] = column[String]("name", O.NotNull, O DBType ("VARCHAR(30)"))
    
     def * : scala.slick.lifted.MappedProjection[MobilesName, (String, Option[Int])] =
      mobileName ~ id <> (MobilesName, MobilesName unapply _)

  }
  
  object MobileModel extends Table[MobileModels]("mobilesmodel") {
    def mobilesnameid: Column[Option[Int]] = column[Option[Int]]("mobilesnameid", O.NotNull)
    def model: Column[String] = column[String]("model", O.NotNull, O DBType ("VARCHAR(30)"))

    def * : scala.slick.lifted.MappedProjection[MobileModels, (String, Option[Int])] =
      model ~ mobilesnameid <> (MobileModels, MobileModels unapply _)

    def mobilenameFkey: ForeignKeyQuery[MobileName.type, MobilesName] = foreignKey("mobilemodal_mobilename_fkey", mobilesnameid, MobileName)(_.id.get)
  }
  
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

}