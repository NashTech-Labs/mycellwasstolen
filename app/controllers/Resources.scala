package controllers

import play.api.mvc._
import play.api._
import model.domains.Domain._
import play.api.cache.Cache
import play.api.Play.current
class Resources extends Controller{
  def contactUs:Action[AnyContent]= Action { implicit request =>
    val username=request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.contact_us(user))}

  def blog:Action[AnyContent]= Action { implicit request =>
    val username=request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.blog(user))}

  def faq:Action[AnyContent]= Action { implicit request =>
    val username=request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.FAQ(user))
    }

  def discussionforum:Action[AnyContent]= Action { implicit request =>
    val username=request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.discussionforum(user))
    }
}
object Resources extends Resources
