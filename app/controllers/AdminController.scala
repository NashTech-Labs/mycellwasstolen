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
import model.repository.AuditForm
import model.repository.Audit
import model.repository.AuditRepository

class AdminController extends Controller with Secured {

  implicit val formats = DefaultFormats
  /**
   * Describes the mobile status form
   */
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  val auditform = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(AuditForm.apply)(AuditForm.unapply))

  /**
   * @param status, mobile status(pending, approved and proofdemanded)
   * @return mobiles page with mobile user according to status
   */
  def mobiles(status: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles method has been called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles = MobileRepository.getAllMobilesUserWithBrandAndModel(status)
      Logger.info("mobiles Admin Controller::::" + mobiles)
      Ok(html.admin.mobiles(status, mobiles, user))
  }

  /**
   * changes mobile status to approved
   * @param imeiId of mobile
   */
  def approve(imeiId: String, page: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:approve - change status to approve : " + imeiId)
    val result = MobileRepository.changeStatusToApproveByIMEID(imeiId)
    result match {
      case Right(1) =>
        val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
        mobileUser match {
          case Some(mobile) =>
            if (mobileUser.get.regType == "stolen") {
              Common.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                "Registration Confirmed on MCWS", Common.approvedMessage(mobileUser.get.imeiMeid))
              //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
            } else {
              Common.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                "Registration Confirmed on MCWS", Common.approvedMessage(mobileUser.get.imeiMeid))
              //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
            }
            Redirect(routes.AdminController.mobiles(page)).flashing(
              "success" -> "Mobile has been approved successfully!")
          case None =>
            Logger.info("AdminController:approve - error in fetching record after approved")
            Redirect(routes.AdminController.mobiles(page)).flashing(
              "success" -> "Mobile has been approved successfully!")
        }
      case Left(message) =>
        Logger.info("AdminController: - false")
        Redirect(routes.AdminController.mobiles(page)).flashing(
          "error" -> "Something wrong!!")
    }
  }

  /**
   * Changes mobile status to proofdemanded
   * @param imeiId of mobile
   */
  def proofDemanded(imeiId: String, page: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:proofDemanded - change status to proofDemanded : " + imeiId)
    val result = MobileRepository.changeStatusToDemandProofByIMEID(imeiId)
    result match {
      case Right(id) =>
        val mobileUser = MobileRepository.getMobileUserByIMEID(imeiId)
        mobileUser match {
          case Some(mobile) =>
            if (mobileUser.get.regType == "stolen") {
              Common.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                "Registration Confirmed on MCWS", Common.demandProofMessage(mobileUser.get.imeiMeid))
              //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
            } else {
              Common.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                "Registration Confirmed on MCWS", Common.demandProofMessage(mobileUser.get.imeiMeid))
              //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
            }
            Redirect(routes.AdminController.mobiles(page)).flashing(
              "success" -> "Mobile has been approved successfully!")
          case None =>
            Logger.info("AdminController:approve - error in fetching record after proof demanded")
            Redirect(routes.AdminController.mobiles(page)).flashing(
              "success" -> "Mobile has been approved successfully!")
        }
      case Left(message) =>
        Logger.info("AdminController: - false")
        Redirect(routes.AdminController.mobiles(page)).flashing(
          "error" -> "Something wrong!!")
    }
  }

  /**
   * Changes mobile status to pending
   * @param imeiId of mobile
   */
  def pending(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("AdminController:pending - change status to pending : " + imeiId)
    val result = MobileRepository.changeStatusToPendingByIMEID(imeiId)
    result match {
      case Right(id) =>
        Logger.info("AdminController: - true")
        Ok("success")
      case Left(message) =>
        Logger.info("AdminController: - false")
        Ok("error")
    }
  }

  /**
   * Render change mobile status(clean or stolen) page
   */
  def changeMobileRegTypeForm: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Ok(html.admin.changeMobileRegType(mobilestatus, user))
  }

  /**
   * Changes mobile status to clean or stolen
   * @param imeiId of mobile
   */
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

  /**
   * Deletes existed mobile
   * @param imeid of mobile
   */
  def deleteMobile(imeid: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:deleteMobile: " + imeid)
      val mobileUser = MobileRepository.getMobileUserByIMEID(imeid)
      val result = MobileRepository.deleteMobileUser(imeid)
      result match {
        case Right(1) =>
          mobileUser match {
            case Some(mobile) =>
              Common.sendMail(mobileUser.get.imeiMeid + "<" + mobileUser.get.email + ">",
                "Delete mobile registration from MCWS", Common.deleteMessage(mobileUser.get.imeiMeid))
              Ok("Success of Delete ajax call")
            case None =>
              Logger.info("AdminController:deleteMobile - error in fetching record")
              Ok("Success of Delete ajax call")
          }
        case Left(message) =>
          Logger.info("AdminController:deleteMobile" + message)
          Ok("error in Delete ajax call")
      }
  }

  def auditPage: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = List()
      Ok(html.admin.audit("imeid", list, user))
  }

  def audit: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val audit = auditform.bindFromRequest()
      audit.fold(
        hasErrors = { form =>
          val list = List()
          Ok(html.admin.audit("imeid", list, user)).flashing("error" -> "Please correct the errors in the form")
        },
        success = { audit =>
          val list = AuditRepository.getAllTimestampsByIMEID(audit.imeiMeid)
          Ok(html.admin.audit("imeid", list, user))
        })
  }

  def auditAllRecords: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = AuditRepository.getAllTimestamps
      Ok(html.admin.audit("all", list, user))

  }
}

object AdminController extends AdminController
