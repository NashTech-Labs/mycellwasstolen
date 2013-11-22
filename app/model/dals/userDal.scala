package model.dals
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

import model.domains.domain._
import play.api.Logger
import utils.Connection

trait UserDalComponent {
def insertMobileUser(mobileuser: MobileRegister): Either[String, Option[Int]]
}


object userDal extends UserDalComponent {

  override def insertMobileUser(mobileuser: MobileRegister): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Right(MobileRegistrationTable.insert.insert(mobileuser))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }

  }

}