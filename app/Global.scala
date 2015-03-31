import java.io.File
import java.sql.Date
import scala.slick.driver.PostgresDriver.simple._
import com.typesafe.config.ConfigFactory
import play.api._
import play.api.Play.current
import play.api.mvc.Results.InternalServerError
import utils.Connection
import model.repository._
import model.repository.ModelRepository.models
import model.repository.BrandRepository.brands
import model.repository.MobileRepository.mobiles
import model.repository.AuditRepository.audits
import play.api.mvc.RequestHeader
import play.mvc._
import org.omg.CosNaming.NamingContextPackage.NotFound
import views.html.defaultpages.notFound
import play.api.mvc.Action
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    Logger.info("Application  configuration file is loading with " + mode.toString + "  mode")
    val modeSpecificConfig = config ++ Configuration(ConfigFactory.load(s"${mode.toString.toLowerCase}.conf"))
    super.onLoadConfig(modeSpecificConfig, path, classloader, mode)
  }

  override def onStart(app: Application): Unit = {
    Logger.info("Application has started")
    val bucketName = Play.application.configuration.getString("aws_bucket_name")
    val accessKey = Play.application.configuration.getString("aws_access_key")
    val secretKey = Play.application.configuration.getString("aws_secret_key")
    val userId = Play.application.configuration.getString("smtp.user")
    val password = Play.application.configuration.getString("smtp.password")
    try {
      Connection.databaseObject.withSession { implicit session: Session =>
        (brands.ddl ++ models.ddl ++ mobiles.ddl ++ audits.ddl).create
        Logger.info("All tables have been created")
      }
    } catch {
      case ex: Exception => Logger.info("please provide csvs in conf" + ex.printStackTrace())
    }
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown.......")

  }
  override def onHandlerNotFound(request: RequestHeader) = {
    Future{
      play.api.mvc.Results.Ok(views.html.errorPage("page not found"))
    }
  }

}