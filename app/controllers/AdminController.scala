package controllers

import model.users.MobileServiceComponent
import play.api.mvc._
import play.api.Logger
import utils.Common
import model.domains.Domain._
import utils.TwitterTweet
import model.users.MobileService

class AdminController(mobileService: MobileServiceComponent) extends Controller{
  
  def approve(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AuthController:approve - change status to approve : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AuthController:mobileUser - change status to approve : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.userName, mobileUser.mobileName, mobileUser.mobileModel, mobileUser.imeiMeid, mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email, mobileUser.regType, model.domains.Domain.Status.approved, mobileUser.description, mobileUser.regDate, mobileUser.document, mobileUser.id)
    val isExist = mobileService.changeStatusToApprove(updatedMobile)
    if (isExist) {
      Logger.info("AuthController: - true")
      if(mobileUser.regType == "stolen"){
      TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
      }else{
      TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
      }
      Ok("success")
    } else {
      Logger.info("AuthController: - false")
      Ok("error")
    }
  }
  
  def proofDemanded(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AuthController:proofDemanded - change status to proofDemanded : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AuthController:mobileUser - change status to proofDemanded : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.userName, mobileUser.mobileName, mobileUser.mobileModel, mobileUser.imeiMeid, mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email, mobileUser.regType, model.domains.Domain.Status.proofdemanded, mobileUser.description, mobileUser.regDate, mobileUser.document, mobileUser.id)
    val isExist = mobileService.changeStatusToDemandProof(updatedMobile)
    if (isExist) {
      Logger.info("AuthController: - true")
      Ok("success")
    } else {
      Logger.info("AuthController: - false")
      Ok("error")
    }
  }

  def sendMailForDemandProof(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AuthController:sendMailForDemandProof - sendMailForDemandProof : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AuthController:mobileUser - change status to proofDemanded : " + mobileUser)
    try {
      Common.sendMail(mobileUser.imeiMeid + " <" + mobileUser.email + ">",
        "Document proof request from MCWS", Common.demandProofMessage(mobileUser.imeiMeid))
      Logger.info("AuthController: - true")
      Ok("success")
    } catch {
      case e: Exception =>
        Logger.info("" + e.printStackTrace())
        Logger.info("AuthController: - false")
        Ok("error")
    }

  }
}
object AdminController extends AdminController(MobileService)