package model.users

import model.dals._
import model.domains.Domain._
import play.api.Logger

trait UserServiceComponent{
  def mobileRegistration(mobileuser: Mobile): Either[String, Mobile]
  def getMobileRecordByIMEID(imeid: String): Option[Mobile]
  def getMobilesName(): List[MobilesName]
  def getMobileModelsById(id: Int): List[MobileModels]
}

class UserService(userdal: UserDALComponent) extends UserServiceComponent{
  
  override def mobileRegistration(mobileuser: Mobile): Either[String, Mobile] = {
    userdal.insertMobileUser(mobileuser) match {
      case Right(id) => Right(Mobile(mobileuser.userName, mobileuser.mobileName,
         mobileuser.mobileModel,mobileuser.imeiMeid,mobileuser.purchaseDate,mobileuser.contactNo,
         mobileuser.email, mobileuser.regType, mobileuser.description))
      case Left(error) => Left(error)
    }
  }
  
  override def getMobileRecordByIMEID(imeid: String): Option[Mobile] = {
    Logger.info("getMobileRecordByIMEID called")
    val mobileData = userdal.getMobileRecordByIMEID(imeid)
    if (mobileData.length != 0) Some(mobileData.head) else None
  }
  
  override def getMobilesName(): List[MobilesName] = {
    Logger.info("getMobilesName called")
    userdal.getMobilesName()
  }
  
  override def getMobileModelsById(id: Int): List[MobileModels] = {
    Logger.info("getMobileRecordByIMEID called")
    userdal.getMobileModelsById(id)
  }

}

object UserService extends UserService(UserDAL)