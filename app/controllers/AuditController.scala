package controllers

import java.util.Calendar

import model.analyticsServices.AnalyticsService
import model.repository.AuditForm
import model.repository.User
import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Writes
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Security
/**
 * Contains behaviors to control for fetching audit reports
 */
class AuditController(analyticService: AnalyticsService) extends Controller with Secured {
  /**
   * Represents a BrandShare Tuplehub.com/
   */
  case class BrandShare(brandShare: Option[List[(String, Float)]])

  /**
   * Describe List of Registrations for a year
   */
  case class Monthly(data: List[Int])

  /**
   * Represents perDayRegistrationCount
   */
  case class RegistrationCount(registrationCounts: List[(String, Int)])

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
          val list = analyticService.getAllTimestampsByIMEID(timestamp.imeiMeid)
          Ok(views.html.admin.audits.mobileCheckStatusTimestamp("imeid", list, user))
        })
  }

  /**
   * Display all timestamp records for all mobiles
   */
  def allTimestamps: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      val list = analyticService.getAllTimestamps
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

  /**
   * Handles AJAX call to fetch Brand Share in total mobile theft
   * Returns brandShare JSON records in the form: [["Nokia",33.33],["Samsung",33.3],["Others",33.33%]]
   */
  def topLostBrands(n: Int): Action[AnyContent] = Action { 
    implicit request =>
      //Define JSON writer for tuple
      implicit def tuple2[A: Writes, B: Writes]: Writes[(A, B)] = Writes[(A, B)](o => play.api.libs.json.Json.arr(o._1, o._2))
      Ok(play.api.libs.json.Json.toJson(analyticService.formatPieChartData(n)))
  }

  /**
   * Handles AJAX call to fetch perDay Registration count starting from a particular year
   * Returns perDayRegistration JSON records in the form: [12,12,12,0,12,12,9,12,]
   */
  def getPerDayRegistrationCount: Action[AnyContent] = withAuth { username =>
    implicit request =>
      implicit def tuple2[A: Writes, B: Writes]: Writes[(A, B)] = Writes[(A, B)](o => play.api.libs.json.Json.arr(o._1, o._2))
      implicit val resultWrites = play.api.libs.json.Json.writes[RegistrationCount]
          Ok(play.api.libs.json.Json.toJson(analyticService.formatTimeSeriesChartData))
  }

  /**
   * Renders registrationGrowthByYear Analytics
   */
  
  def getRegistrationByYears: Action[AnyContent] = withAuth { username =>
    implicit request =>
      val user: Option[User] = Cache.getAs[User](username)
      Logger.info("----registrationGrowthByYear called ----")
      val minimumYear = analyticService.getRegistrationStartingYear
      Ok(views.html.admin.audits.registrationGrowth(user, minimumYear match { case Some(minYear) => minYear; case _ => 2012 }))
  }

  /**
   * Getting monthly data by Year id
   * @param year
   */
  def getMonthlyRegistration(year: String): Action[AnyContent] = Action { implicit request =>
    Logger.info("AuditController: getMonthlyData -> called.")
    val recordList = analyticService.getRegistrationRecordsByYear(year)
    val monthlyData = Monthly(recordList)
    implicit val resultWrites = play.api.libs.json.Json.writes[Monthly]
    Ok(play.api.libs.json.Json.toJson(monthlyData))
  }
}

/**
 * Lets other access all the methods defined in the class AuditController
 */
object AuditController extends AuditController(AnalyticsService)
