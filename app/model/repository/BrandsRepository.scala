package model.repository
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import scala.slick.driver.PostgresDriver.simple._

trait BrandRepository extends BrandTable {
  //provide concrete implementation for all methods here
  def getMobilesName: List[Brand]
  def getMobileNamesById(id: Int): List[Brand]
}
trait BrandTable {
  private[BrandTable] class Brands(tag: Tag) extends Table[Brand](tag, "brands") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O DBType ("VARCHAR(30)"))
    def date: Column[String] = column[String]("date", O.NotNull)
    def * : scala.slick.lifted.ProvenShape[Brand] = (name, date, id) <> (Brand.tupled, Brand.unapply)
  }
  
  val brands = TableQuery[Brands]
  val autoKeyBrands = brands returning brands.map(_.id)
}

case class Brand(
  name: String,
  date: String,
  id: Option[Int] = None)
  
  //Trait companion object
