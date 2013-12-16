package controllers

import play.api.mvc._
import play.api._
class Resources extends Controller{
  def contactUs= Action { implicit request =>
    Ok(views.html.contact_us())}
}
object Resources extends Resources