import java.io.File
import java.io.File._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.slick.driver.PostgresDriver.simple.Session
import scala.slick.driver.PostgresDriver.simple.ddlToDDLInvoker
import scala.slick.driver.PostgresDriver.simple.tableQueryToTableQueryExtensionMethods
import scala.slick.jdbc.meta.MTable
import scala.util.Try
import scala.util.control.Exception.allCatch
import com.typesafe.config.ConfigFactory
import model.repository.AuditRepository.audits
import model.repository.BrandRepository.brands
import model.repository.MobileRepository.mobiles
import model.repository.ModelRepository.models
import play.api.Application
import play.api.Configuration
import play.api.GlobalSettings
import play.api.Logger
import play.api.Mode
import play.api.Play
import play.api.Play.current
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Ok
import utils.Connection
import utils.TablesEnum

object Global extends GlobalSettings {
  /**
   * Loads all configurations from the configuration file when the application starts
   */
  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    Logger.info("Application  configuration file is loading with " + mode.toString + "  mode")
    val modeSpecificConfig = config ++ Configuration(ConfigFactory.load(s"${mode.toString.toLowerCase}.conf"))
    super.onLoadConfig(modeSpecificConfig, path, classloader, mode)
  }

  def getValue(keyname: String): Option[String] = Play.application.configuration.getString(keyname)
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
          _tablenameWithTable =>
            _tablenameWithTable._2.ddl.create
            Logger.info("Table is created" + _tablenameWithTable)
        }

      val allHasCreated = MTable.getTables("mobiles").list.isEmpty &&
        MTable.getTables("audits").list.isEmpty &&
        MTable.getTables("models").list.isEmpty &&
        MTable.getTables("brands").list.isEmpty
      if (!allHasCreated) importDB

      Logger.info("--------allHasCreated-------" + allHasCreated)
    }

    def getFileNameWithoutExt(filename: String): Option[String] = filename.split(".csv").toList.headOption.map(_.toUpperCase)

    /*
     * Invoke CSV Reader if database table does not exists
     * @param tableName:String
     * @return Unit
     */

    def importDB = {
      try {
        val file = getValue("data.postgres.dump").get;
        Logger.info("------File---------" + file)
        val filePath = getOpenCSVPath(file)

        Logger.info("Global:importDB -> called")
        Logger.info("------File Path---------" + filePath)
        new File(filePath).listFiles foreach { file =>
          getFileNameWithoutExt(file.getName).foreach { _fileName =>
            import scala.util.control.Exception._
            allCatch.opt(TablesEnum.withName(_fileName)).foreach {
              Logger.info("----------" + _fileName)
              validEnum => utils.ReadCsv.convert(file, validEnum)
            }
          }
        }
      } catch {
        case ex: Exception => Logger.error("----CSV files have not been imported------" + ex.getMessage)

      }
    }

    def getOpenCSVPath(url: String) = {
      val path = getClass.getResource("").getPath
      Logger.info("------getOpenCSVPath---------" + path)
      path.substring(path.indexOf(":") + 1,
        path.indexOf("target")) + url
    }
  }

  /**
   * Performs task when application goes stop
   */
  override def onStop(app: Application): Unit = {
    Logger.info("Application shutdown........")
  }

  /**
   * Handle the fake requests to application
   */
  override def onHandlerNotFound(request: RequestHeader): Future[play.api.mvc.Result] = {
    Future {
      Ok(views.html.users.contents.errorPage("page not found"))
    }
  }
}
