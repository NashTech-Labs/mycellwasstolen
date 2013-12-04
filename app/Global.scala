import play.api._
import play.api.Logger
import play.api.mvc.SimpleResult
import play.api.mvc.RequestHeader
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Result
import java.sql.Date
import utils.Connection
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session
import com.typesafe.config.ConfigFactory
import java.io.File
import play.api.Play.current
import model.domains.Domain._
import model.users.UserService

object Global extends GlobalSettings{
  
   override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    Logger.info("Application  configuration file is loading with " + mode.toString + "  mode")
    val modeSpecificConfig = config ++ Configuration(ConfigFactory.load(s"${mode.toString.toLowerCase}.conf"))
    super.onLoadConfig(modeSpecificConfig, path, classloader, mode)
  }

  override def onStart(app: Application): Unit = {
    Logger.info("Application has started")

    try {
      Connection.databaseObject.withSession { implicit session: Session =>
     //(Mobiles.ddl ++ MobileName.ddl ++ MobileModel.ddl).create
        (Mobiles.ddl).create
      Logger.info("All tables have been created")
      }
    } catch {
      case ex: Exception => Logger.info(ex.getMessage() + ex.printStackTrace())
    }

//     InitialData.insert
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown.......")
  }
  
  /*override def onError(request: RequestHeader, ex: Throwable) = {
    InternalServerError(views.html.errorPage("error")
    )
  } */ 

 /*override def onHandlerNotFound(request: RequestHeader): Result = {
    NotFound(
      views.html.notFoundPage(request.path)
    )
  }  
*/
   /*override def onBadRequest(request: RequestHeader, error: String) = {
    BadRequest("Bad Request: " + error)
  }*/ 
}

object InitialData {

  def insert(): Any = {
    try {
      val date = new java.sql.Date(new java.util.Date().getTime())
      
        Logger.info("Adding new users in users table")
      } catch {
      case ex: Exception => Logger.info("Error in  initial data population" + ex.printStackTrace())
    }
  }
}