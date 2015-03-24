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
import utils.Common
import utils.TwitterTweet
import model.repository.MobileRegisterForm
import model.repository.MobileStatus
import model.repository.BrandForm
import model.repository.ModelForm
import model.repository.BrandRepository
import model.repository.User
import utils.StatusUtil
import model.repository.MobileRepository
import model.repository.Mobile
import model.repository.ModelRepository
import model.repository.Brand
import model.repository.Model
import model.repository.MobileDetail
import model.repository.AuditRepository
import java.util.Date
import model.repository.Audit

class MobileController extends Controller with Secured {

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
    val mobileBrands = BrandRepository.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new secure mobile registration form
   */
  def mobileRegistrationSecureForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobileBrands = BrandRepository.getAllBrands
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.secureRegistration(mobileregistrationform, mobileBrands, user))
  }

  /**
   * Display the new mobile brand registration form
   */
  def brandRegisterForm: EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("Calling brandRegisterForm")
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.createMobileNameForm(brandform, user))
  }

  /**
   * Display the new mobile brand model registration form
   */
  def modelRegistrationForm: EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("Calling modelRegistrationForm")
      val user: Option[User] = Cache.getAs[User](username)
      val mobileBrands = BrandRepository.getAllBrands
      val email = request.session.get(Security.username).getOrElse("")
      Ok(views.html.createMobileModelForm(modelform, mobileBrands, user))
  }

  /**
   * Handle the new mobile registration form submission and add new mobile
   */
  def mobileRegistration: Action[play.api.mvc.MultipartFormData[play.api.libs.Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val mobileBrands = BrandRepository.getAllBrands
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, mobileBrands, user)),
      mobileuser => {
        val status = StatusUtil.Status.pending
        val sqldate = new java.sql.Date(new java.util.Date().getTime())
        val df = new SimpleDateFormat("MM/dd/yyyy")
        val date = df.format(sqldate)
        val mobileName = BrandRepository.getBrandById(mobileuser.brandId)
        val length = mobileuser.document.length()
        val index = mobileuser.document.indexOf(".")
        val documentName = mobileuser.imeiMeid + mobileuser.document.substring(index)
        val otherMobileBrand = mobileuser.otherMobileBrand
        val otherMobileModel = mobileuser.otherMobileModel
        val result = MobileRepository.insertMobileUser(Mobile(mobileuser.userName, mobileuser.brandId,
          mobileuser.mobileModelId, mobileuser.imeiMeid, mobileuser.otherImeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, StatusUtil.Status.pending,
          mobileuser.description, date, documentName, otherMobileBrand, otherMobileModel))
        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val contentType = image.contentType.get
          val fileToSave = image.ref.file.asInstanceOf[File]
          val bucketName = Play.current.configuration.getString("aws_bucket_name").get
          val AWS_ACCESS_KEY = Play.current.configuration.getString("aws_access_key").get
          val AWS_SECRET_KEY = Play.current.configuration.getString("aws_secret_key").get
          val mcwsAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
          val amazonS3Client = new AmazonS3Client(mcwsAWSCredentials)
          amazonS3Client.putObject(bucketName, documentName, fileToSave)
        }

        result match {
          case Right(Some(id)) => {
            try {
              if (mobileuser.regType == "stolen") {
                Common.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                  "Registration Confirmed on MCWS", Common.stolenRegisterMessage(mobileuser.imeiMeid))
              } else {
                Common.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                  "Registration Confirmed on MCWS", Common.cleanRegisterMessage(mobileuser.imeiMeid))
              }
              if (mobileuser.regType == "stolen") {
               // TwitterTweet.tweetAMobileRegistration(mobileuser.imeiMeid, "is requested to be marked as Stolen at mycellwasstolen.com")
              } else {
                 // TwitterTweet.tweetAMobileRegistration(mobileuser.imeiMeid, "is requested to be marked as Secure at mycellwasstolen.com")
              }
            } catch {
              case e: Exception => Logger.info("" + e.printStackTrace())
            }
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.register.success"))
          }
          case Right(None) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
          case Left(message) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
        }
      })
  }

  /**
   * Getting mobile details by imei id
   * @param imeid of mobile
   */
  def getImeiMeidList(imeid: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getImeiMeidList method has been called.")
    val mobileData = MobileRepository.getMobileUserByIMEID(imeid)
    if (mobileData != None && mobileData.get.id != None) {
      val mobileBrand = BrandRepository.getBrandById(mobileData.get.brandId).get.name
      val mobileModel = ModelRepository.getModelById(mobileData.get.mobileModelId).get.name
      Logger.info("Mobile Records" + mobileData)
      val mobileDetail = MobileDetail(mobileData.get.userName, mobileBrand, mobileModel, mobileData.get.imeiMeid, mobileData.get.otherImeiMeid,
        mobileData.get.mobileStatus.toString(), mobileData.get.purchaseDate, mobileData.get.contactNo, mobileData.get.email,
        mobileData.get.regType, mobileData.get.otherMobileBrand, mobileData.get.otherMobileModel)
      implicit val resultWrites = Json.writes[MobileDetail]
      val obj = Json.toJson(mobileDetail)(resultWrites)
      AuditRepository.insertTimestamp(Audit(imeid,(new Date()).toString()))
      Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
    } else {
      Ok(Json.obj("status" -> "Error"))
    }
  }

  /**
   * Getting all mobile model by brand id
   * @param id, brand id
   */
  def getMobileModels(id: Int): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileModels method has been called.")
    val mobileModel = ModelRepository.getAllModelByBrandId(id)
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
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.mobileStatus(mobilestatus, user))
  }

  /**
   * Checking mobile is exist or not with imeiId
   * @param imeiId of mobile
   */
  def isImeiExist(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist - Checking mobile is exist or not with : " + imeiId)
    val isExist = MobileRepository.getMobileUserByIMEID(imeiId)
    if (!(isExist.isEmpty)) {
      Logger.info("MobileController:isImeiExist - true")
      Ok("false")
    } else {
      Logger.info("MobileController:isImeiExist - false")
      Ok("true")
    }
  }

  /**
   * Handle new mobile brand form submission and add new mobile brand
   */
  def saveMobileName: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: brandRegisterForm")
    Logger.info("brandregisterform" + brandform)
    val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    brandform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileNameForm(formWithErrors, user)),
      brand => {
        Logger.info("MobileNameController: saveMobileName - found valid data.")
        val sqldate = new java.sql.Date(new java.util.Date().getTime())
        val df = new SimpleDateFormat("MM/dd/yyyy")
        val date = df.format(sqldate)
        val regbrand = BrandRepository.insertBrand(Brand(brand.name, date))

        regbrand match {
          case Right(id) => {
            Redirect(routes.MobileController.brandRegisterForm).flashing("SUCCESS" -> Messages("messages.mobile.brand.added.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
        }
      })
  }

  /**
   * Handle new mobile brand model form submission and add new model
   */
  def createMobileModel: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("createMobileModelController:createMobileModel - Mobile Model.")
    Logger.info("createmobilemodelform" + modelform)
    val mobileBrands = BrandRepository.getAllBrands
    val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    modelform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileModelForm(formWithErrors, mobileBrands, user)),
      model => {
        Logger.info("createmobilemodelController:createmobilemodel - found valid data.")
        val createMobileModel = ModelRepository.insertModel(Model(model.mobileModel, model.mobileName.toInt))

        createMobileModel match {
          case Right(id) => {
            Redirect(routes.MobileController.modelRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.modelRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
        }
      })
  }
}

object MobileController extends MobileController
