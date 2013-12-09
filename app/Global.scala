import java.io.File
import java.sql.Date

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Session

import com.typesafe.config.ConfigFactory

import model.dals.MobileDAL
import model.domains.Domain._
import model.users._
import model.users.MobileService
import play.api._
import play.api.Logger
import play.api.Play.current
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError
import utils.Connection

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
     //(Mobiles.ddl ++ Brands.ddl ++ MobileModel.ddl).create
     // (Mobiles.ddl)create
        //Logger.info("All tables have been created")
      }
    } catch {
      case ex: Exception => Logger.info(ex.getMessage() + ex.printStackTrace())
    }

    //InitialData.insert
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

 /* def insert(): Any = {
    try {
      val date = new java.sql.Date(new java.util.Date().getTime())
        Logger.info("Adding new users in users table")
      } catch {
      case ex: Exception => Logger.info("Error in  initial data population" + ex.printStackTrace())
    }
  }*/
  
  def insert(): Any = {
    try {
      val mobileService = new MobileService(MobileDAL)

      val date = new java.sql.Date(new java.util.Date().getTime())

      if (mobileService.getMobilesName.isEmpty) {
        Logger.info("Adding new mobile name in mobile table")
       /* val mobileList = List(
          MobilesName("Nokia"),
          MobilesName("Samsung"),
          MobilesName("Micromax"),
          MobilesName("Sony"))
          mobileList foreach { mobilename => mobileService.addMobileName(mobilename) }*/
      }else {
        Logger.info("Not adding new mobile name in mobile table")
      }
    }
  }
}