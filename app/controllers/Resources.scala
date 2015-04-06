package controllers

import play.api.mvc._
import play.api._
import play.api.cache.Cache
import play.api.Play.current
import model.repository.User

/**
 * Defines controllers to handle page resources of application such as contact us, blog, FAQs etc.  
 */

class Resources extends Controller {

  /**
   * Display contact us page
   */
  def contactUs: Action[AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.contact_us(user))
  }

  /**
   * Display blog page
   */
  def blog: Action[AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.blog(user))
  }

  /**
   * Display FAQs page
   */
  def faq: Action[AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.FAQ(user))
  }

  /**
   * Display Discussion Forum page
   */
  def discussionforum: Action[AnyContent] = Action { implicit request =>
    val username = request.session.get(Security.username).getOrElse("None")
    val user: Option[User] = Cache.getAs[User](username)
    Logger.info("USERNAME:::::" + user)
    Ok(views.html.discussionforum(user))
  }
}

/**
 * Lets other classes, packages, traits access all the behaviors defined in the class Resources  
 */
object Resources extends Resources
