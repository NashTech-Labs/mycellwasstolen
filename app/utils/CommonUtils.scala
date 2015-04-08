package utils

import java.sql.Timestamp

/**
 * This trait Contains common utilities required to application
 */
trait CommonUtils {

  /**
   * Check valid imei number or not
   * @param imei number of mobile
   * @return true on valid, otherwise false
   */

  def validateImei(imei: String): Boolean = {
    val result = (imei.reverse.map { _.toString.toShort }.grouped(2) map
      { t => t(0) + (if (t.length > 1) (t(1) * 2) % 10 + t(1) / 5 else 0) }).sum % 10
    if (result == 0) {
      true
    } else {
      false
    }
  }

  /**
   * Return SimpleDateFormat in mm/dd/yyyy format
   */
  def utilDate: java.text.SimpleDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy")

  /**
   * Return current sql date
   */
  def getSqlDate(): java.sql.Date = {
    val currentDate = utilDate.format(new java.util.Date())
    new java.sql.Date(utilDate.parse(currentDate).getTime())
  }

  /**
   * Convert string to sql date
   * @param date, string in "MM/dd/yyyy" format
   */
  def getSqlDate(date: String): java.sql.Date = {
    new java.sql.Date(utilDate.parse(date).getTime())
  }
}
object CommonUtils extends CommonUtils
