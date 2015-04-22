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
import play.api.libs.json.Writes

/**
 * Contains behaviors to control for fetching audit reports
 */
class AuditController(auditRepo: AuditRepository) extends Controller with Secured {

  /**
   * Represents a BrandShare Tuple
   */
  case class BrandShare(brandShare: Option[List[(String, Float)]])

  /**
   * Describe List of Registrations for a year
   */
  case class Monthly(data: List[Int])

  /**
   * Describe mobile audit form
   */
  val timestampform = Form(
    mapping(
      "imeiMeid" -> nonEmptyText)(AuditForm.apply)(AuditForm.unapply))

  /**
   * Display TimeStamp page
   */
  def timestampPage: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = List()
      Ok(views.html.admin.audits.mobileCheckStatusTimestamp("imeid", list, user))
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
          Ok(views.html.admin.audits.mobileCheckStatusTimestamp("imeid", list, user)).flashing("error" -> "Please correct the errors in the form")
        },
        success = { timestamp =>
          val list = auditRepo.getAllTimestampsByIMEID(timestamp.imeiMeid)
          Ok(views.html.admin.audits.mobileCheckStatusTimestamp("imeid", list, user))
        })
  }

  /**
   * Display all timestamp records for all mobiles
   */
  def allTimestamps: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = auditRepo.getAllTimestamps
      Ok(views.html.admin.audits.mobileCheckStatusTimestamp("all", list, user))
  }

  /**
   * Renders Registration Analytics
   */
  def registrationRecordsByYear(year: String): Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val years = (2012 to Calendar.getInstance().get(Calendar.YEAR)).toList
      Logger.info("---------registrationRecordByYear called")
      Ok(views.html.admin.audits.registrationAnalytics(user, years))
  }

  /**
   * Renders Top Lost Brands Analytics
   */
  def renderTopLostBrands: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Ok(views.html.admin.audits.topLostBrands(user))
  }

  def topLostBrands(n: Int): Action[AnyContent] = withAuth { username =>
    implicit request =>
      //Define JSON writer for tuple
      implicit def tuple2[A: Writes, B: Writes]: Writes[(A, B)] = Writes[(A, B)](o => play.api.libs.json.Json.arr(o._1, o._2))
      val topN = auditRepo.getTopNLostBrands(n)
      topN match {
        case Some(ex: List[(String, Float)]) => {
          implicit val resultWrites = play.api.libs.json.Json.writes[BrandShare]
          Ok(play.api.libs.json.Json.toJson(topN))
        }
        case None =>
          Ok(play.api.libs.json.Json.toJson(List(("NoData", 0.0))))
      }

  }

  /**
   * Renders registrationGrowthByYear Analytics
   */

  def getRegistrationByYears: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val years = (2012 to Calendar.getInstance().get(Calendar.YEAR)).toList
      Logger.info("---------toLostBrands called--------")
      Ok(views.html.admin.audits.registrationGrowth(user))
  }

  /**
   * Getting monthly data by Year id
   * @param year
   */

  def getMonthlyRegistration(year: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("AuditController: getMonthlyData -> called.")
    val topFive = auditRepo.getTopNLostBrands(4)
    val recordList = auditRepo.getRecordByDate(year)
    val monthlyData = Monthly(recordList)
    implicit val resultWrites = play.api.libs.json.Json.writes[Monthly]
    Ok(play.api.libs.json.Json.toJson(monthlyData))
  }
}

/**
 * Lets other access all the methods defined in the class AuditController
 */
object AuditController extends AuditController(AuditRepository)
