package model.dals

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

import model.domains.Domain._
import play.api.Logger
import utils.Connection

trait UserDalComponent {
def insertMobileUser(mobileuser: Mobile): Either[String, Option[Int]]
def getMobileRecordByIMEID(imeid: String): List[Mobile]
}


class UserDal extends UserDalComponent {

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
}

object UserDal extends UserDal