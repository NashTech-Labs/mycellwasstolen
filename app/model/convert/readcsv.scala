package model.convert

import com.github.tototoshi.csv._
import java.io._
import java.util.ArrayList
import model.repository._
import utils.StatusUtil.Status
import java.sql.Date
import utils.CommonUtils
import play.api.Logger
import java.sql.Timestamp
import utils.Constants

/**
 * Value of table names to be used for scenario of matching
 */
object TablesEnum extends Enumeration {
  val BRANDS, MODELS, MOBILES, AUDITS = Value
}
/**
 *  CSV Reader for files that are to be imported into DB
 */

object ReadCsv extends CommonUtils {
  import TablesEnum._
  /**
   * Read CSV and insert entries to the DB based on the table name
   * @param file:File, tableName:String
   * @return Unit
   */
  def convert(file: File, tableName: TablesEnum.Value): Unit = {
    tableName match {
      case `BRANDS` => {
        caseBrands
      }
      case `MODELS` => {
        caseModels
      }

      case `MOBILES` => {
        caseMobile
      }
      case `AUDITS` => {
        caseAudits
      }
      case _ =>
        Logger.info("Table with this name does not exists in the system. Please check again")
        None
    }
  }

  /**
   * Inserts data into Mobile table
   */
  def caseMobile: Unit = {
    val mobilesReader = CSVReader.open(new FileReader("conf/csv/Mobiles.csv"))
    val resultIterator = mobilesReader.iterator
    resultIterator.foreach { result =>
      val status = mobileStatus(result(Constants.NINE))
      val purchaseDate = getSqlDate(result(Constants.FIVE))
      val registerDate = getSqlDate(result(Constants.ELEVEN))
      MobileRepository.insertMobileUser(Mobile(result(Constants.ZERO), result(Constants.ONE).toInt, result(Constants.TWO).toInt,
          result(Constants.THREE), result(Constants.FOUR),
        purchaseDate, result(Constants.SIX), result(Constants.SEVEN), result(Constants.EIGHT), status,
        result(Constants.TEN), registerDate, result(Constants.TEN),
        result(Constants.THIRTEEN), result(Constants.FOURTEEN)))
    }
    mobilesReader.close()
  }

  /**
   * Insert data from CSV into Models table
   */

  def caseModels: Unit = {
    val modelReader = CSVReader.open(new FileReader("conf/csv/Models.csv"))
    val resultIterator = modelReader.iterator
    resultIterator.foreach { result =>
      ModelRepository.insertModel(Model(result(Constants.ONE), result(Constants.TWO).toInt))
    }
    modelReader.close()
  }

  /**
   * Insert data from CSV into Brands table
   */
  def caseBrands: Unit = {
    val auditReader = CSVReader.open(new FileReader("conf/csv/Audits.csv"))
    val resultIterator = auditReader.iterator
    resultIterator.foreach { result =>
      AuditRepository.insertTimestamp(Audit(result(Constants.ONE), Timestamp.valueOf(result(Constants.ZERO))))
    }
    auditReader.close()
  }

  /**
   * Insert data from CSV into Audit table
   */

  def caseAudits: Unit = {
    val auditReader = CSVReader.open(new FileReader("conf/csv/Audits.csv"))
    val resultIterator = auditReader.iterator
    resultIterator.foreach { result =>
      AuditRepository.insertTimestamp(Audit(result(Constants.ONE), Timestamp.valueOf(result(Constants.ZERO))))
    }
    auditReader.close()
  }

  /**
   * Get the status of mobile registration request
   * @param status:String
   * @return Status.Value (approved, proof demanded, pending)
   */

  private def mobileStatus(status: String) = {
    status match {
      case ("approved")      => Status(Constants.ONE)
      case ("proofdemanded") => Status(Constants.TWO)
      case ("pending")       => Status(Constants.ZERO)
    }

  }
}
