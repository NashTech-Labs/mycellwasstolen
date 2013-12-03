package model.users
import model.domains.Domain._
import model.dals.UserDal
import play.api.Logger

trait UserServiceComponent{
  def mobileRegistration(mobileuser: Mobile): Either[String, Mobile]
  def getMobileRecordByIMEID(imeid: String): List[Mobile]
  def getMobilesName(): List[MobilesName]
  def getMobileModelsById(id: Int): List[MobileModels]
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
  
  override def getMobileRecordByIMEID(imeid: String): List[Mobile] = {
    Logger.info("getMobileRecordByIMEID called")
    UserDal.getMobileRecordByIMEID(imeid)
  }
  override def getMobilesName(): List[MobilesName] = {
    Logger.info("getMobilesName called")
    UserDal.getMobilesName()
  }
  
  override def getMobileModelsById(id: Int): List[MobileModels] = {
    Logger.info("getMobileModelsById called")
    UserDal.getMobileModelsById(id)
  }

}

object UserService extends UserService