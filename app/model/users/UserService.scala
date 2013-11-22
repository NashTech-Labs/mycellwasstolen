package model.users
import model.domains.domain._
import model.dals.userDal

trait UserServiceComponent{
  def mobileRegistration(mobileuser: MobileRegister): Either[String, MobileRegister]
}

object UserService extends UserServiceComponent{
  
  override def mobileRegistration(mobileuser: MobileRegister): Either[String, MobileRegister] = {
    userDal.insertMobileUser(mobileuser) match {
      case Right(id) => Right(MobileRegister(mobileuser.username, mobileuser.mobileName,
         mobileuser.mobileModel,mobileuser.imeiMeid,mobileuser.purchaseDate,mobileuser.contactNo,
         mobileuser.email,mobileuser.description))
      case Left(error) => Left(error)
    }
  }

}