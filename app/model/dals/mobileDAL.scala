package model.dals

import scala.slick.driver.PostgresDriver.simple._
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
  def deleteMobile(imeid: String): Either[String, Int]
}

class MobileDAL extends MobileDALComponent {
  /**
   * insert mobile user information
   */

  override def insertMobileUser(mobile: Mobile): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyMobiles.insert(mobile))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * retrieve mobile record by IMEID no.
   */

  override def getMobileRecordByIMEID(imeid: String): List[Mobile] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileRecordByIMEID" + imeid)
      mobiles.filter(_.imeiMeid === imeid).list
    }
  }

  /**
   * retrieve mobile brands list
   */

  override def getMobilesName(): List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobilesName")
      brands.list
    }
  }

  /**
   * retrieve mobile models
   */

  override def getMobileModelsById(id: Int): List[MobileModels] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileRecordByIMEID" + id)
      mobileModel.filter(_.brandId === id).list
    }
  }

  /**
   * insert mobile brand name
   */

  override def insertMobileName(brand: Brand): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyBrands.insert(brand))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert mobile name" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * insert mobile model name
   */

  override def insertMobileModel(mobilemodel: MobileModels): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(autoKeyModels.insert(mobilemodel))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   * Getting Mobile Brands
   */

  override def getMobileNamesById(mid: Int): List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + mid)
      brands.filter(_.id === mid).list
    }
  }

  /**
   * Retrieving all brands and models
   */

  def getAllMobilesWithBrandAndModel(status: String): List[(Mobile, String, String)] = {
    Connection.databaseObject withSession { implicit session: Session =>
      
      Logger.info("Calling getAllMobilesWithBrandAndModel with " + Status.withName(status))
        (for {
          mobile <- mobiles if (mobile.mobileStatus === Status.withName(status))
          brand <- brands if (brand.id === mobile.brandId)
          mobileModel <- mobileModel if (mobileModel.id === mobile.mobileModelId)

        } yield (mobile, brand.name, mobileModel.modelName)).list
    }
  }

  /**
   * Change status to approve
   */

  override def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int] = {

    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = for {
            mobile <- mobiles if (mobile.imeiMeid === mobileUser.imeiMeid)
          } yield mobile.mobileStatus

          Logger.info("updateQuery data:" + updateQuery.updateStatement)
          updateQuery.update(mobileUser.mobileStatus)
          Right(updateQuery.update(mobileUser.mobileStatus))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  /**
   * Change status to proof demand
   */

  override def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  /**
   * Getting Brand model
   */

  override def getMobileModelById(mid: Int): List[MobileModels] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + mid)
      (for { model <- mobileModel if (model.id === mid) } yield model).list
    }
  }

  /**
   * Change registration type (Stolen or Clean)
   */

  override def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  /**
   * Change status to pending
   */

  override def changeStatusToPendingByIMEID(mobileUser: Mobile): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          val updateQuery = mobiles.filter { mobile => mobile.imeiMeid === mobileUser.imeiMeid }
          Logger.info("updateQuery data:" + updateQuery)
          Right(updateQuery.update(mobileUser))
        } catch {
          case ex: Exception =>
            Logger.info("Error in update user method: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }

  override def deleteMobile(imeid: String): Either[String, Int] = {
    Connection.databaseObject().withSession {
      implicit session: Session =>
        try {
          Logger.info("Delet mobile user:" + imeid)
          Right(mobiles.filter(_.imeiMeid === imeid).delete)
        } catch {
          case ex: Exception =>
            Logger.info("Error in delete mobile: " + ex.printStackTrace())
            Left(ex.getMessage())
        }
    }
  }
}
object MobileDAL extends MobileDAL
