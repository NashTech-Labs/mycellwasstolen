import java.io.File
import java.sql.Date
import scala.slick.driver.PostgresDriver.simple._
import com.typesafe.config.ConfigFactory
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
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import play.mvc._
import org.omg.CosNaming.NamingContextPackage.NotFound
import views.html.defaultpages.notFound
import play.api.mvc.Action
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import model.convert.TablesEnum

object Global extends GlobalSettings {

  /**
   * Loads all configurations from the configuration file when the application starts
   */

  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    Logger.info("Application  configuration file is loading with " + mode.toString + "  mode")
    val modeSpecificConfig = config ++ Configuration(ConfigFactory.load(s"${mode.toString.toLowerCase}.conf"))
    super.onLoadConfig(modeSpecificConfig, path, classloader, mode)
  }

  def getValue(keyname: String):Option[String] = Play.application.configuration.getString(keyname)

  /**
   * Loads all credentials and create tables when application starts
   */

  override def onStart(app: Application): Unit = {
    Logger.info("Application has started")
    val bucketName = getValue("aws_bucket_name")
    val accessKey = getValue("aws_access_key")
    val secretKey = getValue("aws_secret_key")
    val userId = getValue("smtp.user")
    val password = getValue("smtp.password")
    val adminUsername = getValue("admin_username")
    val adminPassword = getValue("admin_password")

    Connection.databaseObject.withSession { implicit session: Session =>
      val allTables = Map("brands" -> brands, "models" -> models, "mobiles" -> mobiles, "audits" -> audits)
        .filter(tablenameWithTable => MTable.getTables(tablenameWithTable._1).list.isEmpty)
        .foreach {
          _tablenameWithTable => _tablenameWithTable._2.ddl.create
        }

      /*val allHasCreated = MTable.getTables("mobiles").list.isEmpty &&
        MTable.getTables("audits").list.isEmpty &&
        MTable.getTables("models").list.isEmpty &&
        MTable.getTables("brands").list.isEmpty
      if (!allHasCreated) importDB*/
    }
  }

  def getFileNameWithoutExt(filename: String): Option[String] = filename.split(".csv").toList.headOption.map(_.toUpperCase)

  /**
   * Invoke CSV Reader if database table does not exists
   * @param tableName:String
   * @return Unit
   */

  def importDB:Unit = {
    try {
      val filePath = Global.getClass().getClassLoader().getResource("csv")
      new File(filePath.toURI()).listFiles foreach { file =>
        getFileNameWithoutExt(file.getName).foreach { _fileName =>
          import scala.util.control.Exception._
          allCatch.opt(TablesEnum.withName(_fileName)).foreach {
            validEnum => model.convert.ReadCsv.convert(file, validEnum)
          }
        }
      }
    } catch {
      case ex: Exception => Logger.info("Please check the csv at the destination" + ex.printStackTrace)
    }
  }

  /**
   * Performs task when application goes stop
   */

  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown.......")

  }

  /**
   * Handle the fake requests to application
   */
  override def onHandlerNotFound(request: RequestHeader) = {
    Future {
      Ok(views.html.errorPage("page not found"))
    }
  }
}
