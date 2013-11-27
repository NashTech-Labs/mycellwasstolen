package controllers

import model.domains.Domain._
import play.api._
import play.api.Play.current
import play.api.data.Forms._
import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.data.Form
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Security
import play.api.Routes

class Application extends Controller {
  
  def index: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Ok(views.html.index("Hello"))
  }
  
def javascriptRoutes: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    import routes.javascript._
    Ok(Routes.javascriptRouter("jsRoutes")(
      routes.javascript.MobileController.getImeiMeidList)).as("text/javascript")
  }
}

object Application extends Application