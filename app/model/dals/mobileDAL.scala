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
  def insertMobileModel(mobilemodel: MobileModels): Either[String, Option[Int]]
  def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int]
  def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int]
  def getMobileModelById(mid: Int): List[MobileModels]
  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int]
  def getAllMobilesWithBrandAndModel(status: String): List[(Mobile, String, String)]
  def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int]
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
      Logger.info("Calling getMobileRecordByIMEID" + imeid)
      (for { mobile <- Mobiles if ((mobile.imeiMeid === imeid) || (mobile.otherImeiMeid === imeid)) } yield mobile).list
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
      Logger.info("Calling getMobileRecordByIMEID" + id)
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

  override def insertMobileModel(mobilemodel: MobileModels): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(MobileModel.insert.insert(mobilemodel))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  override def getMobileNamesById(mid: Int): List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + mid)
      (for { brand <- Brands if (brand.id === mid) } yield brand).list
    }
  }

  def getAllMobilesWithBrandAndModel(status: String): List[(Mobile, String, String)] = {
    Connection.databaseObject withSession { implicit session: Session =>

      (for {
        mobile <- Mobiles if (mobile.mobileStatus === Status.withName(status))
        brand <- Brands if (brand.id === mobile.brandId)
        mobileModel <- MobileModel if (mobileModel.id === mobile.mobileModelId)

      } yield (mobile, brand.name, mobileModel.model)).sortBy(_._1.id) list

    }
  }

  override def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = Mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  override def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = Mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  override def getMobileModelById(mid: Int): List[MobileModels] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + mid)
      (for { model <- MobileModel if (model.id === mid) } yield model).list
    }
  }

  override def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = Mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          // (for { mobile <- Mobiles if (mobile.imeiMeid===mobileUser.imeiMeid) } yield (mobile))
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }
  
  override def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = Mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

}

object MobileDAL extends MobileDAL
