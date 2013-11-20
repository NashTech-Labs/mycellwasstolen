package model.domains

import scala.slick.driver.PostgresDriver.simple._

object Domain {

  /**
   *  Question  object and mapping with question table in database
   *
   */
  case class MobileRegistrationForm(
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

  object MobileRegistrationPool extends Table[MobileRegistrationForm]("mobileregistrationpool") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def username: Column[String] = column[String]("username", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileName: Column[String] = column[String]("mobileName", O.NotNull, O DBType ("VARCHAR(100)"))
    def mobileModel: Column[String] = column[String]("mobileModel", O.NotNull, O DBType ("VARCHAR(100)"))
    def imeiMeid: Column[String] = column[String]("imeiMeid", O.NotNull, O DBType ("VARCHAR(100)"))
    def purchaseDate: Column[java.sql.Date] = column[java.sql.Date]("purchasedate", O.NotNull)
    def contactNo: Column[Int] = column[Int]("contactno", O.NotNull)
    def email: Column[String] = column[String]("email", O.NotNull, O DBType ("VARCHAR(100)"))
    def description: Column[String] = column[String]("description", O.NotNull, O DBType ("VARCHAR(100)"))
    
    //def * : scala.slick.lifted.MappedProjection[MobileRegistration,(String,String,String,String,java.sql.Date,Int,String,String, Option[Int])]= username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ description ~ id <> (MobileRegistration.apply _, MobileRegistration.unapply _)
    
    def * : scala.slick.lifted.MappedProjection[MobileRegistrationForm, (String, String, String, String, java.sql.Date, Int, String, String, Option[Int])] =
      username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~ email ~ description ~ id <> (MobileRegistrationForm, MobileRegistrationForm unapply _)
    
    def insert: slick.driver.PostgresDriver.KeysInsertInvoker[MobileRegistrationForm, Option[Int]] =
      username ~ mobileName ~ mobileModel ~ imeiMeid ~ purchaseDate ~ contactNo ~
      email ~ description <> (
        { (username,mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , description) =>
          MobileRegistrationForm(username,mobileName, mobileModel,imeiMeid, purchaseDate, contactNo ,  email , description)
        },
        { mobileregistration: MobileRegistrationForm =>
          Some((mobileregistration.username,mobileregistration.mobileName, mobileregistration.mobileModel,mobileregistration.imeiMeid, mobileregistration.purchaseDate, mobileregistration.contactNo ,  mobileregistration.email , mobileregistration.description))
        }) returning id
  }
}