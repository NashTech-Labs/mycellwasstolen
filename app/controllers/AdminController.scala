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
import model.repository.{ Mobile, Brand, Model, Audit, User, MobileStatus, AuditForm }
import model.repository.{ AuditRepository, MobileRepository }
import utils.Constants

class AdminController(mobileRepo: MobileRepository, auditRepo: AuditRepository, mail: Common) extends Controller with Secured {

  implicit val formats = DefaultFormats
  /**
   * Describes the mobile status form
   */
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  /**
   * Describe mobile audit form
   */
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
      val mobiles = mobileRepo.getAllMobilesUserWithBrandAndModel(status)
      Logger.info("mobiles Admin Controller::::" + mobiles)
      Ok(html.admin.mobiles(status, mobiles, user))
  }

  /**
   * changes mobile status to approved
   * @param imeiId of mobile
   */
  def approve(imeiId: String, page: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:approve - change status to approve : " + imeiId)
      val result = mobileRepo.changeStatusToApproveByIMEID(imeiId)
      result match {
        case Right(updatedRecord: Int) if updatedRecord != Constants.ZERO =>
          val mobileUser = mobileRepo.getMobileUserByIMEID(imeiId)
          mobileUser match {
            case Some(mobile) =>
              if (mobileUser.get.regType == "stolen") {
                mail.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                  "Registration Confirmed on MCWS", Common.approvedMessage(mobileUser.get.imeiMeid))
                //TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
              } else {
                mail.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
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
        case Left(messege) =>
          Redirect(routes.AdminController.mobiles(page)).flashing(
            "error" -> "Something wrong!!")
        case _ =>
          Logger.info("AdminController: - false")
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!!")
      }
  }

  /**
   * Changes mobile status to proofdemanded
   * @param imeiId of mobile
   */
  def proofDemanded(imeiId: String, page: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:proofDemanded - change status to proofDemanded : " + imeiId)
      val result = mobileRepo.changeStatusToDemandProofByIMEID(imeiId)
      result match {
        case Right(updatedRecord: Int) if updatedRecord != Constants.ZERO =>
          val mobileUser = mobileRepo.getMobileUserByIMEID(imeiId)
          mobileUser match {
            case Some(mobile) =>
              mail.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                "Registration Confirmed on MCWS", Common.demandProofMessage(mobileUser.get.imeiMeid))
              Redirect(routes.AdminController.mobiles(page)).flashing(
                "success" -> "A Proof has been demanded from this user!")
            case None =>
              Logger.info("AdminController:approve - error in fetching record after proof demanded")
              Redirect(routes.AdminController.mobiles(page)).flashing(
                "success" -> "A Proof has been demanded from this user!")
          }
        case Left(message) =>
          Logger.info("AdminController: - false" + message)
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!!")
        case _ =>
          Logger.info("AdminController: - false")
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!!")

      }
  }

  /**
   * Changes mobile status to pending
   * @param imeiId of mobile
   */
  def pending(imeiId: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:pending - change status to pending : " + imeiId)
      val result = mobileRepo.changeStatusToPendingByIMEID(imeiId)
      result match {
        case Right(deletedRecord: Int) if deletedRecord != Constants.ZERO =>
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
  def changeMobileRegType(imeiId: String): EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:changeMobileRegType - change Registration type : " + imeiId)
      val mobileUser = mobileRepo.getMobileUserByIMEID(imeiId)
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
      val result = mobileRepo.changeRegTypeByIMEID(updatedMobile)
      result match {
        case Right(updatedRecord: Int) if updatedRecord != Constants.ZERO =>
          Logger.info("AdminController changeMobileRegType : - true")
          Ok("success")
        case Left(message) =>
          Logger.info("AdminController changeMobileRegType : - false")
          Ok("error")
        case _ =>
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
      val mobileUser = mobileRepo.getMobileUserByIMEID(imeid)
      mobileUser match {
        case Some(mobile) =>
          val result = mobileRepo.deleteMobileUser(imeid)
          result match {
            case Right(deletedRecord: Int) if deletedRecord != Constants.ZERO =>
              mail.sendMail(mobile.imeiMeid + "<" + mobile.email + ">",
                "Delete mobile registration from MCWS", Common.deleteMessage(mobile.imeiMeid))
              Ok("Success of Delete ajax call")
            case Left(msg) =>
              Logger.info("AdminController:deleteMobile - error in deleting record" + msg)
              Ok("Error of Delete ajax call")
          }
        case None =>
          Logger.info("AdminController:deleteMobile - error in fetching record")
          Ok("error in Delete ajax call")
      }
  }

  /**
   * Display audit page
   */
  def auditPage: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = List()
      Ok(html.admin.audit("imeid", list, user))
  }

  /**
   * Display timestamp records of particular imei number
   */
  def audit: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val audit = auditform.bindFromRequest()
      Logger.info(":::::::::::::::::::::::" + audit)
      audit.fold(
        hasErrors = { form =>
          val list = List()
          Ok(html.admin.audit("imeid", list, user)).flashing("error" -> "Please correct the errors in the form")
        },
        success = { audit =>
          val list = auditRepo.getAllTimestampsByIMEID(audit.imeiMeid)
          Ok(html.admin.audit("imeid", list, user))
        })
  }

  /**
   * Display all timestamp records for all mobiles
   */
  def auditAllRecords: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = auditRepo.getAllTimestamps
      Ok(html.admin.audit("all", list, user))

  }
}
object AdminController extends AdminController(MobileRepository, AuditRepository, Common)
