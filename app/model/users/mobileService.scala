package model.users

import model.dals._
import model.domains.Domain._
import play.api.Logger

trait MobileServiceComponent{
  def mobileRegistration(mobileuser: Mobile): Either[String, Mobile]
  def getMobileRecordByIMEID(imeid: String): Option[Mobile]
  def getMobilesName(): List[MobilesName]
  def getMobileModelsById(id: Int): List[MobileModels]
  def isImeiExist(imeid: String): Boolean
  def addMobileName(mobilename: MobilesName): Either[String, MobilesName]
  def getMobileNamesById(id: Int): Option[MobilesName]
  def createMobileModel(mobilemodel: MobileModels): Either[String, MobileModels]
}

class MobileService(mobiledal: MobileDALComponent) extends MobileServiceComponent{
  
  override def mobileRegistration(mobileuser: Mobile): Either[String, Mobile] = {
    mobiledal.insertMobileUser(mobileuser) match {
      case Right(id) => Right(Mobile(mobileuser.userName, mobileuser.mobileName,
         mobileuser.mobileModel,mobileuser.imeiMeid,mobileuser.purchaseDate,mobileuser.contactNo,
         mobileuser.email, mobileuser.regType, mobileuser.mobileStatus, mobileuser.description))
      case Left(error) => Left(error)
    }
  }
  
  override def getMobileRecordByIMEID(imeid: String): Option[Mobile] = {
    Logger.info("getMobileRecordByIMEID called")
    val mobileData = mobiledal.getMobileRecordByIMEID(imeid)
    if (mobileData.length != 0) Some(mobileData.head) else None
  }
  
  override def getMobilesName(): List[MobilesName] = {
    Logger.info("getMobilesName called")
    mobiledal.getMobilesName()
  }
  
  override def getMobileModelsById(id: Int): List[MobileModels] = {
    Logger.info("getMobileRecordByIMEID called")
    mobiledal.getMobileModelsById(id)
  }
  
  
  override def getMobileNamesById(id: Int): Option[MobilesName] = {
    Logger.info("getMobileNamesById called")
    val mobileName = mobiledal.getMobileNamesById(id)
    if (mobileName.length != 0) Some(mobileName.head) else None
  }
  
  override def isImeiExist(imeid: String): Boolean = {
    val mobile = mobiledal.getMobileRecordByIMEID(imeid)
    if (mobile.length != 0) true else false
  }

  override def addMobileName(mobilename: MobilesName): Either[String, MobilesName] = {
    mobiledal.insertMobileName(mobilename) match {
      case Right(id) => Right(MobilesName(mobilename.mobileName))
      case Left(error) => Left(error)
    }
  }
  
  override def createMobileModel(mobilemodel: MobileModels): Either[String, MobileModels] = {
    mobiledal.insertMobileModel(mobilemodel) match {
      case Right(id) => Right(MobileModels(mobilemodel.mobileModel,mobilemodel.mobileName))
      case Left(error) => Left(error)
    }
  }

}

object MobileService extends MobileService(MobileDAL)