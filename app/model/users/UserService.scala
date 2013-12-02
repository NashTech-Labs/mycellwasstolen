package model.users
import model.domains.Domain._
import model.dals.UserDal
import play.api.Logger

trait UserServiceComponent{
  def mobileRegistration(mobileuser: Mobile): Either[String, Mobile]
  def getMobileRecordByIMEID(imeid: String): Option[Mobile]
}

class UserService extends UserServiceComponent{
  
  override def mobileRegistration(mobileuser: Mobile): Either[String, Mobile] = {
    UserDal.insertMobileUser(mobileuser) match {
      case Right(id) => Right(Mobile(mobileuser.userName, mobileuser.mobileName,
         mobileuser.mobileModel,mobileuser.imeiMeid,mobileuser.purchaseDate,mobileuser.contactNo,
         mobileuser.email,mobileuser.description))
      case Left(error) => Left(error)
    }
  }
  
  override def getMobileRecordByIMEID(imeid: String): Option[Mobile] = {
    Logger.info("getMobileRecordByIMEID called")
    val mobileData = UserDal.getMobileRecordByIMEID(imeid)
    if (mobileData.length != 0) Some(mobileData.head) else None
  }

}

object UserService extends UserService