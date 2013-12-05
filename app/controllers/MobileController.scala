package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import model.domains.Domain._
import model.users.UserService
import model.users._
import java.io.File
import play.api.libs.json.Json

class MobileController(userService: UserServiceComponent) extends Controller {

  val mobileregistrationform = Form(
    mapping(
      "userName" -> nonEmptyText,
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText,
      "imeiMeid" -> nonEmptyText.verifying(" IMEI must be of minimum 14 digits",_.matches("[0-9]{14,17}")),
      "purchaseDate" -> sqlDate("yyyy-MM-dd"),
      "contactNo" -> nonEmptyText,
      "email" -> email,
      "regType" -> nonEmptyText,
      "description" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))
      
  val addmobilenameform = Form(
    mapping(
      "mobileName" -> nonEmptyText)(MobilesNameForm.apply)(MobilesNameForm.unapply))

  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = userService.getMobilesName()
    Logger.info("mobilesName>>" + mobilesName)
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobilesName))
  }
  
  def mobileRegistrationSecureForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = userService.getMobilesName()
    Logger.info("mobilesName>>" + mobilesName)
    Ok(views.html.secureRegistration(mobileregistrationform, mobilesName))
  }
  
   
  def addmobileNameForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("addMobileform call>>")
    Ok(views.html.addmobileNameForm(addmobilenameform))
  }

  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    Logger.info("MobileRegistrationController:mobileRegistrationForm - Mobile registration.")
    Logger.info("mobileregistrationform" + mobileregistrationform)
    val mobilesName = userService.getMobilesName()
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors, mobilesName)),
      mobileuser => {
        Logger.info("MobileRegistrationController:mobileRegistration - found valid data.")

        val regMobile = userService.mobileRegistration(Mobile(mobileuser.userName, mobileuser.mobileName,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.regType, mobileuser.description))

        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val contentType = image.contentType.get
          image.ref.moveTo(new File("/home/gaurav/Desktop/" + mobileuser.imeiMeid))
        }

        regMobile match {
          case Right(mobileuser) => {
            Redirect(routes.Application.index)

          }
          case Left(message) =>
            Redirect(routes.MobileController.mobileRegistrationForm).flashing("message" -> "error")
        }
      })

  }

  def getImeiMeidList(imeid: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getImeiMeidList method has been called.")
    val mobileData = userService.getMobileRecordByIMEID(imeid)
    Logger.info("Mobile Records" + mobileData)
    implicit val resultWrites = Json.writes[model.domains.Domain.Mobile]
    if (mobileData != None && mobileData.get.id != None) {
      val obj = Json.toJson(mobileData.get)(resultWrites)
      Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
    } else {
      Ok(Json.obj("status" -> "Error"))
    }
  }
   
   def getMobileModels(id: Int): Action[play.api.mvc.AnyContent] = Action {implicit request =>
      Logger.info("MobileController: getMobileModels method has been called.")
      val mobileModel = userService.getMobileModelsById(id)
      Logger.info("Mobile Models" + mobileModel)
      implicit val resultWrites = Json.writes[model.domains.Domain.MobileModels]
      Ok(Json.toJson(mobileModel))
  }

  def mobileStatus: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.mobileStatus(mobilestatus))
  }

  def isImeiExist(imeiId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController:isImeiExist - Checking mobile is exist or not with : " + imeiId)
    val isExist = userService.isImeiExist(imeiId)
    if (isExist) {
      Logger.info("MobileController:isImeiExist - true")
      Ok("false")
    } else {
      Logger.info("MobileController:isImeiExist - false")
      Ok("true")
    }
  }
  
  
  def addMobileName = Action(parse.multipartFormData) { implicit request =>
    Logger.info("addMobileNameController:addmobileNameForm - Mobile Name.")
    Logger.info("addmobilenameform" + addmobilenameform)
    addmobilenameform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.addmobileNameForm(formWithErrors)),
      mobilename => {
        Logger.info("addMobileNameController:addMobileName - found valid data.")

        val addMobile = userService.addMobileName(MobilesName(mobilename.mobileName))

        addMobile match {
          case Right(mobilename) => {
            Redirect(routes.Application.index)

          }
          case Left(message) =>
            Redirect(routes.MobileController.addmobileNameForm).flashing("message" -> "error")
        }
      })

  }

  
}

object MobileController extends MobileController(UserService)