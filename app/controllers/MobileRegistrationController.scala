package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import model.domains.domain.MobileRegisterForm

object MobileRegistrationController extends Controller {
val mobileregistrationform=Form(
    mapping(
        "username"->nonEmptyText,
        "mobileName"->nonEmptyText,
        "monileModel"->nonEmptyText,
        "mobileIMEI"->nonEmptyText,
        "purchaseDate"->sqlDate("yyyy-MM-dd"),
        "contactNo"->nonEmptyText,
        "email"->email,
        "description"->nonEmptyText)(MobileRegisterForm.apply)(MobileRegisterForm.unapply))

        def mobileRegistrationForm: Action[play.api.mvc.AnyContent] = Action { implicit request =>
          Ok(views.html.mobileRegistrationForm(mobileregistrationform))
        }
    	def mobileRegistration =TODO
    
}