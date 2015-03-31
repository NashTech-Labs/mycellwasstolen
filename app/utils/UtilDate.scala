package utils

trait UtilDate {
  def utilDate = new java.text.SimpleDateFormat("MM/dd/yyyy")

  def getDate() = {
    val currentDate = utilDate.format(new java.util.Date())
    new java.sql.Date(utilDate.parse(currentDate).getTime())
  }
  
  def getDate(date:String) = {
    new java.sql.Date(utilDate.parse(date).getTime())
  }
}

object UtilDate extends UtilDate
