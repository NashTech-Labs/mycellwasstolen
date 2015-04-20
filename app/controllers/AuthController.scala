package controllers

import play.api.Logger
import play.api.Play
import play.api.Play.current
import model.repository.User
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms.{ text, tuple }
import play.api.i18n.Messages
import play.api.mvc._
import views.html

/**
 * Contains authorization specific controllers to authorize users
 */
class AuthController extends Controller with Secured {

  /**
   * Describes the admin login form
   */
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => check(email, password)
      }))

  /**
   * Checks Admin's login credentials
   * @param username of Admin
   * @param password of Admin
   */
  private def check(username: String, password: String): Boolean = {

    val adminUsername = Play.current.configuration.getString("admin_username").get
    val adminPassword = Play.current.configuration.getString("admin_password").get
    if (username == adminUsername && password == adminPassword) {
      val user = User(adminUsername, adminPassword)
      Cache.set(username, user, 60 * 60)
      true
    } else { false }
  }

  /**
   * Displays the Admin login form
   */
  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.admin.contents.login(loginForm))
  }

  /**
   * Handle the admin login form submission
   */
  def authenticate: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.admin.contents.login(formWithErrors)),
      user =>
        Redirect(routes.AdminController.index).withSession(Security.username -> user._1))
  }

  /**
   * Handles admin logout
   */
  def logout: Action[AnyContent] = Action {
    Redirect(routes.AuthController.login).withNewSession.flashing(
      "success" -> "You are now logged out.")
  }
}

/**
 * Handles login security
 */
trait Secured {

  /**
   * Gets user from request
   */

  def username(request: RequestHeader): Option[String] = request.session.get(Security.username)
  def unauthorizedSimpleRequest(request: RequestHeader): Result = Results.Redirect(routes.AuthController.login)
  /**
   * Handle unauthorized user
   */
  def onUnauthorized(request: RequestHeader): Result = {
    Results.Redirect(routes.AuthController.login).withNewSession.flashing("success" -> Messages("messages.user.expired"))
  }

  def withAuth(f: => String => Request[AnyContent] => Result): Action[AnyContent] = {
    Action { implicit request =>
      username(request).map { id =>
        val cachedUser: Option[User] = Cache.getAs[User](id)
        cachedUser match {
          case Some(user: User) => f(id)(request)
          case None  => onUnauthorized(request)
        }
      }.getOrElse(unauthorizedSimpleRequest(request))
    }
  }
}

/**
 * Lets other classes, packages, traits access all the behaviors defined in the class AuthController
 */
object AuthController extends AuthController
