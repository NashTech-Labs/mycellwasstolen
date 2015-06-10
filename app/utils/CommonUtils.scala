package utils

import java.sql.Timestamp

/**
 * This trait Contains common utilities required to application
 */
trait CommonUtils {
  /**
   * Return SimpleDateFormat in mm/dd/yyyy format
   */
  def utilDate: java.text.SimpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")

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
