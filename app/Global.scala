import java.io.File
import java.sql.Date
import scala.slick.driver.PostgresDriver.simple._
import com.typesafe.config.ConfigFactory
import play.api._
import play.api.Play.current
import play.api.mvc.Results.InternalServerError
import utils.Connection
import model.repository.ModelRepository._
import model.repository.BrandRepository
import model.repository.MobileRepository.mobiles
import model.repository.AuditRepository.audits
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
        (audits.ddl).create
        (mobiles.ddl ++ brands.ddl ++ models.ddl).create
        Logger.info("All tables have been created")
        val filePath = Global.getClass().getClassLoader().getResource("csv")
        new File(filePath.toURI()).listFiles foreach { file =>
          //val result = model.convert.readcsv.convert(file)
        }
      }
    } catch {
      case ex: Exception => Logger.info(ex.getMessage() + ex.printStackTrace())
    }
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown.......")
  }
}

object InitialData {
  def insert(): Any = {
    try {
      val date = new java.sql.Date(new java.util.Date().getTime())

      if (BrandRepository.getAllBrands.isEmpty) {
        Logger.info("Adding new mobile name in mobile table")

      } else {
        Logger.info("Not adding new mobile name in mobile table")
      }
    }
  }
}
