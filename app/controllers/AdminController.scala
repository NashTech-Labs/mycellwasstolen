package controllers

import model.repository.AuditForm
import model.repository.AuditRepository
import model.repository.Mobile
import model.repository.MobileRepository
import model.repository.MobileStatus
import model.repository.User
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.write
import play.api.Logger
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import utils.Common
import utils.Constants
import utils.TwitterTweet
import play.api.cache.Cache
import play.twirl.api.Html

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
  val timestampform = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(AuditForm.apply)(AuditForm.unapply))

  /**
   * @param status, mobile status(pending, approved and proofdemanded)
   * @return mobiles page with mobile user according to status
   */
  def mobiles(status: String): Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles -> called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles = mobileRepo.getAllMobilesUserWithBrandAndModel(status)
      Logger.info("mobiles Admin Controller::::" + mobiles)
      Ok(views.html.admin.mobiles(status, mobiles, user))
  }

  /**
   * changes mobile status to approved
   * @param imeiId of mobile
   */
  def approve(imeiId: String, page: String): Action[AnyContent] = withAuth { username =>
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
                try {
                  TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Stolen at mycellwasstolen.com")
                }
                catch{
                  case ex:Exception => Logger.info("Some how messege was not tweeted")
                }
              } else {
                mail.sendMail(mobileUser.get.imeiMeid + " <" + mobileUser.get.email + ">",
                  "Registration Confirmed on MCWS", Common.approvedMessage(mobileUser.get.imeiMeid))
                try {
                  TwitterTweet.tweetAMobileRegistration(imeiId, "has been marked as Secure at mycellwasstolen.com")
                } catch {
                  case ex: Exception => Logger.info("Somehow coudn't tweet the messege")
                }
              }

              Redirect(routes.AdminController.mobiles(page)).flashing(
                "success" -> "Mobile has been approved successfully!")
            case None =>
              Logger.info("AdminController:approve - error in fetching record after approved")
              Redirect(routes.AdminController.mobiles(page)).flashing(
                "success" -> "Mobile has been approved successfully!")
          }
        case _ =>
          Logger.info("AdminController: - false")
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!!")
      }
  }

  /**
   * Changes mobile status to proofdemanded
   * @param imeiId of mobile
   */
  def proofDemanded(imeiId: String, page: String): Action[AnyContent] = withAuth { username =>
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
        case _ =>
          Logger.info("AdminController: - No Record were inserted: method returned with left")
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!!")

      }
  }
 
  /**
   * Changes mobile status to pending
   * @param imeiId of mobile
   */
  def pending(imeiId: String): Action[AnyContent] = withAuth { username =>
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
  def changeMobileRegTypeForm: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.admin.changeMobileRegType(mobilestatus, user))
  }

  /**
   * Changes mobile status to clean or stolen
   * @param imeiId of mobile
   */
  def changeMobileRegType(imeiId: String): Action[AnyContent] = withAuth { username =>
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
        case _ =>
          Logger.info("AdminController changeMobileRegType : - false")
          Ok("error in change status")
      }
  }

  /**
   * Deletes existed mobile
   * @param imeid of mobile
   */
  def deleteMobile(imeid: String): Action[AnyContent] = withAuth { username =>
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
  def auditPage: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = List()
      Ok(views.html.admin.audit("imeid", list, user))
  }

  /**
   * Display timestamp records of particular imei number
   */
  def getTimestampByIMEI: Action[AnyContent] = Action {
    implicit request =>
      Logger.info("AdminController:audit -> called")
      val email = request.session.get(Security.username).getOrElse("")
      val user: Option[User] = Cache.getAs[User](email)
      val audit = timestampform.bindFromRequest()
      audit.fold(
        hasErrors = { form =>
          val list = List()
          Ok(views.html.admin.audit("imeid", list, user)).flashing("error" -> "Please correct the errors in the form")
        },
        success = { timestamp =>
          val list = auditRepo.getAllTimestampsByIMEID(timestamp.imeiMeid)
          Ok(views.html.admin.audit("imeid", list, user))
        })
  }

  /**
   * Display all timestamp records for all mobiles
   */
  def getAllTimestamp: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = auditRepo.getAllTimestamps
      Ok(views.html.admin.audit("all", list, user))

  }
}
object AdminController extends AdminController(MobileRepository, AuditRepository, Common)
