package controllers

import model.repository._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.write
import play.api.Logger
import play.api._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }
import play.api.mvc._
import utils._
import play.api.cache.Cache
import play.twirl.api.Html
import java.util.Calendar

/**
 * Contains behaviors to control for fetching audit reports
 */
class AuditController(auditRepo: AuditRepository) extends Controller with Secured {

  /**
   * Describe mobile audit form
   */
  val timestampform = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(AuditForm.apply)(AuditForm.unapply))

  /**
   * Display audit page
   */
  def auditPage: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = List()
      Ok(views.html.audit.overviews("imeid", list, user))
  }

  /**
   * Display timestamp records of particular imei number
   */
  def timestampsByIMEI: Action[AnyContent] = withAuth { username =>
    implicit request =>
      Logger.info("AdminController:audit -> called")
      val email = request.session.get(Security.username).getOrElse("")
      val user: Option[User] = Cache.getAs[User](email)
      timestampform.bindFromRequest().fold(
        hasErrors = { form =>
          val list = List()
          Ok(views.html.audit.overviews("imeid", list, user)).flashing("error" -> "Please correct the errors in the form")
        },
        success = { timestamp =>
          val list = auditRepo.getAllTimestampsByIMEID(timestamp.imeiMeid)
          Ok(views.html.audit.overviews("imeid", list, user))
        })
  }

  /**
   * Display all timestamp records for all mobiles
   */
  def allTimestamps: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = auditRepo.getAllTimestamps
      Ok(views.html.audit.overviews("all", list, user))
  }

  def registrationRecordsByYear(year: String): Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val years = (2012 to Calendar.getInstance().get(Calendar.YEAR)).toList
      val monthList = List("Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec")
      val recordList = auditRepo.getRecordByDate(year)
      val recordsList = monthList zip recordList
      Ok(views.html.audit.analytics(user, recordsList, years))
  }
}

/**
 * Lets other access all the methods defined in the class AuditController
 */
object AuditController extends AuditController(AuditRepository)
