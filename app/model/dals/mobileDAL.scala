package model.dals

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session
import model.domains.Domain._
import play.api.Logger
import utils.Connection
import play.libs.F.Tuple

trait MobileDALComponent {
  def insertMobileUser(mobileuser: Mobile): Either[String, Option[Int]]
  def getMobileRecordByIMEID(imeid: String): List[Mobile]
  def getMobilesName: List[Brand]
  def getMobileModelsById(id: Int): List[MobileModels]
  def insertMobileName(brand: Brand): Either[String, Option[Int]]
  def getMobileNamesById(id: Int): List[Brand]
  def insertMobileModel(mobilemodel: MobileModels): Either[String, Option[Int]]
  def getAllMobiles(status: String): List[Mobile]
  def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int]
  def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int]
  def getMobileModelById(mid: Int): List[MobileModels]
  def changeRegTypeByIMEID(mobileUser: Mobile): Either[String, Int]
  //override def getAllMobilesJoinWithBrand(mobiles: List[Mobile]): List[(Mobile,Brand,MobileModels)]
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

  override def getAllMobiles(status: String): List[Mobile] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getUserRecord")
      if (status.equals("pending")) {
        (for { mobile <- Mobiles if (mobile.mobileStatus === model.domains.Domain.Status.pending) } yield mobile).sortBy(_.id).list
      } else if (status.equals("proofdemanded")) {
        (for { mobile <- Mobiles if (mobile.mobileStatus === model.domains.Domain.Status.proofdemanded) } yield mobile).sortBy(_.id).list
      } else (for { mobile <- Mobiles if (mobile.mobileStatus === model.domains.Domain.Status.approved) } yield mobile).sortBy(_.id).list
    }
  }
 /* // for testing a concept
  override def getAllMobilesJoinWithBrand(mobiles: List[Mobile]): List[(Mobile,Brand,MobileModels)] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getUserRecord")
      
        (for { mobile <- mobiles
          brand <- Brands 
          model <- MobileModel 
          if(mobile.mobileName.equals(brand.id.toString()) && mobile.mobileModel.equals(model.id.toString())) } yield mobile)
    }
  }*/
  
  override def changeStatusToApproveByIMEID(mobileUser: Mobile): Either[String, Int] = {
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

  override def changeStatusToDemandProofByIMEID(mobileUser: Mobile): Either[String, Int] = {
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

}

object MobileDAL extends MobileDAL
