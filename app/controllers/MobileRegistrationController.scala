package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import model.domains.domain._
import model.users.UserService
import model.users._

object MobileRegistrationController extends Controller {
  //this(userService: UserServiceComponent)
  val mobileregistrationform = Form(
    mapping(
      "username" -> nonEmptyText,
      "mobileName" -> nonEmptyText,
      "mobileModel" -> nonEmptyText,
      "imeiMeid" -> nonEmptyText,
      "purchaseDate" -> sqlDate("yyyy-MM-dd"),
      "contactNo" -> number,
      "email" -> email,
      "description" -> nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

  def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.mobileRegistrationForm(mobileregistrationform))
  }
  def mobileRegistration: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Logger.info("MobileRegistrationController:mobileRegistrationForm - Mobile registration.")
    Logger.info("mobileregistrationform" + mobileregistrationform)
    /*mobileregistrationform.bindFromRequest.fold(formWithErrors => BadRequest("errors"),*/ //views.html.mobileRegistrationForm(formWithErrors)
      
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors)),
    mobileuser => {
        Logger.info("MobileRegistrationController:mobileRegistration - found valid data.")
        val regMobile = UserService.mobileRegistration(MobileRegister(mobileuser.username, mobileuser.mobileName,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.description))

        regMobile match {
          case Right(mobileuser) => {
            Redirect(routes.Home.index)
            /* if (mobileuser != None) {
              Redirect("/").flashing("success" -> "Suggestion send successfully.")
            } else {
              Redirect("/").flashing("error" ->"Suggestion is not inserted.")
            }*/
            
          }
          case Left(message) =>
            Redirect(routes.MobileRegistrationController.mobileRegistrationForm).flashing("message" -> "error")
        }
      })

  }

}