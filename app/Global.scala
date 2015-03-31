import play.api._
import java.io.File
import utils.Connection
import play.api.Play.current
import scala.slick.jdbc.meta.MTable
import com.typesafe.config.ConfigFactory
import scala.slick.driver.PostgresDriver.simple._
import model.repository.ModelRepository.models
import model.repository.BrandRepository.brands
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

    Connection.databaseObject.withSession { implicit session: Session =>
      if (MTable.getTables("brands").list.isEmpty) {
        brands.ddl.create
        Logger.info("Table brand created in database")
      }

      if (MTable.getTables("models").list.isEmpty) {
        models.ddl.create
        Logger.info("Table brand created in database")
      }

      if (MTable.getTables("mobiles").list.isEmpty) {
        mobiles.ddl.create
        Logger.info("Table brand created in database")
      }
      if (MTable.getTables("audits").list.isEmpty) {
        audits.ddl.create
        Logger.info("Table brand created in database")
      } else

        Logger.info("Table already exists in database")

    }
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown.......")
  }
}
