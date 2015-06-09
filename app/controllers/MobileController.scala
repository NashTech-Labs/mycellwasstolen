package controllers
import java.io.File
import java.text.SimpleDateFormat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import play.api._
import play.api.Play.current
import play.api.cache.Cache
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import utils._
import model.repository._
import java.util.Date
import java.sql.Timestamp
import play.api.libs.Files

/**
 * Contains controllers to handle user tasks such as rendering RegistrationForms, handling submits,
 * sending Email Notification on type of registrations etc
 */
class MobileController(mobileRepo: MobileRepository, brandRepo: BrandRepository,
                       modelRepo: ModelRepository, auditRepo: AuditRepository, mail: MailUtil, s3Util: S3UtilComponent, commonUtils: CommonUtils)
  extends Controller with Secured {

  /**
   * Describes the new mobile registration form (used in both stolen and secure mobile registration form)
   */
  val mobileregistrationform = Form(
    mapping(
      "userName" -> nonEmptyText,
      "brandId" -> number,
      "modelId" -> number,
      "imei" -> nonEmptyText,
      "otherImei" -> text,
      "contactNo" -> nonEmptyText,
      "email" -> email,
      "regType" -> nonEmptyText,
      "document" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

  /**
   * Handle the new mobile registration form submission and add new mobile
   */
  def saveMobileUser: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    val brands = brandRepo.getAllBrands
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest("Invalid form data"),
      mobileuser => {
        val date = commonUtils.getSqlDate()
        val index = mobileuser.document.indexOf(".")
        val documentName = mobileuser.imei + mobileuser.document.substring(index)
        val result = mobileRepo.insertMobileUser(Mobile(mobileuser.userName, mobileuser.brandId,
          mobileuser.modelId, mobileuser.imei, mobileuser.otherImei, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, StatusUtil.Status.pending, date, documentName))
        result match {
          case Right(Some(insertRecord: Int)) if insertRecord != Constants.ZERO =>
            val user = mobileRepo.getMobileUserByIMEID(mobileuser.imei)
            sendEmail(user.get, "registration")
            request.body.file("fileUpload").map { image =>
              val fileToSave = image.ref.file.asInstanceOf[File]
              s3Util.store(documentName, fileToSave)
            }
            Ok("success").flashing("success" -> "Your IMEI registration has been successfully added.")
          case _ =>
            Ok("error").flashing("error" -> "Oops! Something wrong. Please try again.")
        }
      })
  }

  /**
   * This function sends mail to the mobile user
   * @param mobileuser object of Mobile
   * @param msg type of mail
   */
  private def sendEmail(mobileuser: Mobile, msg: String) = {
    val post = Play.current.configuration.getBoolean("Email.send")
    if (!post.get) {
      Logger.info("MobileController:sendEmail -> disabled")
    } else {
      msg match {
        case "registration" =>
          if (mobileuser.regType == "stolen") {
            mail.sendMail(mobileuser.imei + " <" + mobileuser.email + ">",
              "Registration Confirmed on MCWS", mail.stolenRegisterMessage(mobileuser.imei))
          } else {
            mail.sendMail(mobileuser.imei + " <" + mobileuser.email + ">",
              "Registration Confirmed on MCWS", mail.cleanRegisterMessage(mobileuser.imei))
          }
        case _ =>
          Logger.info("MobileController:sendEmail -> failed")
      }
    }
  }

  /**
   * Getting mobile details by imei id
   * @param imeid of mobile
   */
  def checkMobileStatus(imeid: String, user: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileUser -> called")
    val data = mobileRepo.getMobileUserByIMEID(imeid)
    data match {
      case Some(mobileData) =>
        val brand = brandRepo.getBrandById(mobileData.brandId).get.name
        val model = modelRepo.getModelById(mobileData.modelId).get.name
        val mobileDetail = MobileDetail(mobileData.userName, brand, model, mobileData.imei, mobileData.otherImei,
          mobileData.mobileStatus.toString(), mobileData.contactNo, mobileData.email,
          mobileData.regType)
        implicit val resultWrites = Json.writes[MobileDetail]
        val obj = Json.toJson(mobileDetail)(resultWrites)
        if (user != "admin") {
          auditRepo.insertTimestamp(Audit(imeid, new Timestamp(new java.util.Date().getTime)))
        }
        Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
      case None =>
        Ok(Json.obj("status" -> "Error"))
    }
  }

  /**
   * Getting all mobile model by brand id
   * @param id, brand id
   */
  def getModels(id: Int): Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileModels -> called.")
    val models = modelRepo.getAllModelByBrandId(id)
    Logger.info("Mobile Models" + models)
    implicit val resultWrites = Json.writes[Model]
    Ok(Json.toJson(models))
  }

  /**
   * Checking mobile is exist or not with imeiId
   * @param imeiId of mobile
   */
  def isImeiExist(imeiId: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist -> called " + imeiId)
    val isExist = mobileRepo.getMobileUserByIMEID(imeiId)
    if (!(isExist.isEmpty)) {
      Logger.info("MobileController:isImeiExist - true")
      Ok("false")
    } else {
      Logger.info("MobileController:isImeiExist - false")
      Ok("true")
    }
  }
}

/**
 * Lets other classes, packages, traits access all the behaviors defined in the class MobileController
 */
object MobileController extends MobileController(MobileRepository, BrandRepository, ModelRepository, AuditRepository, MailUtil, S3Util, CommonUtils)
