package model.dals

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

import model.domains.Domain._
import play.api.Logger
import utils.Connection

trait MobileDALComponent {
    def insertMobileUser(mobileuser: Mobile): Either[String, Option[Int]]
    def getMobileRecordByIMEID(imeid: String): List[Mobile]
    def getMobilesName: List[Brand]
    def getMobileModelsById(id: Int): List[MobileModels]
    def insertMobileName(brand: Brand): Either[String, Option[Int]]
    def getMobileNamesById(id: Int): List[Brand]
    def insertMobileModel(mobilemodel: MobileModels): Either[String, Int]
    def getAllMobiles: List[Mobile]
}

class MobileDAL extends MobileDALComponent {

  override def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(Mobiles.insert.insert(mobile))
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

  override def getMobilesName(): List[Brand] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobilesName")
       (for { brand <- Brands } yield brand).list
      }
    }

  override def getMobileModelsById(id: Int): List[MobileModels] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobileRecordByIMEID" +id)
       (for { mobilemodel <- MobileModel if (mobilemodel.mobilesnameid === id) } yield mobilemodel).list
      }
    }

  override def insertMobileName(brand: Brand): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(Brands.insert.insert(brand))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert mobile name" + ex.printStackTrace())
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

  override def getMobileNamesById(mid: Int): List[Brand] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getMobileNameById" +mid)
       (for { brand <- Brands if (brand.id === mid) } yield brand).list
      }
    }

   override def getAllMobiles : List[Mobile] = {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Calling getUserRecord")
       (for { mobile <- Mobiles } yield mobile).list
      }
    }

}

object MobileDAL extends MobileDAL
