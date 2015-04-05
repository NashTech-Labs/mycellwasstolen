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

/**
 * Value of table names to be used for scenario of matching
 */
object TablesEnum extends Enumeration {
  val BRANDS, MODELS, MOBILES, AUDITS = Value
}
/**
 *  CSV Reader for files that are to be imported into DB
 */

object readcsv extends CommonUtils {
  import TablesEnum._
  /**
   * Read CSV and insert entries to the DB based on the table name
   * @param file:File, tableName:String
   * @return Unit
   */
  def convert(file: File, tableName: TablesEnum.Value): Unit = {

    tableName match {

      case `BRANDS` =>

        val brandReader = CSVReader.open(new FileReader("conf/csv/Brands.csv"))
        val brands = brandReader.allWithHeaders().toList.flatMap(brand => brand.get("name"))

        brands.foreach { _brand =>
          BrandRepository.insertBrand(Brand(_brand))
        }
        brandReader.close()
      case `MODELS` =>

        val modelReader = CSVReader.open(new FileReader("conf/csv/Models.csv"))
        val resultIterator = modelReader.iterator
        resultIterator.foreach { result =>
          ModelRepository.insertModel(Model(result(1), result(2).toInt))
        }

      case `MOBILES` =>
        val mobilesReader = CSVReader.open(new FileReader("conf/csv/Mobiles.csv"))
        val resultIterator = mobilesReader.iterator
        resultIterator.foreach { result =>
          val status = mobileStatus(result(9))
          val purchaseDate = getSqlDate(result(5))
          val registerDate = getSqlDate(result(11))

          MobileRepository.insertMobileUser(Mobile(result(0), result(1).toInt, result(2).toInt, result(3), result(4),
            purchaseDate, result(6), result(7), result(8), status,
            result(10), registerDate, result(12),
            result(13), result(14)))
        }

      case `AUDITS` =>
        val auditReader = CSVReader.open(new FileReader("conf/csv/Audit.csv"))
        val resultIterator = auditReader.iterator
        resultIterator.foreach { result =>
          AuditRepository.insertTimestamp(Audit(result(1), Timestamp.valueOf(result(0))))

        }

      case _ =>
        Logger.info("Table with this name does not exists in the system. Please check again")
        None

    }
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
