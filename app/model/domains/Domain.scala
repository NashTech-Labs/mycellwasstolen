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
    contactNo:Int,
    email: String,
    regType: String,
    description: String,
    id: Option[Int] = None
    )
    
  case class MobileStatus(
    imeiMeid:String)
    
  case class MobileModels(
    mobileModel:String,
    id: Option[Int] = None)
    
    
  case class MobileRegisterForm(
      userName:String,
      mobileName:String,
      mobileModel:String,
      imeiMeid:String,
      purchaseDate:java.sql.Date,
      contactNo:Int,
      email:String,
      regType:String,
      description:String)
  
  case class MobilesName(
    mobileName:String,
    id: Option[Int] = None)

  object Mobiles extends Table[Mobile]("mobiles") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def userName: Column[String] = column[String]("username", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileName: Column[String] = column[String]("mobile_name", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileModel: Column[String] = column[String]("mobile_model", O.NotNull, O DBType ("VARCHAR(100)"))
    def imeiMeid: Column[String] = column[String]("imei_meid", O.NotNull, O DBType ("VARCHAR(100)"))
    def purchaseDate: Column[java.sql.Date] = column[java.sql.Date]("purchase_date", O.NotNull)
    def contactNo: Column[Int] = column[Int]("contact_no", O.NotNull)
    def email: Column[String] = column[String]("email", O.NotNull, O DBType ("VARCHAR(100)"))
    def regType: Column[String] = column[String]("type", O.NotNull, O DBType ("VARCHAR(20)"))
    def description: Column[String] = column[String]("description", O.NotNull, O DBType ("VARCHAR(500)"))
    
    def * : scala.slick.lifted.MappedProjection[Mobile, (String, String, String, String, java.sql.Date, Int, String, String, String, Option[Int])] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ regType ~ description ~ id <> (Mobile, Mobile unapply _)
    
    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[Mobile, Option[Int]] =
      userName ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~
      email ~ regType ~ description<> (
        { (username, mobileName, mobileModel,imeiMeid, purchaseDate, contactNo, email, regType, description) =>
          Mobile(username, mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , regType, description)
        },
        { mobileregistration: Mobile =>
          Some((mobileregistration.userName,mobileregistration.mobileName, mobileregistration.mobileModel,mobileregistration.imeiMeid,
              mobileregistration.purchaseDate, mobileregistration.contactNo ,  mobileregistration.email , mobileregistration.regType,  mobileregistration.description))
        }) returning id
  }
  
   object MobileName extends Table[MobilesName]("mobilesname") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O.NotNull, O DBType ("VARCHAR(30)"))
    
     def * : scala.slick.lifted.MappedProjection[MobilesName, (String, Option[Int])] =
      name ~ id <> (MobilesName, MobilesName unapply _)
   }
  
  object MobileModel extends Table[MobileModels]("mobilesmodel") {
    def mobilesnameid: Column[Option[Int]] = column[Option[Int]]("mobilesnameid", O.NotNull)
    def model: Column[String] = column[String]("model", O.NotNull, O DBType ("VARCHAR(30)"))

    def * : scala.slick.lifted.MappedProjection[MobileModels, (String, Option[Int])] =
      model ~ mobilesnameid <> (MobileModels, MobileModels unapply _)

    def mobilenameFkey: ForeignKeyQuery[MobileName.type, MobilesName] = foreignKey("mobilemodal_mobilename_fkey", mobilesnameid, MobileName)(_.id.get)
  }

}