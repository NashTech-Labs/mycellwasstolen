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
      "mobileModelId" -> number,
      "imeiMeid" -> nonEmptyText,
      "otherImeiMeid" -> text,
      "purchaseDate" -> nonEmptyText,
      "contactNo" -> nonEmptyText,
      "email" -> email,
      "regType" -> nonEmptyText,
      "document" -> nonEmptyText,
      "description" -> nonEmptyText,
      "otherMobileBrand" -> text,
      "otherMobileModel" -> text)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

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
   * Display the new mobile registration form for stolen mobile
   */
  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController:mobileRegistrationForm -> called")
    val mobileBrands = brandRepo.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new secure mobile registration form
   */
  def mobileRegistrationSecureForm: Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController:mobileRegistrationSecureForm -> called")
    val mobileBrands = brandRepo.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Ok(views.html.secureRegistration(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new mobile brand registration form
   */
  def brandRegisterForm: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController:brandRegistrationForm -> called")
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.createMobileNameForm(brandform, user))
  }

  /**
   * Display the new mobile brand model registration form
   */
  def modelRegistrationForm: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("MobileController:modelRegistrationForm -> called")
      val user: Option[User] = Cache.getAs[User](username)
      val mobileBrands = brandRepo.getAllBrands
      Ok(views.html.createMobileModelForm(modelform, mobileBrands, user))
  }

  /**
   * Handle the new mobile registration form submission and add new mobile
   */
  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val mobileBrands = brandRepo.getAllBrands
    val user: Option[User] = Cache.getAs[User](username)
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, mobileBrands, user)),
      mobileuser => {
        val date = commonUtils.getUtilDate()
        val index = mobileuser.document.indexOf(".")
        val documentName = mobileuser.imeiMeid + mobileuser.document.substring(index)
        val result = mobileRepo.insertMobileUser(Mobile(mobileuser.userName, mobileuser.brandId,
          mobileuser.mobileModelId, mobileuser.imeiMeid, mobileuser.otherImeiMeid, commonUtils.getUtilDate(mobileuser.purchaseDate), mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, StatusUtil.Status.pending,
          mobileuser.description, date, documentName, mobileuser.otherMobileBrand, mobileuser.otherMobileModel))
        result match {
          case Right(Some(insertRecord: Int)) if insertRecord != Constants.ZERO =>
            val user = mobileRepo.getMobileUserByIMEID(mobileuser.imeiMeid)
            sendEmail(user.get, "registration")
            request.body.file("fileUpload").map { image =>
              val fileToSave = image.ref.file.asInstanceOf[File]
              s3Util.store(documentName, fileToSave)
            }
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.register.success"))
          case _ =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
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
            mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
              "Registration Confirmed on MCWS", mail.stolenRegisterMessage(mobileuser.imeiMeid))
          } else {
            mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
              "Registration Confirmed on MCWS", mail.cleanRegisterMessage(mobileuser.imeiMeid))
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
  def getMobileUser(imeid: String, user: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileUser -> called")
    val data = mobileRepo.getMobileUserByIMEID(imeid)
    data match {
      case Some(mobileData) =>
        val brand = brandRepo.getBrandById(mobileData.brandId).get.name
        val model = modelRepo.getModelById(mobileData.mobileModelId).get.name
        val mobileDetail = MobileDetail(mobileData.userName, brand, model, mobileData.imeiMeid, mobileData.otherImeiMeid,
          mobileData.mobileStatus.toString(), mobileData.purchaseDate.toString(), mobileData.contactNo, mobileData.email,
          mobileData.regType, mobileData.otherMobileBrand, mobileData.otherMobileModel)
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
   * Display mobile status form
   */
  def mobileStatus: Action[AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("mobileController:mobileStatus -> called")
    Ok(views.html.mobileStatus(mobilestatus, user))
  }

  /**
   * Checking mobile is exist or not with imeiId
   * @param imeiId of mobile
   */
  def isImeiExist(imeiId: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist -> called " + imeiId)
    val result = CommonUtils.validateImei(imeiId)
    if (result) {
      val isExist = mobileRepo.getMobileUserByIMEID(imeiId)
      if (!(isExist.isEmpty)) {
        Logger.info("MobileController:isImeiExist - true")
        Ok("false")
      } else {
        Logger.info("MobileController:isImeiExist - false")
        Ok("true")
      }
    } else {
      Logger.info("MobileController:isImeiExist - invalid IMEI number")
      Ok("false")
    }
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
        formWithErrors => BadRequest(views.html.createMobileNameForm(formWithErrors, user)),
        brand => {
          val insertedBrand = brandRepo.insertBrand(Brand(brand.name))
          insertedBrand match {
            case Right(Some(id)) =>
              Redirect(routes.MobileController.brandRegisterForm).flashing("SUCCESS" -> Messages("messages.mobile.brand.added.success"))
            case _ =>
              Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
          }
        })
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
        formWithErrors => BadRequest(views.html.createMobileModelForm(formWithErrors, brands, user)),
        modell => {
          val insertedModel = modelRepo.insertModel(Model(modell.modelName, modell.brandName.toInt))
          insertedModel match {
            case Right(Some(id)) =>
              Redirect(routes.MobileController.modelRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.success"))
            case _ =>
              Redirect(routes.MobileController.modelRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
          }
        })
  }
}

object MobileController extends MobileController(MobileRepository, BrandRepository, ModelRepository, AuditRepository, MailUtil, S3Util, CommonUtils)
