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
      "imeiMeid" -> nonEmptyText,
      "purchaseDate" -> sqlDate("yyyy-MM-dd"),
      "contactNo" -> number,
      "email" -> email,
      "regType" -> nonEmptyText,
      "description" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

  val mobilestatus = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(MobileStatus.apply)(MobileStatus.unapply))

  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val mobilesName = userService.getMobilesName()
    Logger.info("mobilesName>>" + mobilesName)
    Ok(views.html.mobileRegistrationForm(mobileregistrationform, mobilesName))
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
          image.ref.moveTo(new File("/home/supriya/Desktop/" + mobileuser.imeiMeid))
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

  /*def getMobileModels(id: Int): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileController: getImeiMeidList method has been called.")
    val mobileModel = userService.getMobileModelsById(id).head
    Logger.info("Mobile Records" + mobileModel)
    implicit val resultWrites = Json.writes[model.domains.Domain.MobileModels]
    val obj = Json.toJson(mobileModel)(resultWrites)
    if (mobileModel.id != None) {
      Logger.info("mobileModel>>>>>>" + mobileModel)
      Ok(Json.obj("status" -> "Ok", "mobileModel" -> obj))
    } else {
      Ok(Json.obj("status" -> "Error"))
    }
  }*/
   
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
}

object MobileController extends MobileController(UserService)