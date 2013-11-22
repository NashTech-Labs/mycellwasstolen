package model.domains

import scala.slick.driver.PostgresDriver.simple._

object domain {

  /**
   *  Question  object and mapping with question table in database
   *
   */
  case class MobileRegister(
    username:String,
    mobileName:String,
    mobileModel:String,
    imeiMeid:String,
    purchaseDate: java.sql.Date,
    contactNo:Int,
    email: String,
    description: String,
    id: Option[Int] = None
    )

  object MobileRegistrationTable extends Table[MobileRegister]("mobileregistrationtable") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def username: Column[String] = column[String]("username", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileName: Column[String] = column[String]("mobile_name", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileModel: Column[String] = column[String]("mobile_model", O.NotNull, O DBType ("VARCHAR(100)"))
    def imeiMeid: Column[String] = column[String]("imei_meid", O.NotNull, O DBType ("VARCHAR(100)"))
    def purchaseDate: Column[java.sql.Date] = column[java.sql.Date]("purchase_date", O.NotNull)
    def contactNo: Column[Int] = column[Int]("contact_no", O.NotNull)
    def email: Column[String] = column[String]("email", O.NotNull, O DBType ("VARCHAR(100)"))
    def description: Column[String] = column[String]("description", O.NotNull, O DBType ("VARCHAR(100)"))
    
    //def * : scala.slick.lifted.MappedProjection[MobileRegistration,(String,String,String,String,java.sql.Date,Int,String,String, Option[Int])]= username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ description ~ id <> (MobileRegistration.apply _, MobileRegistration.unapply _)
    
    def * : scala.slick.lifted.MappedProjection[MobileRegister, (String, String, String, String, java.sql.Date, Int, String, String, Option[Int])] =
      username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ description ~ id <> (MobileRegister, MobileRegister unapply _)
    
    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[MobileRegister, Option[Int]] =
      username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~
      email ~ description<> (
        { (username,mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , description) =>
          MobileRegister(username,mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , description)
        },
        { mobileregistration: MobileRegister =>
          Some((mobileregistration.username,mobileregistration.mobileName, mobileregistration.mobileModel,mobileregistration.imeiMeid, mobileregistration.purchaseDate, mobileregistration.contactNo ,  mobileregistration.email , mobileregistration.description))
        }) returning id
  }
  
  
  
 
  case class MobileRegisterForm(
      username:String,
      mobileName:String,
      mobileModel:String,
      imeiMeid:String,
      purchaseDate:java.sql.Date,
      contactNo:Int,
      email:String,
      description:String
     //id:Option[Int]=None
      //docproof:String
      
  )
}