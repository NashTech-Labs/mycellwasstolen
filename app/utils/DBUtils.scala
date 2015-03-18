package utils
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver

/**
 * Provides commonly used database utilities 
 */
object DBUtils {

  /**
 *  Provides all types of Status
 */
 object Status extends Enumeration {
  val pending = Value("pending")
  val approved = Value("approved")
  val proofdemanded = Value("proofdemanded")
}
 
  /**
   * Maps the status to a slick Column Type
   */
  implicit val mobileStatusMapper = MappedColumnType.base[Status.Value, String](
    { enuStatus => enuStatus.toString() },
    {
      strStatus =>
        strStatus match {
          case "pending"       => Status(0)
          case "approved"      => Status(1)
          case "proofdemanded" => Status(2)
        }
    })

}