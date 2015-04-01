package controllers

import model.repository._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.write
import play.api.Logger
import play.api._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import utils._
import play.api.cache.Cache
import play.twirl.api.Html

class AdminController(mobileRepo: MobileRepository, auditRepo: AuditRepository, mail: MailUtil, s3Util: S3UtilComponent) extends Controller with Secured {

  /**
   * Describes the mobile status form
   */
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

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
              sendEmail(mobileUser.get, "approve")
              tweet(mobileUser.get, "approve")
              Redirect(routes.AdminController.mobiles(page)).flashing("success" -> "Mobile has been approved successfully!")
            case None =>
              Logger.info("AdminController:approve - error in fetching record after approved")
              Redirect(routes.AdminController.mobiles(page)).flashing("success" -> "Mobile has been approved successfully!")
          }
        case _ =>
          Logger.info("AdminController: - false")
          Redirect(routes.AdminController.mobiles(page)).flashing("error" -> "Something wrong!")
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
              sendEmail(mobileUser.get, "proofDemanded")
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
      val regType = if (mobileUser.get.regType == "stolen") "Clean" else "stolen"
      val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.mobileModelId,
        mobileUser.get.imeiMeid, mobileUser.get.otherImeiMeid, mobileUser.get.purchaseDate, mobileUser.get.contactNo, mobileUser.get.email,
        regType, mobileUser.get.mobileStatus, mobileUser.get.description,
        mobileUser.get.regDate, mobileUser.get.document, mobileUser.get.otherMobileBrand, mobileUser.get.otherMobileModel,
        mobileUser.get.id)
      val result = mobileRepo.changeRegTypeByIMEID(updatedMobile)
      result match {
        case Right(updatedRecord: Int) if updatedRecord != Constants.ZERO =>
          sendEmail(mobileUser.get, "changeMobileRegType")
          tweet(mobileUser.get, "changeMobileRegType")
          Logger.info("AdminController changeMobileRegType : - true")
          Ok("success")
        case _ =>
          Logger.info("AdminController changeMobileRegType : - false")
          Ok("error in change status")
      }
  }

  /**
   * This function sends mail to the mobile user
   * @param mobileuser object of Mobile
   * @param msg type of mail
   */
  private def sendEmail(mobileuser: Mobile, msg: String) = {
    val post = Play.current.configuration.getBoolean("Email.send")
    if (!post.get) {
      Logger.info("AdminController:tweet -> disabled")
    } else {
    msg match {
      case "approved" =>
        mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">", "Registration Confirmed on MCWS", mail.demandProofMessage(mobileuser.imeiMeid))
      case "proofDemanded" =>
        mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">", "Registration Confirmed on MCWS", mail.demandProofMessage(mobileuser.imeiMeid))
      case "delete" =>
        mail.sendMail(mobileuser.imeiMeid + "<" + mobileuser.email + ">", "Delete mobile registration from MCWS", mail.deleteMessage(mobileuser.imeiMeid))
      case "changeMobileRegType" =>
        if (mobileuser.regType == "stolen")
          mail.sendMail(mobileuser.imeiMeid + "<" + mobileuser.email + ">", "Change mobile status from MCWS", mail.changeMobileRegTypeStolen(mobileuser.imeiMeid))
        else
          mail.sendMail(mobileuser.imeiMeid + "<" + mobileuser.email + ">", "Change mobile status from MCWS", mail.changeMobileRegTypeClean(mobileuser.imeiMeid))
      case _ =>
        Logger.info("AdminController:sendEmail -> failed")
    }
    }
  }

  private def tweet(mobileuser: Mobile, msg: String) = {
    val post = Play.current.configuration.getBoolean("Tweet.post")
    if (!post.get) {
      Logger.info("AdminController:tweet -> disabled")
    } else {
      msg match {
        case "approve" =>
          if (mobileuser.regType == "stolen") {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForStolen(mobileuser.imeiMeid))
          } else {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForClean(mobileuser.imeiMeid))
          }
        case "changeMobileRegType" =>
          if (mobileuser.regType == "stolen") {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForStolen(mobileuser.imeiMeid))
          } else {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForClean(mobileuser.imeiMeid))
          }
      }
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
          s3Util.delete(mobile.document)
          val result = mobileRepo.deleteMobileUser(imeid)
          result match {
            case Right(deletedRecord: Int) if deletedRecord != Constants.ZERO =>
              sendEmail(mobileUser.get, "delete")
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
}
object AdminController extends AdminController(MobileRepository, AuditRepository, MailUtil, S3Util)
