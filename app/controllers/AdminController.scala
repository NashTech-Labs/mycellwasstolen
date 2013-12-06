package controllers

import model.domains.Domain._
import model.domains.Domain._
import model.users.MobileServiceComponent
import play.api._
import play.api.Logger
import play.api.Play.current
import play.api.data._
import play.api.data.Form
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.email
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.tuple
import play.api.i18n.Messages
import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.Security
import views._
import play.api.cache.Cache
import model.users.MobileService

class AdminController(mobileService: MobileServiceComponent) extends Controller with Secured{
   val adminProfile = Form(
    mapping(
      "emal"-> nonEmptyText,
      "password" -> nonEmptyText)(Admin.apply)(Admin.unapply))
  
  def mobileRecord: Action[play.api.mvc.AnyContent] = Action { implicit request =>
        Logger.info("ddfjhkljhk")
        Ok(html.admin.users(adminProfile))
  }
   
  def showAllMobileList:  EssentialAction = withAuth { username =>
    implicit request =>
      Logger.info("showAllMobileList has been called")
      val user: Option[Mobile] = Cache.getAs[Mobile](username)
      val users: List[Mobile] = mobileService.getUserList
        Ok(html.admin.users(adminProfile))
 }
  
    def authenticate: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    adminProfile.bindFromRequest.fold(
      formWithErrors => BadRequest(html.admin.users(formWithErrors)),
      adminrecord => Redirect(routes.AdminController.mobileRecord).withSession(Security.username -> adminrecord.email))
  }
}

trait Secured {

  def username(request: RequestHeader): Option[String] = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader): play.api.mvc.SimpleResult = {
    Results.Redirect(routes.AdminController.mobileRecord).withNewSession.flashing("success" -> Messages("messages.user.expired"))
  }

  def withAuth(f: => String => Request[play.api.mvc.AnyContent] => Result): play.api.mvc.EssentialAction = {
    Security.Authenticated(username, onUnauthorized) { username =>
      {
        val cachedUser: Option[Mobile] = Cache.getAs[Mobile](username)
        cachedUser match {
          case Some(user) => { Cache.set(username, user, 60 * 60); Action(request => f(username)(request)) }
          case None => Action(request => onUnauthorized(request))
        }
      }
    }
  }
}

object AdminController extends AdminController(MobileService)