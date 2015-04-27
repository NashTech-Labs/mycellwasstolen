package utils
import java.io.File
import java.io.FileReader
import java.sql.Timestamp

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat

import model.repository.Audit
import model.repository.AuditRepository
import model.repository.Mobile
import model.repository.MobileRepository
import model.repository.Model
import model.repository.ModelRepository
import play.api.Logger
import utils.StatusUtil.Status
/**
 * Value of table names to be used for scenario of matching
 */

object TableEnums extends Enumeration {
  val BRANDS, MODELS, MOBILES, AUDITS = Value
}

/**
 *  CSV Reader for files that are to be imported into DB
 */
object ReadCSV extends CommonUtils {
  import TableEnums._
  /**
   * Read CSV and insert entries to the DB based on the table name
   * @param file:File, tableName:String
   * @return Unit
   */
  def convert(file: File, tableName: TableEnums.Value): Unit = {
    
    Logger("ailu re")
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
  private def caseMobile: Unit = {
    val mobilesReader = CSVReader.open(new FileReader("conf/csv/Mobiles.csv"))
    val resultIterator = mobilesReader.iterator
    resultIterator.foreach { result =>
      val status = mobileStatus(result(Constants.NINE))
      val registerDate = getSqlDate(result(Constants.ELEVEN))
      MobileRepository.insertMobileUser(Mobile(result(Constants.ZERO), result(Constants.ONE).toInt, result(Constants.TWO).toInt,
        result(Constants.THREE), result(Constants.FOUR), result(Constants.SIX), result(Constants.SEVEN), result(Constants.EIGHT), status,
        registerDate,result(Constants.TWELVE)))
    }
    mobilesReader.close()
  }

  /**
   * Insert data from CSV into Models table
   */
  private def caseModels: Unit = {
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
  private def caseBrands: Unit = {
    val auditReader = CSVReader.open(new FileReader("conf/csv/Brands.csv"))
    val resultIterator = auditReader.iterator
    resultIterator.foreach { result =>
      AuditRepository.insertTimestamp(Audit(result(Constants.ONE), Timestamp.valueOf(result(Constants.ZERO))))
    }
    auditReader.close()
  }

  /**
   * Insert data from CSV into Audit table
   */
  private def caseAudits: Unit = {
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
