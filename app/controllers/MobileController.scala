package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import model.domains.Domain._
import model.users._
import java.io.File
import play.api.libs.json.Json
import play.api.i18n.Messages
import utils.Common

class MobileController(mobileService: MobileServiceComponent) extends Controller {

  val mobileregistrationform = Form(
    mapping(
      "userName" -> nonEmptyText,
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText,
      "imeiMeid" -> nonEmptyText,
      "purchaseDate" -> sqlDate("yyyy-MM-dd"),
      "contactNo" -> nonEmptyText,
      "email" -> email,
      "regType" -> nonEmptyText,
      "description" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

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
  
  def brandRegisterForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("Calling MobileNameform")
    Ok(views.html.createMobileNameForm(brandregisterform))
  }
  
  def createMobileModelForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = mobileService.getMobilesName()
    Logger.info("createmobilemodelform call>>")
    Ok(views.html.createMobileModelForm(createmobilemodelform, mobilesName))
  }

  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    Logger.info("MobileRegistrationController:mobileRegistrationForm - Mobile registration.")
    val mobilesName = mobileService.getMobilesName()
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, mobilesName)),
      mobileuser => {
        Logger.info("MobileRegistrationController:mobileRegistration - found valid data.")
        val status = model.domains.Domain.Status.pending
        val date = new java.sql.Date(new java.util.Date().getTime())
        val mobileName = mobileService.getMobileNamesById(mobileuser.mobileName.toInt)
       Logger.info("MobileName - found valid data." + mobileName)
        /*val regMobile = mobileService.mobileRegistration(Mobile(mobileuser.userName, mobileName.get.name,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, model.domains.Domain.Status.pending, mobileuser.description, date, ))*/

        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val exte = imageFilename.split(".")
          Logger.info("extension" + exte)
          Logger.info("imageFilename" +imageFilename)
          val contentType = image.contentType.get
          image.ref.moveTo(new File("proofDocuments/" + mobileuser.imeiMeid + "." +exte))
        }
        
        val regMobile = mobileService.mobileRegistration(Mobile(mobileuser.userName, mobileName.get.name,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, model.domains.Domain.Status.pending, mobileuser.description))

        regMobile match {
          case Right(mobileuser) => {
            try {
              Common.sendMail(mobileuser.imeiMeid + " <" + mobileuser.email + ">",
                "Registration Confirmed on MCWS", Common.registerMessage(mobileuser.imeiMeid))
            } catch {
              case e: Exception => Logger.info("" + e.printStackTrace())
            }
            Redirect(routes.Application.index).flashing("SUCCESS" -> Messages("messages.mobile.register.success"))
          }
          case Left(message) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("ERROR" -> Messages("messages.mobile.register.error"))
        }
      })
  }

  def getImeiMeidList(imeid: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getImeiMeidList method has been called.")
    val mobileData = mobileService.getMobileRecordByIMEID(imeid)
    Logger.info("Mobile Records" + mobileData)
    if (mobileData != None && mobileData.get.id != None) {
	  val mobileDetail = MobileDetail(mobileData.get.userName, mobileData.get.mobileName, mobileData.get.mobileModel, mobileData.get.imeiMeid,
	                     mobileData.get.purchaseDate, mobileData.get.contactNo, mobileData.get.email, mobileData.get.regType)
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
    brandregisterform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileNameForm(formWithErrors)),
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
    createmobilemodelform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createMobileModelForm(formWithErrors,mobilesName)),
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
