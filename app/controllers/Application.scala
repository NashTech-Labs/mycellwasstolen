package controllers

import play.api.Routes
import play.api.data.Form
import play.api.data.Forms.email
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.tuple
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc._
import play.api._
import play.api.cache.Cache
import play.api.Play.current
import model.repository.User

class Application extends Controller {


  def index: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val username=request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.index("Welcome",user))
  }

  def javascriptRoutes: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    import routes.javascript._
    Ok(Routes.javascriptRouter("jsRoutes")(
      routes.javascript.MobileController.getImeiMeidList,
      routes.javascript.MobileController.getMobileModels,
      routes.javascript.MobileController.isImeiExist,
      routes.javascript.AdminController.approve,
      routes.javascript.AdminController.proofDemanded,
      routes.javascript.AdminController.pending,
      routes.javascript.AdminController.sendMailForDemandProof,
      routes.javascript.AdminController.sendMailForApprovedRequest,
      routes.javascript.AdminController.deleteMobile,
      routes.javascript.AdminController.mobiles,
      routes.javascript.AdminController.changeMobileRegType)).as("text/javascript")
  }
}

object Application extends Application
