package controllers

import play.api.mvc._
import play.api.Logger
import utils.Common
import utils.TwitterTweet
import play.api.cache.Cache
import play.api.Play.current
import views.html
import play.api.libs.json.Json
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import play.api.libs.json._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import play.api.data.Form
import play.api.data.Forms._
import play.mvc.Results.Redirect
import model.repository.MobileStatus
import model.repository.User
import model.repository.MobileRepository
import model.repository.Mobile
class AdminController extends Controller with Secured {

  implicit val formats = DefaultFormats
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  def mobiles(status: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles method has been called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles = MobileRepository.getAllMobilesUserWithBrandAndModel(status)
      Logger.info("mobiles Admin Controller::::" + mobiles)
      Ok(html.admin.mobiles(mobiles, user))
  }

  def approve(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:approve - change status to approve : " + imeiId)

    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    Logger.info("AdminController:mobileUser - change status to approve : " + mobileUser.get)
    val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.mobileModelId,
      mobileUser.get.imeiMeid, mobileUser.get.otherImeiMeid, mobileUser.get.purchaseDate, mobileUser.get.contactNo, mobileUser.get.email,
      mobileUser.get.regType, utils.StatusUtil.Status.approved, mobileUser.get.description,
      mobileUser.get.regDate, mobileUser.get.document, mobileUser.get.otherMobileBrand, mobileUser.get.otherMobileModel,
      mobileUser.get.id)
    val result = MobileRepository.changeStatusToApproveByIMEID(updatedMobile)
    result match {
      case Right(id) =>
        Logger.info("AdminController: - true")
        if (mobileUser.get.regType == "stolen") {
          //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
        } else {
          //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
        }
        Ok("success")
      case Left(message) =>
        Logger.info("AdminController: - false")
        Ok("error")
    }
  }

  def proofDemanded(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:proofDemanded - change status to proofDemanded : " + imeiId)
    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    Logger.info("AdminController:mobileUser - change status to proofDemanded : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.mobileModelId, mobileUser.get.imeiMeid,
      mobileUser.get.otherImeiMeid, mobileUser.get.purchaseDate, mobileUser.get.contactNo, mobileUser.get.email, mobileUser.get.regType,
      utils.StatusUtil.Status.proofdemanded, mobileUser.get.description, mobileUser.get.regDate, mobileUser.get.document,
      mobileUser.get.otherMobileBrand, mobileUser.get.otherMobileModel, mobileUser.get.id)
    val result = MobileRepository.changeStatusToDemandProofByIMEID(updatedMobile)
    result match {
      case Right(id) =>
        Logger.info("AdminController: - true")
        Ok("success")
      case Left(message) =>
        Logger.info("AdminController: - false")
        Ok("error")
    }
  }

  def pending(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:pending - change status to pending : " + imeiId)
    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    Logger.info("AdminController:mobileUser - change status to pending : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.mobileModelId, mobileUser.get.imeiMeid,
      mobileUser.get.otherImeiMeid, mobileUser.get.purchaseDate, mobileUser.get.contactNo, mobileUser.get.email, mobileUser.get.regType,
      utils.StatusUtil.Status.pending, mobileUser.get.description, mobileUser.get.regDate, mobileUser.get.document, mobileUser.get.otherMobileBrand,
      mobileUser.get.otherMobileModel, mobileUser.get.id)
    val result = MobileRepository.changeStatusToPendingByIMEID(updatedMobile)
    result match {
      case Right(id) =>
        Logger.info("AdminController: - true")
        Ok("success")
      case Left(message) =>
        Logger.info("AdminController: - false")
        Ok("error")
    }
  }

  def sendMailForApprovedRequest(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:The request been approved of " + imeiId)
    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    try {
      Common.sendMail(mobileUser.get.imeiMeid + "<" + mobileUser.get.email + ">",
        "Request Approved On MCWS", Common.approvedMessage(mobileUser.get.imeiMeid))
      Logger.info("AuthController:-true")
      Ok("success")
    } catch {
      case e: Exception =>
        Logger.info("" + e.printStackTrace())
        Logger.info("AuthController: - false")
        Ok("error")
    }
  }

  def sendMailForDemandProof(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:sendMailForDemandProof - sendMailForDemandProof : " + imeiId)
    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    Logger.info("AdminController:mobileUser - change status to proofDemanded : " + mobileUser)
    try {
      Common.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
        "Document Proof Request from MCWS", Common.demandProofMessage(mobileUser.get.imeiMeid))
      Logger.info("AuthController: - true")
      Ok("success")
    } catch {
      case e: Exception =>
        Logger.info("" + e.printStackTrace())
        Logger.info("AuthController: - false")
        Ok("error")
    }
  }

  def changeMobileRegTypeForm: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Ok(html.admin.changeMobileRegType(mobilestatus, user))
  }

  def changeMobileRegType(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:changeMobileRegType - change Registration type : " + imeiId)
    val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
    Logger.info("AdminController:changeMobileRegType - change Registration type: " + mobileUser)
    val regType = if (mobileUser.get.regType == "stolen") {
      "Clean"
    } else {
      "stolen"
    }
    val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.mobileModelId,
      mobileUser.get.imeiMeid, mobileUser.get.otherImeiMeid, mobileUser.get.purchaseDate, mobileUser.get.contactNo, mobileUser.get.email,
      regType, mobileUser.get.mobileStatus, mobileUser.get.description,
      mobileUser.get.regDate, mobileUser.get.document, mobileUser.get.otherMobileBrand, mobileUser.get.otherMobileModel,
      mobileUser.get.id)
    val result = MobileRepository.changeRegTypeByIMEID(updatedMobile)
    result match {
      case Right(id) =>
        Logger.info("AdminController changeMobileRegType : - true")
        Ok("success")
      case Left(message) =>
        Logger.info("AdminController changeMobileRegType : - false")
        Ok("error")
    }
  }

  def deleteMobile(imeid: String): EssentialAction = withAuth { username =>
    implicit request =>
      try {
        Logger.info("AdminController:deleteMobile: " + imeid)
        val mobileUser = MobileRepository.getMobileUserByIMEID(imeid)
        Common.sendMail(mobileUser.get.imeiMeid + "<" + mobileUser.get.email + ">",
          "Delete mobile registration from MCWS", Common.deleteUserMessage(mobileUser.get.imeiMeid))
        MobileRepository.deleteMobileUser(imeid)
        Ok("Delete ajax call")
      } catch {
        case e: Exception =>
          Logger.info("" + e.printStackTrace())
          Logger.info("AuthController: - false")
          Ok("error")
      }
  }
}
object AdminController extends AdminController
