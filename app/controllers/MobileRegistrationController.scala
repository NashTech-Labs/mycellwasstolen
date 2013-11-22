package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import model.domains.domain._
import model.users.UserService
import model.users._
import java.io.File
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

  def mobileRegistration = Action(parse.multipartFormData) { implicit request =>
    Logger.info("MobileRegistrationController:mobileRegistrationForm - Mobile registration.")
    Logger.info("mobileregistrationform" + mobileregistrationform)
    mobileregistrationform.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.mobileRegistrationForm(formWithErrors)),
      mobileuser => {
        Logger.info("MobileRegistrationController:mobileRegistration - found valid data.")

        val regMobile = UserService.mobileRegistration(MobileRegister(mobileuser.username, mobileuser.mobileName,
          mobileuser.mobileModel, mobileuser.imeiMeid, mobileuser.purchaseDate, mobileuser.contactNo,
          mobileuser.email, mobileuser.description))

        request.body.file("fileUpload").map { image =>
          val imageFilename = image.filename
          val contentType = image.contentType.get
          image.ref.moveTo(new File("/home/gaurav/Desktop/" + mobileuser.imeiMeid))
        }

        regMobile match {
          case Right(mobileuser) => {
            Redirect(routes.Home.index)

          }
          case Left(message) =>
            Redirect(routes.MobileRegistrationController.mobileRegistrationForm).flashing("message" -> "error")
        }
      })

  }

}