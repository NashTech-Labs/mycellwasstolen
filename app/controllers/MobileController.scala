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
  //this(userService: UserServiceComponent)
  val mobileregistrationform = Form(
    mapping(
      "userName" -> nonEmptyText,
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText,
      "imeiMeid" -> nonEmptyText,
      "purchaseDate" -> sqlDate("yyyy-MM-dd"),
      "contactNo" -> number,
      "email" -> email,
      "description" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

   val mobilestatus= Form(
      mapping(
         "imeiMeid" -> nonEmptyText )(MobileStatus.apply)(MobileStatus.unapply))
         
  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.mobileRegistrationForm(mobileregistrationform))
  }

  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    Logger.info("MobileRegistrationController:mobileRegistrationForm - Mobile registration.")
    Logger.info("mobileregistrationform" + mobileregistrationform)
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors)),
      mobileuser => {
        Logger.info("MobileRegistrationController:mobileRegistration - found valid data.")

        val regMobile = UserService.mobileRegistration(Mobile(mobileuser.userName, mobileuser.mobileName,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.description))

        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val contentType = image.contentType.get
          image.ref.moveTo(new File("/home/swati/Desktop/" + mobileuser.imeiMeid))
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
  
   def getImeiMeidList(imeid: String): Action[play.api.mvc.AnyContent] = Action {implicit request =>
      Logger.info("MobileController: getImeiMeidList method has been called.")
      val mobileData = userService.getMobileRecordByIMEID(imeid).head
      Logger.info("Mobile Records" + mobileData)
      implicit val resultWrites = Json.writes[model.domains.Domain.Mobile]
       val obj = Json.toJson(mobileData)(resultWrites)
       if(mobileData.id != None){
         Logger.info("mobileData>>>>>>" +mobileData)
         Ok(Json.obj("status" -> "Ok", "mobileData" -> obj))
         //Ok(Json.toJson("success"))
       }else {
        Ok(Json.obj("status" -> "Error"))
        // Ok(Json.toJson("error"))
       }
  }
  
  def mobileStatus:  Action[play.api.mvc.AnyContent] = Action {implicit request =>
    Ok(views.html.mobileStatus(mobilestatus))   
  }

}

object MobileController extends MobileController(UserService)