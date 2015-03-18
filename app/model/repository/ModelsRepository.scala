package model.repository
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver
import scala.slick.lifted.ProvenShape
trait ModelRepository extends ModelTable {
  //provide concrete implementation for all methods here
  def getMobileModelsById(id: Int): List[MobileModels]
  def insertMobileModel(mobilemodel: MobileModels): Either[String, Option[Int]]
}

trait ModelTable extends BrandTable {

  private[ModelTable] class Models(tag: Tag) extends Table[MobileModels](tag, "mobilesmodel") {
    def id: Column[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def brandId: Column[Int] = column[Int]("brandId")
    def modelName: Column[String] = column[String]("modelName", O DBType ("VARCHAR(30)"))
    def * : scala.slick.lifted.ProvenShape[MobileModels] = (
      modelName, brandId, id) <> (MobileModels.tupled, MobileModels.unapply)
    def mobilebrand: Object = foreignKey("SUP_FK", brandId, brands)(_.id.get, onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Cascade)
  }

  val mobileModel = TableQuery[Models]
  val autoKeyModels = mobileModel returning mobileModel.map(_.id)
}

case class MobileModels(
  mobileModel: String,
  mobileName: Int,
  id: Option[Int] = None)
  
//Trait companion object
