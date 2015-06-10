package utils

import com.github.tototoshi.csv._
import java.io._
import java.util.ArrayList
import model.repository._
import utils.StatusUtil.Status
import java.sql.Date
import play.api.Logger
import java.sql.Timestamp

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
    println("----------------" + tableName)
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
      println(result)
      val status = mobileStatus(result(8))
      val registerDate = getSqlDate(result(9))
      MobileRepository.insertMobileUser(Mobile(result(0), result(1).toInt, result(2).toInt,
        result(3), result(4),
        result(5), result(6), result(7), status,
        registerDate, result(10)))
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
      println(result)
      ModelRepository.insertModel(Model(result(Constants.ZERO), result(Constants.ONE).toInt))
    }
    modelReader.close()
  }

  /**
   * Insert data from CSV into Brands table
   */
  private def caseBrands: Unit = {
    val brandReader = CSVReader.open(new FileReader("conf/csv/Brands.csv"))
    val resultIterator = brandReader.iterator

    resultIterator.foreach { result =>
      println(result(0))
      BrandRepository.insertBrand(Brand(result(0)))
    }
    brandReader.close()
  }

  /**
   * Insert data from CSV into Audit table
   */
  private def caseAudits: Unit = {
    val auditReader = CSVReader.open(new FileReader("conf/csv/Audits.csv"))
    println("-----Audits------" + auditReader)
    val resultIterator = auditReader.iterator
    resultIterator.foreach { result =>
      println(result)
      AuditRepository.insertTimestamp(Audit(result(0), Timestamp.valueOf(result(1))))
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
      case ("approved")      => Status(1)
      case ("proofdemanded") => Status(2)
      case ("pending")       => Status(0)
    }
  }
}
