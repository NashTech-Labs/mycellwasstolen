package controllers

import java.io.File
import model.domains.Domain._
import model.users._
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import utils.Common
import play.api.cache.Cache
import play.api.Play.current
import java.text.SimpleDateFormat
import utils.TwitterTweet
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials

class MobileController(mobileService: MobileServiceComponent) extends Controller with Secured {

  val mobileregistrationform = Form(
    mapping(
      "userName" -> nonEmptyText,
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText,
      "imeiMeid" -> nonEmptyText,
      "purchaseDate" -> nonEmptyText,
      "contactNo" -> nonEmptyText,
      "email" -> email,
      "regType" -> nonEmptyText,
      "document" -> nonEmptyText,
      "description" -> nonEmptyText,
      "otherMobileBrand"->text,
      "otherMobileModel"->text)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  val brandregisterform = Form(
    mapping(
      "name" -> nonEmptyText)(BrandForm.apply)(BrandForm.unapply))

   val createmobilemodelform = Form(
    mapping(
      "mobileName" -> nonEmptyText,
      "mobileModel"->nonEmptyText)(MobilesModelForm.apply)(MobilesModelForm.unapply))

  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = mobileService.getMobilesName()
    Logger.info("mobilesName>>" + mobilesName)
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobilesName))
  }

  def mobileRegistrationSecureForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = mobileService.getMobilesName()
    Logger.info("mobilesName>>" + mobilesName)
    Ok(views.html.secureRegistration(mobileregistrationform, mobilesName))
  }


  def brandRegisterForm: EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("Calling MobileNameform")
      val user: Option[User] = Cache.getAs[User](username)
    val email = request.session.get(Security.username).getOrElse("")
    Ok(views.html.createMobileNameForm(brandregisterform,user))
  }

  def createMobileModelForm: EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("Calling createMobileModelform")
      val user: Option[User] = Cache.getAs[User](username)
    val mobilesName = mobileService.getMobilesName()
    Logger.info("createmobilemodelform call>>")
    val email = request.session.get(Security.username).getOrElse("")
    Ok(views.html.createMobileModelForm(createmobilemodelform, mobilesName,user))
  }

  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    val mobilesName = mobileService.getMobilesName()
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, mobilesName)),
      mobileuser => {
        val status = model.domains.Domain.Status.pending
        val sqldate = new java.sql.Date(new java.util.Date().getTime())
        val df = new SimpleDateFormat("MM/dd/yyyy")
        val date= df.format(sqldate)
        val mobileName = mobileService.getMobileNamesById(mobileuser.mobileName.toInt)
       val length = mobileuser.document.length()
       val index = mobileuser.document.indexOf(".")
       val documentName = mobileuser.imeiMeid + mobileuser.document.substring(index)
       val otherMobileBrand=mobileuser.otherMobileBrand
       val otherMobileModel=mobileuser.otherMobileModel
       val regMobile = mobileService.mobileRegistration(Mobile(mobileuser.userName, mobileuser.mobileName,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, model.domains.Domain.Status.pending,
          mobileuser.description, date, documentName,otherMobileBrand,otherMobileModel))
        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val contentType = image.contentType.get
          val fileToSave= image.ref.file.asInstanceOf[File]
          val bucketName ="mcws"
          val AWS_ACCESS_KEY = "AKIAIEVJZRX3DX6WCICQ"
          val AWS_SECRET_KEY = "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"
          val mcwsAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
          val amazonS3Client = new AmazonS3Client(mcwsAWSCredentials)
          amazonS3Client.putObject(bucketName, documentName, fileToSave)
        }

        regMobile match {
          case Right(mobileuser) => {
            try {
               if(mobileuser.regType == "stolen"){
              Common.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                "Registration Confirmed on MCWS", Common.stolenRegisterMessage(mobileuser.imeiMeid))
                }else{
                 Common.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                "Registration Confirmed on MCWS", Common.cleanRegisterMessage(mobileuser.imeiMeid))
                }
                if(mobileuser.regType == "stolen"){
                   TwitterTweet.tweetAMobileRegistration(mobileuser.imeiMeid, "is requested to be marked as Stolen at mycellwasstolen.com")
                 }else{
                   TwitterTweet.tweetAMobileRegistration(mobileuser.imeiMeid, "is requested to be marked as Secure at mycellwasstolen.com")
                      }
            } catch {
              case e: Exception => Logger.info("" + e.printStackTrace())
            }
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("SUCCESS" -> Messages("messages.mobile.register.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
        }
      })
  }

  def getImeiMeidList(imeid: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getImeiMeidList method has been called.")
    val mobileData = mobileService.getMobileRecordByIMEID(imeid)
    val mobilesName=mobileService.getMobileNamesById(mobileData.get.mobileName.toInt)
    val mobileName=mobilesName.get.name
    val mobileModel=mobileService.getMobileModelById(mobileData.get.mobileModel.toInt).get.mobileModel
    Logger.info("Mobile Records" + mobileData)
    if (mobileData != None && mobileData.get.id != None) {
    val mobileDetail = MobileDetail(mobileData.get.userName, mobileName, mobileModel, mobileData.get.imeiMeid,
                             mobileData.get.purchaseDate, mobileData.get.contactNo, mobileData.get.email,
                             mobileData.get.regType,mobileData.get.otherMobileBrand,mobileData.get.otherMobileModel)
      implicit val resultWrites = Json.writes[model.domains.Domain.MobileDetail]
      val obj = Json.toJson(mobileDetail)(resultWrites)
      Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
    } else {
      Ok(Json.obj("status" -> "Error"))
    }
  }

  def getMobileModels(id: Int): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getMobileModels method has been called.")
    val mobileModel = mobileService.getMobileModelsById(id)
    Logger.info("Mobile Models" + mobileModel)
    implicit val resultWrites = Json.writes[model.domains.Domain.MobileModels]
    Ok(Json.toJson(mobileModel))
  }

  def mobileStatus: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.mobileStatus(mobilestatus))
  }

  def isImeiExist(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist - Checking mobile is exist or not with : " + imeiId)
    val isExist = mobileService.isImeiExist(imeiId)
    if (isExist) {
      Logger.info("MobileController:isImeiExist - true")
      Ok("false")
    } else {
      Logger.info("MobileController:isImeiExist - false")
      Ok("true")
    }
  }

  def saveMobileName:Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: brandRegisterForm")
    Logger.info("brandregisterform" + brandregisterform)
     val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    brandregisterform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileNameForm(formWithErrors,user)),
      brand => {
        Logger.info("MobileNameController: saveMobileName - found valid data.")
        val date = new java.sql.Date(new java.util.Date().getTime())
        val regbrand = mobileService.addMobileName(Brand(brand.name, date))

        regbrand match {
          case Right(id) => {
            Redirect(routes.MobileController.brandRegisterForm).flashing("SUCCESS" -> Messages("messages.mobile.brand.added.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.brandRegisterForm).flashing("ERROR" -> Messages("messages.mobile.brand.added.error"))
        }
      })
  }

  def createMobileModel: Action[play.api.mvc.AnyContent] = Action  { implicit request =>
    Logger.info("createMobileModelController:createMobileModel - Mobile Model.")
    Logger.info("createmobilemodelform" + createmobilemodelform)
    val mobilesName = mobileService.getMobilesName()
    val email = request.session.get(Security.username).getOrElse("")
    val user: Option[User] = Cache.getAs[User](email)
    createmobilemodelform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileModelForm(formWithErrors,mobilesName,user)),
      mobilemodel => {
        Logger.info("createmobilemodelController:createmobilemodel - found valid data.")
        val createMobileModel = mobileService.createMobileModel(MobileModels(mobilemodel.mobileModel,mobilemodel.mobileName.toInt))

        createMobileModel match {
          case Right(mobilename) => {
            Redirect(routes.MobileController.createMobileModelForm).flashing("SUCCESS" -> Messages("messages.mobile.model.added.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.createMobileModelForm).flashing("ERROR" -> Messages("messages.mobile.model.added.error"))
        }
      })
  }

}

object MobileController extends MobileController(MobileService)
