package model.repository
 import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import utils.Connection
import model.repository._
import play.api.Logger
import java.sql.Date

trait BrandRepository extends BrandTable {

  /**
   * Returns List of mobile Brands
   */
  def getAllBrands: List[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getAllBrands")
      brands.list
    }
  }

  /**
   * Inserts a new Brand record
   * @param Brand, Object of Brand
   * @return id of new inserted brand
   */
  def insertBrand(brand: Brand): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insertBrand")
        Right(autoKeyBrands.insert(brand))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert mobile name" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }

  /**
   *  Returns object of mobile Brand
   *  @param id, id of brand
   */
  def getBrandById(id: Int): Option[Brand] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getMobileNameById" + id)
      brands.filter(_.id === id).firstOption
    }
  }
}
trait BrandTable {
  private[repository] class Brands(tag: Tag) extends Table[Brand](tag, "brands") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name", O DBType ("VARCHAR(30)"))
    def * : scala.slick.lifted.ProvenShape[Brand] = (name, id) <> (Brand.tupled, Brand.unapply)
    def brandIndex: scala.slick.lifted.Index = index("idx_brand", (name), unique = true)
  }

  val brands = TableQuery[Brands]
  val autoKeyBrands = brands returning brands.map(_.id)
}

case class Brand(
  name: String,
  id: Option[Int] = None)

//Represents a brand name
case class BrandForm(name: String)

//Trait companion object
object BrandRepository extends BrandRepository
