package controllers

import model.domains.Domain.Mobile
import model.domains.Domain.User
import model.users.MobileService
import model.users.MobileServiceComponent
import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.i18n.Messages
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.Security
import views.html
import play.api.mvc.EssentialAction

class AuthController(mobileService: MobileServiceComponent) extends Controller with Secured{

  def mobiles: EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:mobiles method has been called.")
      val user: Option[User] = Cache.getAs[User](username)
      val mobiles: List[Mobile] = mobileService.getAllMobiles
      Ok(html.admin.mobiles(mobiles,user))
  }

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    (username == "admin" && password == "1234")
    val user = User("admin", "1234")
    Cache.set(username, user, 60 * 60)
    true
  }

  def login = Action { implicit request =>
    Ok(html.admin.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.admin.login(formWithErrors)),
      user => Redirect(routes.AuthController.mobiles).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.AuthController.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}

trait Secured {

  def username(request: RequestHeader): Option[String] = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader): play.api.mvc.SimpleResult = {
    Results.Redirect(routes.AuthController.login).withNewSession.flashing("success" -> Messages("messages.user.expired"))
  }

  def withAuth(f: => String => Request[play.api.mvc.AnyContent] => Result): play.api.mvc.EssentialAction = {
    Security.Authenticated(username, onUnauthorized) { username =>
      {
        val cachedUser: Option[User] = Cache.getAs[User](username)
        cachedUser match {
          case Some(user) => { Cache.set(username, user, 60 * 60); Action(request => f(username)(request)) }
          case None => Action(request => onUnauthorized(request))
        }
      }
    }
  }
}

object AuthController extends AuthController(MobileService)
