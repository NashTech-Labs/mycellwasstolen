package controllers

import model.users.MobileServiceComponent
import play.api.mvc._
import play.api.Logger
import utils.Common
import model.domains.Domain._
import utils.TwitterTweet
import model.users.MobileService
import play.api.cache.Cache
import play.api.Play.current
import views.html
import play.api.libs.json.Json
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import play.api.libs.json.JsValue
import net.liftweb.json.JObject
import net.liftweb.json.JString
import net.liftweb.json.JObject
import net.liftweb.json.JString
import net.liftweb.json.JField
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import play.api.data.Form
import play.api.data.Forms._

class AdminController(mobileService: MobileServiceComponent) extends Controller with Secured {

  implicit val formats = DefaultFormats
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  def mobiles(status: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles method has been called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles: List[Mobile] = mobileService.getAllMobiles(status)
      mobileNameWithMobile(mobiles)
      if (mobiles.isEmpty) {
        Ok(html.admin.mobiles(mobileNameWithMobile(mobiles), user))
      } else {
        Logger.info("AdminController mobile list: - true")
        Ok(html.admin.mobiles(mobileNameWithMobile(mobiles), user))

      }

  }

 private def mobileNameWithMobile(mobiles: List[Mobile]) = {
    mobiles.map {
      mobile =>
        (mobile, mobileService.getMobileNamesById(mobile.mobileName.toInt).get.name, mobileService.getMobileModelById(mobile.mobileModel.toInt).get.mobileModel)

    }

  }

  def mobilesForAjaxCall(status: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles method has been called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles: List[Mobile] = mobileService.getAllMobiles(status)
      Logger.info("Mobiles Record" + mobiles)
      if (!mobiles.isEmpty) {
        Ok(write(mobileNameWithMobile(mobiles))).as("application/json")
      } else {
        Logger.info("AuthController mobile list: - true")
        Ok(Json.obj("status" -> "Error"))
      }

  }

  def approve(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:approve - change status to approve : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AdminController:mobileUser - change status to approve : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.userName, mobileUser.mobileName, mobileUser.mobileModel,
      mobileUser.imeiMeid, mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email,
      mobileUser.regType, model.domains.Domain.Status.approved, mobileUser.description,
      mobileUser.regDate, mobileUser.document, mobileUser.otherMobileBrand, mobileUser.otherMobileModel,
      mobileUser.id)
    val isExist = mobileService.changeStatusToApprove(updatedMobile)
    if (isExist) {
      Logger.info("AdminController: - true")
      if (mobileUser.regType == "stolen") {
        TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
      } else {
        TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
      }
      Ok("success")
    } else {
      Logger.info("AdminController: - false")
      Ok("error")
    }
  }

  def proofDemanded(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:proofDemanded - change status to proofDemanded : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AdminController:mobileUser - change status to proofDemanded : " + mobileUser)
    val updatedMobile = Mobile(mobileUser.userName, mobileUser.mobileName, mobileUser.mobileModel, mobileUser.imeiMeid, mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email, mobileUser.regType, model.domains.Domain.Status.proofdemanded, mobileUser.description, mobileUser.regDate, mobileUser.document, mobileUser.otherMobileBrand, mobileUser.otherMobileModel, mobileUser.id)
    val isExist = mobileService.changeStatusToDemandProof(updatedMobile)
    if (isExist) {
      Logger.info("AdminController: - true")
      Ok("success")
    } else {
      Logger.info("AdminController: - false")
      Ok("error")
    }
  }

  def sendMailForDemandProof(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:sendMailForDemandProof - sendMailForDemandProof : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AdminController:mobileUser - change status to proofDemanded : " + mobileUser)
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

  def changeMobileRegTypeForm: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Ok(html.admin.changeMobileRegType(mobilestatus, user))
  }

  def changeMobileRegType(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:changeMobileRegType - change Registration type : " + imeiId)

    val mobileUser = mobileService.getMobileRecordByIMEID(imeiId).get
    Logger.info("AdminController changeMobileRegType - change Registration type: " + mobileUser)

    val regType = if (mobileUser.regType == "stolen") "Clean"

    else "stolen"

    val updatedMobile = Mobile(mobileUser.userName, mobileUser.mobileName, mobileUser.mobileModel,
      mobileUser.imeiMeid, mobileUser.purchaseDate, mobileUser.contactNo, mobileUser.email,
      regType, model.domains.Domain.Status.approved, mobileUser.description,
      mobileUser.regDate, mobileUser.document, mobileUser.otherMobileBrand, mobileUser.otherMobileModel,
      mobileUser.id)

    val isExist = mobileService.changeRegTypeByIMEID(updatedMobile)
    if (isExist) {
      Logger.info("AdminController changeMobileRegType : - true")

      Ok("success")

    } else {
      Logger.info("AdminController changeMobileRegType : - false")
      Ok("error")
    }
  }
}
object AdminController extends AdminController(MobileService)
