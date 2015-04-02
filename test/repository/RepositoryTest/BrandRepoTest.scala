package repository.RepositoryTest

import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.Connection
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import model.repository.Brand
import model.repository.{ BrandRepository, MobileRepository, ModelRepository }
import model._
import java.util.Date
import org.scalatest.BeforeAndAfterAll

class BrandRepoTest extends FunSuite  {

  val brand = Brand("nokia",Some(1))

  //Tests Brand Insertion
  test("BrandRepository: insert a brand successfully") {
    running(FakeApplication()) {
      val valueToCompare = Right(Some(1))
      val valueReturned = BrandRepository.insertBrand(brand)
      assert(valueToCompare === valueReturned)
    }
  }

  //Tests duplicate brand insertion

  test("BrandRepository: insert a duplicate brand : unsuccessfull") {
    running(FakeApplication()) {
      //insert first brand
      BrandRepository.insertBrand(brand)
      //insert it again and get the result
      val valueReturned = BrandRepository.insertBrand(brand)
      assert(valueReturned.isLeft)
    }
  }

  //Tests the listing of the inserted Brand

  test("BrandRepository: Lists inserted brands") {
    running(FakeApplication()) {
      //insert first brand
      BrandRepository.insertBrand(brand)
      val valueToCompare = List(brand)
      val valueReturned = BrandRepository.getAllBrands
      assert(valueToCompare === valueReturned)
    }
  }
  //Tests fetching a brand by its id 
  test("BrandRepository: fetch brand by id") {
    running(FakeApplication()) {
   //insert first brand
      BrandRepository.insertBrand(brand)
      val insertedBrandId = 1
      val valueToCompare = Some(brand)
      val valueReturned = BrandRepository.getBrandById(insertedBrandId)
      assert(valueToCompare === valueReturned)
    }
  }

}

