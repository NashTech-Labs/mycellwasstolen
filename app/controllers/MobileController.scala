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
import utils.StatusUtil
import java.util.Date
import utils.Constants

class MobileController(mobileRepo: MobileRepository, brandRepo: BrandRepository,
                       modelRepo: ModelRepository, auditRepo: AuditRepository, mail: Common, s3Util: S3UtilComponent)
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
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText)(ModelForm.apply)(ModelForm.unapply))

  /**
   * Display the new mobile registration form for stolen mobile
   */
  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobileBrands = brandRepo.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("MobileController:mobileRegistrationForm -> called")
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new secure mobile registration form
   */
  def mobileRegistrationSecureForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobileBrands = brandRepo.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("MobileController:mobileRegistrationSecureForm -> called")
    Ok(views.html.secureRegistration(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new mobile brand registration form
   */
  def brandRegisterForm: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Logger.info("MobileController:brandRegistrationForm -> called")
      Ok(views.html.createMobileNameForm(brandform, user))
  }

  /**
   * Display the new mobile brand model registration form
   */
  def modelRegistrationForm: EssentialAction = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val mobileBrands = brandRepo.getAllBrands
      Logger.info("MobileController:modelRegistrationForm -> called")
      Ok(views.html.createMobileModelForm(modelform, mobileBrands, user))
  }

  /**
   * Handle the new mobile registration form submission and add new mobile
   */
  def mobileRegistration: Action[play.api.mvc.MultipartFormData[play.api.libs.Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val allBrands = brandRepo.getAllBrands
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, allBrands, user)),
      mobileuser => {
        val sqldate = new java.sql.Date(new java.util.Date().getTime())
        val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
        val date = dateFormat.format(sqldate)
        val index = mobileuser.document.indexOf(".")
        val documentName = mobileuser.imeiMeid + mobileuser.document.substring(index)
        val result = mobileRepo.insertMobileUser(Mobile(mobileuser.userName, mobileuser.brandId,
          mobileuser.mobileModelId, mobileuser.imeiMeid, mobileuser.otherImeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, StatusUtil.Status.pending,
          mobileuser.description, date, documentName, mobileuser.otherMobileBrand, mobileuser.otherMobileModel))
        request.body.file("fileUpload").map { image =>
          val fileToSave = image.ref.file.asInstanceOf[File]
          s3Util.store(documentName, fileToSave)
        }
        result match {
          case Right(Some(insertRecord: Int)) if insertRecord != Constants.ZERO =>
            if (mobileuser.regType == "stolen") {
              mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                "Registration Confirmed on MCWS", mail.stolenRegisterMessage(mobileuser.imeiMeid))
            } else {
              mail.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                "Registration Confirmed on MCWS", mail.cleanRegisterMessage(mobileuser.imeiMeid))
            }
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.register.success"))
          case Right(None) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
          case Left(message) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
          case _ =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
        }
      })
  }

  /**
   * Getting mobile details by imei id
   * @param imeid of mobile
   */
  def getMobileUser(imeid: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileUser -> called")
    val data = mobileRepo.getMobileUserByIMEID(imeid)
    data match {
      case Some(mobileData) =>
        val mobileBrand = brandRepo.getBrandById(mobileData.brandId).get.name
        val mobileModel = modelRepo.getModelById(mobileData.mobileModelId).get.name
        val mobileDetail = MobileDetail(mobileData.userName, mobileBrand, mobileModel, mobileData.imeiMeid, mobileData.otherImeiMeid,
          mobileData.mobileStatus.toString(), mobileData.purchaseDate, mobileData.contactNo, mobileData.email,
          mobileData.regType, mobileData.otherMobileBrand, mobileData.otherMobileModel)
        implicit val resultWrites = Json.writes[MobileDetail]
        val obj = Json.toJson(mobileDetail)(resultWrites)
        auditRepo.insertTimestamp(Audit(imeid, (new Date()).toString()))
        Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
      case None =>
        Ok(Json.obj("status" -> "Error"))
    }
  }

  /**
   * Getting all mobile model by brand id
   * @param id, brand id
   */
  def getModels(id: Int): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileModels -> called.")
    val mobileModel = modelRepo.getAllModelByBrandId(id)
    Logger.info("Mobile Models" + mobileModel)
    implicit val resultWrites = Json.writes[Model]
    Ok(Json.toJson(mobileModel))
  }

  /**
   * Display mobile status form
   */
  def mobileStatus: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("mobileController:mobileStatus -> called")
    Ok(views.html.mobileStatus(mobilestatus, user))
  }

  /**
   * Check valid imei number or not
   * @param imei number of mobile
   * @return true on valid, otherwise false
   */
  def validateImei(imei: String): Boolean = {
    val arr = imei.map(f => f.toString().toInt).toArray
    val len = arr.length
    val checksum = arr(len - 1)
    if (len != 15)
      false
    var mul = 2
    var sum = 0
    var i = len - 2
    while (i >= 0) {
      if ((arr(i) * mul) >= 10) {
        sum += ((arr(i) * mul) / 10) + ((arr(i) * mul) % 10)
        i = i - 1
      } else {
        sum += arr(i) * mul
        i = i - 1
      }
      if (mul == 2) mul = 1 else mul = 2
    }
    var m10 = sum % 10
    if (m10 > 0) m10 = 10 - m10
    if (m10 == checksum) true
    else
      false
  }

  /**
   * Checking mobile is exist or not with imeiId
   * @param imeiId of mobile
   */
  def isImeiExist(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist -> called " + imeiId)
    val result = validateImei(imeiId)
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
  def saveBrand: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: brandRegisterForm")
    Logger.info("brandregisterform" + brandform)
    val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    brandform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileNameForm(formWithErrors, user)),
      brand => {
        Logger.info("MobileNameController: saveBrand -> called")
        val sqldate = new java.sql.Date(new java.util.Date().getTime())
        val df = new SimpleDateFormat("MM/dd/yyyy")
        val date = df.format(sqldate)
        val regbrand = brandRepo.insertBrand(Brand(brand.name, date))
        regbrand match {
          case Right(Some(id)) =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("SUCCESS" -> Messages("messages.mobile.brand.added.success"))
          case Right(None) =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
          case Left(message) =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
          case _ =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
        }
      })
  }

  /**
   * Handle new mobile brand model form submission and add new model
   */
  def saveModel: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("createMobileModelController:saveModel -> called")
    Logger.info("createmobilemodelform" + modelform)
    val mobileBrands = brandRepo.getAllBrands
    val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    modelform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileModelForm(formWithErrors, mobileBrands, user)),
      model => {
        Logger.info("createmobilemodelController:createmobilemodel - found valid data.")
        val createMobileModel = modelRepo.insertModel(Model(model.mobileModel, model.mobileName.toInt))
        createMobileModel match {
          case Right(Some(id)) =>
            Redirect(routes.MobileController.modelRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.success"))
          case Right(None) =>
            Redirect(routes.MobileController.modelRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.error"))
          case Left(message) =>
            Redirect(routes.MobileController.modelRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
          case _ =>
            Redirect(routes.MobileController.modelRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
        }
      })
  }
}

object MobileController extends MobileController(MobileRepository, BrandRepository, ModelRepository, AuditRepository, Common, S3Util)
