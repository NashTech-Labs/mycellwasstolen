package repository.RepositoryTest

import org.scalatest.FunSuite
import model.repository.{Brand,Model}
import model.repository.{ BrandRepository, ModelRepository }
import play.api.test.Helpers._
import utils.StatusUtil.Status
import play.api.test.FakeApplication

/**
  * @author knoldus
 */
class ModelRepoTest extends FunSuite {
  val brand = Brand("nokia", Some(1))
  val model = Model("N72", 1, Some(1))
  
  //Test model insertion
  test("ModelRepository :insert a model successfully ") {
    running(FakeApplication()) {
     //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      val valueReturned = ModelRepository.insertModel(model)
      assert(valueReturned === Right(Some(1)))
    }
  } 
  
  //Tests duplicate model insertion
  test("ModelRepository :insert a duplicate model") {
    running(FakeApplication()) {
     //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert first model
      ModelRepository.insertModel(model)
      //insert duplicate model and get the return result
      val valueReturned = ModelRepository.insertModel(model)
      assert(valueReturned.isLeft)
    }
  }
  
  //Tests fetching model by its imeid_number
  test("ModelRepository :fetch a model by its Id") {
    running(FakeApplication()) {
      val insertedModelId = 1
     //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert first model
      ModelRepository.insertModel(model)
      //insert duplicate model and get the return result
      val valueReturned = ModelRepository.getModelById(insertedModelId)
      assert(valueReturned === Some(model))
    }
  }
  
  //Tests fetching list of Models my a brand id
  
  test("ModelRepository: fetch the list of models by a brand"){
    running(FakeApplication()) {
      val insertedBrandId = 1
     //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert first model
      ModelRepository.insertModel(model)
      //insert duplicate model and get the return result
      val valueReturned = ModelRepository.getAllModelByBrandId(insertedBrandId)
      assert(valueReturned === List(model))
    }
  }
}

