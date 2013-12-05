package model.dals

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

import model.domains.Domain._
import play.api.Logger
import utils.Connection

trait MobileDALComponent {
	def insertMobileUser(mobileuser: Mobile): Either[String, Option[Int]]
	def getMobileRecordByIMEID(imeid: String): List[Mobile]
	def getMobilesName(): List[MobilesName]
	def getMobileModelsById(id: Int): List[MobileModels]
	def insertMobileName(mobilename: MobilesName): Either[String, Int]
	def getMobileNamesById(id: Int): List[MobilesName]
	def insertMobileModel(mobilemodel: MobileModels): Either[String, Int]
}

class MobileDAL extends MobileDALComponent {

  override def insertMobileUser(mobileuser: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(Mobiles.insert.insert(mobileuser))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }
  
  override def getMobileRecordByIMEID(imeid: String): List[Mobile] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobileRecordByIMEID" +imeid)
       (for { mobile <- Mobiles if (mobile.imeiMeid === imeid) } yield mobile).list
      }
    }
  
  override def getMobilesName(): List[MobilesName] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobilesName")
       (for { mobilename <- MobileName } yield mobilename).list
      }
      
      
    }
  
  override def getMobileModelsById(id: Int): List[MobileModels] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobileRecordByIMEID" +id)
       (for { mobilemodel <- MobileModel if (mobilemodel.mobilesnameid === id) } yield mobilemodel).list
      }
    }
  
  override def insertMobileName(mobilename: MobilesName): Either[String, Int] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(MobileName.insert(mobilename))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }
  
  
  override def insertMobileModel(mobilemodel: MobileModels): Either[String, Int] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(MobileModel.insert(mobilemodel))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }
  
  override def getMobileNamesById(mid: Int): List[MobilesName] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobileNameById" +mid)
       (for { mobilename <- MobileName if (mobilename.id === mid) } yield mobilename).list
      }
    }
  
}

object MobileDAL extends MobileDAL
