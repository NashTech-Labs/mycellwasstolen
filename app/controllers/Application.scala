package controllers

import play.api.Routes
import play.api.data.Form
import play.api.data.Forms.email
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.tuple
import play.api.mvc.Action
import play.api.mvc.Controller

class Application extends Controller {

  val formExample = Form(
    tuple(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "confPassword" -> nonEmptyText))

  def showExampleForm = Action { implicit request =>
    Ok(views.html.formExample(formExample))
  }

  def handleFormExample = Action { implicit request =>
    formExample.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.formExample(formWithErrors)),
      {
        case (name, email, password, confPassword) =>
          Redirect(routes.Application.index).flashing("SUCCESS" -> "Form submited successfully")
      })
  }

  def index: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.index("Welcome"))
  }

  def javascriptRoutes: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    import routes.javascript._
    Ok(Routes.javascriptRouter("jsRoutes")(
      routes.javascript.MobileController.getImeiMeidList,
      routes.javascript.MobileController.getMobileModels,
      routes.javascript.MobileController.isImeiExist)).as("text/javascript")
  }
}

object Application extends Application
