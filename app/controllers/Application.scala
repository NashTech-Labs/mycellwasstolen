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
import model.repository.BrandRepository

/**
 * Contains application generated controllers javaScriptRoutes to make ajax calls
 */
class Application extends Controller {

  /**
   * Display the home page
   */
  def index: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val allBrands=BrandRepository.getAllBrands
    Ok(views.html.users.index("Welcome",MobileController.mobileregistrationform,allBrands))
  }

  /**
   * Handle the calling of controllers actions from javascript ajax calls
   */
  def javascriptRoutes: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    import routes.javascript._
    Ok(Routes.javascriptRouter("jsRoutes")(
      routes.javascript.MobileController.checkMobileStatus,
      routes.javascript.MobileController.getModels,
      routes.javascript.MobileController.isImeiExist,
      routes.javascript.AdminController.pending,
      routes.javascript.AdminController.markMobileAsSpam,
      routes.javascript.AdminController.changeMobileRegType)).as("text/javascript")
  }
}
/**
 * Lets other access all the methods defined in the class Application
 */
object Application extends Application
