package repository.MobileRepositoryTest

import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.Connection
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import model.repository.Brand
import model.repository.Mobile
import model.repository.Model
import utils.StatusUtil.Status
import model.repository.MobileRepository
import model.repository.BrandRepository
import model.repository.ModelRepository

/**
 * Class MobileRepoTest: Unit tests the methods in MobileRepository.
 */

class MobileRepoTest extends FunSuite with BeforeAndAfterEach with MobileRepository with BrandRepository with ModelRepository {

  val brand = Brand("nokia", "12-17-2013")
  val model = Model("N72", 1)
  val mobileUser = Mobile(
    "gauravs", 1, 2, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454", Some(1))

  //Mobile Insertion Test 
  test("MobileRepository:insert and get mobile name successfully ") {
    running(FakeApplication()) {
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      assert(insertedMobile === Right(Some(1)))
    }
  }
  
  //Insert A User with Duplicate Email
  test("MobileRepository:insert fails since email is duplicate ") {
    running(FakeApplication()) {
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Insert it again to test duplicate entry 
      val insertedDuplicateMobile = MobileRepository.insertMobileUser(mobileUser)
      assert(insertedDuplicateMobile.isLeft)
    }
  }
  
  test("MobileRepository: get Mobile by IMEID ") {
    running(FakeApplication()) {
    	val imeiInserted = "12345678901234" 
      MobileRepository.insertMobileUser(mobileUser)
      val insertedMobile = mobileUser
      val mobileUserToCompareWith = MobileRepository.getMobileUserByIMEID(imeiInserted)
      assert(mobileUser=== mobileUserToCompareWith.get)
    }

  }

}