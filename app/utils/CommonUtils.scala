package utils

trait CommonUtils {

  /**
   * Check valid imei number or not
   * @param imei number of mobile
   * @return true on valid, otherwise false
   */
  def validateImei(imei: String): Boolean = {
    val arr = imei.map(f => f.toString().toInt).toArray
    val len = arr.length
    val checksum = arr(len - 1)
    if (len != 15) { false }
    var mul = 2
    var sum = 0
    var i = len - 2
    while (i >= 0) {
      if ((arr(i) * mul) >= 10) {
        sum += ((arr(i) * mul) / 10) + ((arr(i) * mul) % 10)
        i = i - 1
      } else {
        sum += arr(i) * mul
        i = i - 1
      }
      if (mul == 2) mul = 1 else mul = 2
    }
    var m10 = sum % 10
    if (m10 > 0) { m10 = 10 - m10 }
    if (m10 == checksum) { true }
    else
      false
  }

  def utilDate: java.text.SimpleDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy")

  def getSqlDate(): java.sql.Date = {
    val currentDate = utilDate.format(new java.util.Date())
    new java.sql.Date(utilDate.parse(currentDate).getTime())
  }

  def getSqlDate(date: String): java.sql.Date = {
    new java.sql.Date(utilDate.parse(date).getTime())
  }
}
object CommonUtils extends CommonUtils
