package model.users

import model.dals._
import model.domains.Domain._
import play.api.Logger

trait MobileServiceComponent {
  def mobileRegistration(mobileuser: Mobile): Either[String, Mobile]
  def getMobileRecordByIMEID(imeid: String): Option[Mobile]
  def getMobilesName(): List[Brand]
  def getMobileModelsById(id: Int): List[MobileModels]
  def isImeiExist(imeid: String): Boolean
  def addMobileName(brand: Brand): Either[String, Option[Int]]
  def getMobileNamesById(id: Int): Option[Brand]
  def createMobileModel(mobilemodel: MobileModels): Either[String, MobileModels]
  def changeStatusToApprove(mobileUser: Mobile): Boolean
  def changeStatusToDemandProof(mobileUser: Mobile): Boolean
  def getMobileModelById(id: Int): Option[MobileModels]
  def changeRegTypeByIMEID(mobileUser: Mobile): Boolean
  def getAllMobilesWithBrandAndModel(status: String): List[(Mobile, String, String)]
}

class MobileService(mobiledal: MobileDALComponent) extends MobileServiceComponent {

  override def mobileRegistration(mobileuser: Mobile): Either[String, Mobile] = {
    mobiledal.insertMobileUser(mobileuser) match {
      case Right(id) => Right(Mobile(mobileuser.userName, mobileuser.brandId,
        mobileuser.mobileModelId, mobileuser.imeiMeid, mobileuser.otherImeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
        mobileuser.email, mobileuser.regType, mobileuser.mobileStatus,
        mobileuser.description, mobileuser.regDate, mobileuser.document, mobileuser.otherMobileBrand,
        mobileuser.otherMobileModel))
      case Left(error) => Left(error)
    }
  }

  override def getMobileRecordByIMEID(imeid: String): Option[Mobile] = {
    Logger.info("getMobileRecordByIMEID called")
    val mobileData = mobiledal.getMobileRecordByIMEID(imeid)
    if (mobileData.length != 0) Some(mobileData.head) else None
  }

  override def getMobilesName(): List[Brand] = {
    Logger.info("getMobilesName called")
    mobiledal.getMobilesName
  }

  override def getMobileModelsById(id: Int): List[MobileModels] = {
    Logger.info("getMobileRecordByIMEID called")
    mobiledal.getMobileModelsById(id)
  }

  override def getMobileNamesById(id: Int): Option[Brand] = {
    Logger.info("getMobileNamesById called")
    val mobileName = mobiledal.getMobileNamesById(id)
    //if (mobileName.length != 0) Some(mobileName.head) else None
    mobileName.headOption
  }

  override def isImeiExist(imeid: String): Boolean = {
    val mobile = mobiledal.getMobileRecordByIMEID(imeid)
    if (mobile.length != 0) true else false
  }

  override def addMobileName(brand: Brand): Either[String, Option[Int]] = {
    mobiledal.insertMobileName(brand)
  }

  override def createMobileModel(mobilemodel: MobileModels): Either[String, MobileModels] = {
    mobiledal.insertMobileModel(mobilemodel) match {
      case Right(id) => Right(MobileModels(mobilemodel.mobileModel, mobilemodel.mobileName))
      case Left(error) => Left(error)
    }
  }

  override def getAllMobilesWithBrandAndModel(status: String): List[(Mobile, String, String)] = {
    Logger.info("getAllMobiles called")
    mobiledal.getAllMobilesWithBrandAndModel(status)

  }

  override def changeStatusToApprove(mobileUser: Mobile): Boolean = {
    mobiledal.changeStatusToApproveByIMEID(mobileUser) match {
      case Right(id) => true
      case Left(error) => false
    }
  }

  override def changeStatusToDemandProof(mobileUser: Mobile): Boolean = {
    mobiledal.changeStatusToDemandProofByIMEID(mobileUser) match {
      case Right(id) => true
      case Left(error) => false
    }
  }
  override def getMobileModelById(id: Int): Option[MobileModels] = {
    Logger.info("getMobileModelById called")
    val mobileModel = mobiledal.getMobileModelById(id)
    //if (mobileName.length != 0) Some(mobileName.head) else None
    mobileModel.headOption
  }

  override def changeRegTypeByIMEID(mobileUser: Mobile): Boolean = {
    mobiledal.changeRegTypeByIMEID(mobileUser) match {
      case Right(id) => true
      case Left(error) => false
    }
  }

}

object MobileService extends MobileService(MobileDAL)
