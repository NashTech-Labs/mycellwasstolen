package model.repository

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
import utils.Connection
import model.repository._
import play.api.Logger
import java.util.Date
import scala.slick.lifted.ForeignKeyQuery

trait ModelRepository extends ModelTable {

  /**
   * Getting mobile model
   * @param id, id of model
   * @return object of model
   */
  def getModelById(id: Int): Option[Model] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getModelById" + id)
      models.filter(_.id === id).firstOption
    }
  }

  /**
   * Getting all models of particular brand
   * @param id, id of model
   * @return list of model
   */
  def getAllModelByBrandId(brandid: Int): List[Model] = {
    Connection.databaseObject().withSession { implicit session: Session =>
      Logger.info("Calling getModelById" + brandid)
      models.filter(_.brandId === brandid).list
    }
  }

  /**
   * Inserts new mobile model
   * @param model, object of Model
   * @return id of newly inserted model
   */
  def insertModel(modell: Model): Either[String, Option[Int]] = {
    try {
      Connection.databaseObject().withSession { implicit session: Session =>
        Logger.info("Called insertModel")
        Right(autoKeyModels.insert(modell))
      }
    } catch {
      case ex: Exception =>
        Logger.info("Error in insert user" + ex.printStackTrace())
        Left(ex.getMessage())
    }
  }
}

trait ModelTable extends BrandTable {
  private[repository] class Models(tag: Tag) extends Table[Model](tag, "models") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def brandId: Column[Int] = column[Int]("brandId")
    def name: Column[String] = column[String]("modelName", O DBType ("VARCHAR(30)"))
    def * : scala.slick.lifted.ProvenShape[Model] = (
      name, brandId, id) <> (Model.tupled, Model.unapply)
    def fkey = foreignKey("brand_FK", brandId, brands)(_.id.get, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)
    def modelIndex: scala.slick.lifted.Index = index("idx_model", (name), unique = true)
  }
  val models = TableQuery[Models]
  val autoKeyModels = models returning models.map(_.id)
}

case class Model(
  name: String,
  brandId: Int,
  id: Option[Int] = None)

case class ModelForm(
  brandName: String,
  modelName: String)

//Trait companion object
object ModelRepository extends ModelRepository
