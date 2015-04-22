/**
 * Provides controller classes to control the application
 */
package controllers

import model.repository._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.write
import play.api.Logger
import play.api._
import play.api.Play.current
import play.api.i18n.Messages
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import utils._
import play.api.cache.Cache
import play.twirl.api.Html

/**
 * Controls administrative tasks such as handling user requests, approving a request, request verification
 */
class AdminController(mobileRepo: MobileRepository, brandRepo: BrandRepository, modelRepo: ModelRepository, auditRepo: AuditRepository, mail: MailUtil
    , s3Util: S3UtilComponent)
  extends Controller with Secured {

  /**
   * Describes the mobile status form
   */
  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  /**
   * Describes the new mobile brand form
   */
  val brandform = Form(
    mapping(
      "name" -> nonEmptyText)(BrandForm.apply)(BrandForm.unapply))

  /**
   * Describe the new mobile model form
   */
  val modelform = Form(
    mapping(
      "brandName" -> nonEmptyText,
      "modelName" -> nonEmptyText)(ModelForm.apply)(ModelForm.unapply))

  /**
   * Display admin home page
   */
  def index: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:index -> called")
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.admin.index("Admin home", user))
  }
  /**
   * Display the new mobile brand registration form
   */
  def brandRegisterForm: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController:brandRegistrationForm -> called")
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.admin.contents.newBrandForm(brandform, user))
  }

  /**
   * Handle new mobile brand form submission and add new mobile brand
   */
  def saveBrand: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController: saveBrand -> called")
      Logger.info("brandregisterform" + brandform)
      val email = request.session.get(Security.username).getOrElse("")
      val user: Option[User] = Cache.getAs[User](email)
      brandform.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.admin.contents.newBrandForm(formWithErrors, user)),
        brand => {
          if (brandRepo.getAllBrands.filter { x => x.name.equalsIgnoreCase(brand.name) }.isEmpty) {
            val insertedBrand = brandRepo.insertBrand(Brand(brand.name))
            insertedBrand match {
              case Right(Some(id)) =>
                Redirect(routes.AdminController.brandRegisterForm).flashing("SUCCESS" -> Messages("messages.mobile.brand.added.success"))
              case _ =>
                Redirect(routes.AdminController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
            }
          } else {
            Redirect(routes.AdminController.brandRegisterForm).flashing("ERROR" -> "Brand allready exist")
          }
        })
  }

  /**
   * Display the new mobile brand model registration form
   */
  def modelRegistrationForm: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController:modelRegistrationForm -> called")
      val user: Option[User] = Cache.getAs[User](username)
      val mobileBrands = brandRepo.getAllBrands
      Ok(views.html.admin.contents.newModelForm(modelform, mobileBrands, user))
  }

  /**
   * Handle new mobile brand model form submission and add new model
   */
  def saveModel: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController:saveModel -> called")
      val brands = brandRepo.getAllBrands
      val email = request.session.get(Security.username).getOrElse("")
      val user: Option[User] = Cache.getAs[User](email)
      modelform.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.admin.contents.newModelForm(formWithErrors, brands, user)),
        modell => {
          if (modelRepo.getAllModelByBrandId(modell.brandName.toInt).filter { x => x.name.equalsIgnoreCase(modell.modelName) }.isEmpty) {
            val insertedModel = modelRepo.insertModel(Model(modell.modelName, modell.brandName.toInt))
            insertedModel match {
              case Right(Some(id)) =>
                Redirect(routes.AdminController.modelRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.success"))
              case _ =>
                Redirect(routes.AdminController.modelRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
            }
          } else {
            Redirect(routes.AdminController.modelRegistrationForm).flashing("ERROR" -> "Model Allready exist")
          }
        })
  }

  /**
   * Renders MobileUser Page
   * @param status, mobile status(pending, approved and proofdemanded)
   * @return mobile page with mobile user according to status
   */
  def requestsList(status: String): Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles -> called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles = mobileRepo.getAllMobilesUserWithBrandAndModel(status)
      Logger.info("mobiles Admin Controller::::" + mobiles)
      Ok(views.html.admin.contents.requestsList(status, mobiles, user))
  }

  /**
   * changes mobile status to approved
   * @param imeiId of mobile
   * @return Action redirecting to either of error or success page
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
              sendEmail(mobileUser.get, "approved")
              tweet(mobileUser.get, "approved")
              Redirect(routes.AdminController.requestsList(page)).flashing("success" -> "Mobile has been approved successfully!")
            case None =>
              Logger.info("AdminController:approve - error in fetching record after approved")
              Redirect(routes.AdminController.requestsList(page)).flashing("success" -> "Mobile has been approved successfully!")
          }
        case _ =>
          Logger.info("AdminController: - false")
          Redirect(routes.AdminController.requestsList(page)).flashing("error" -> "Something wrong!")
      }
  }

  /**
   * Changes mobile status to "proofdemanded"
   * @param imeiId of mobile
   * @return error or success pages
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
              Redirect(routes.AdminController.requestsList(page)).flashing(
                "success" -> "A Proof has been demanded from this user!")
            case None =>
              Logger.info("AdminController:approve - error in fetching record after proof demanded")
              Redirect(routes.AdminController.requestsList(page)).flashing(
                "success" -> "A Proof has been demanded from this user!")
          }
        case _ =>
          Logger.info("AdminController: - No Record were inserted: method returned with left")
          Redirect(routes.AdminController.requestsList(page)).flashing("error" -> "Something wrong!!")

      }
  }

  /**
   * Changes mobile status to pending
   * @param imeiId of mobile
   * @return success or error page
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
      Ok(views.html.admin.contents.changeMobileRegType(mobilestatus, user))
  }

  /**
   * Changes mobile status to clean or stolen
   * @param imeiId of mobile
   * @return success or error page
   */
  def changeMobileRegType(imeiId: String): Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:changeMobileRegType - change Registration type : " + imeiId)
      val mobileUser = mobileRepo.getMobileUserByIMEID(imeiId)
      val regType = if (mobileUser.get.regType == "stolen") "Clean" else "stolen"
      val updatedMobile = Mobile(mobileUser.get.userName, mobileUser.get.brandId, mobileUser.get.modelId,
        mobileUser.get.imei, mobileUser.get.otherImei, mobileUser.get.contactNo, mobileUser.get.email,
        regType, mobileUser.get.mobileStatus,mobileUser.get.regDate, mobileUser.get.document, mobileUser.get.id)
      val result = mobileRepo.changeRegTypeByIMEID(updatedMobile)
      result match {
        case Right(updatedRecord: Int) if updatedRecord != Constants.ZERO =>
          sendEmail(updatedMobile, "changeMobileRegType")
          tweet(updatedMobile, "changeMobileRegType")
          Logger.info("AdminController changeMobileRegType : - true")
          Ok("successs")
        case _ =>
          Logger.info("AdminController changeMobileRegType : - false")
          Ok("error in change status")
      }
  }

  /**
   * This function sends mail to the mobile user
   * @param mobileuser object of Mobile
   * @param msg type of mail
   * @return : Unit
   */
  private def sendEmail(mobileuser: Mobile, msg: String): Unit = {
    val post = Play.current.configuration.getBoolean("Email.send")
    if (!post.get) {
      Logger.info("AdminController:tweet -> disabled")
    } else {
      msg match {
        case "approved" =>
          mail.sendMail(mobileuser.imei + " <" + mobileuser.email + ">", "Registration Confirmed on MCWS", mail.approvedMessage((mobileuser.imei)))
        case "proofDemanded" =>
          mail.sendMail(mobileuser.imei + " <" + mobileuser.email + ">", "Registration Confirmed on MCWS", mail.demandProofMessage(mobileuser.imei))
        case "delete" =>
          mail.sendMail(mobileuser.imei + "<" + mobileuser.email + ">", "Delete mobile registration from MCWS", mail.deleteMessage(mobileuser.imei))
        case "changeMobileRegType" =>
          if (mobileuser.regType == "stolen") {
            mail.sendMail(mobileuser.imei + "<" + mobileuser.email + ">", "Change mobile status from MCWS",
              mail.changeMobileRegTypeStolen(mobileuser.imei))
          } else {
            mail.sendMail(mobileuser.imei + "<" + mobileuser.email + ">", "Change mobile status from MCWS",
              mail.changeMobileRegTypeClean(mobileuser.imei))
          }
        case _ =>
          Logger.info("AdminController:sendEmail -> failed")
      }
    }
  }

  /**
   * This function tweet the post on twitter page
   * @param mobileuser object of mobile
   * @param msg type of tweet
   * @return Unit
   */
  private def tweet(mobileuser: Mobile, msg: String): Unit = {
    val post = Play.current.configuration.getBoolean("Tweet.post")
    if (!post.get) {
      Logger.info("AdminController:tweet -> disabled")
    } else {
      msg match {
        case "approved" =>
          if (mobileuser.regType == "stolen") {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForStolen(mobileuser.imei))
          } else {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForClean(mobileuser.imei))
          }
        case "changeMobileRegType" =>
          if (mobileuser.regType == "stolen") {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForStolen(mobileuser.imei))
          } else {
            TwitterTweet.tweetAMobileRegistration(TwitterTweet.tweetForClean(mobileuser.imei))
          }
      }
    }
  }

  /**
   * Deletes existed mobile
   * @param imeid of mobile
   * @return Unit
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

/**
 * Lets other classes, traits, objects access all the behaviors defined in the class AdminController
 */
object AdminController extends AdminController(MobileRepository, BrandRepository, ModelRepository, AuditRepository, MailUtil, S3Util)
