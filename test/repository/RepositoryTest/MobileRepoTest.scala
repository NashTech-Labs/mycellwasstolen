package repository.RepositoryTest

import org.scalatest.FunSuite
import model.repository.{ Brand, Mobile, Model }
import model.repository.{ BrandRepository, MobileRepository, ModelRepository }
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.StatusUtil.Status

/**
 * Class MobileRepoTest: Unit tests the methods in MobileRepository.
 */

class MobileRepoTest extends FunSuite with MobileRepository with BrandRepository with ModelRepository {
  val imeiInserted = "12345678901234"
  val brand = Brand("nokia", Some(1))
  val model = Model("N72", 1)
  val mobileUser = Mobile(
    "gauravs", 1, 1, "12345678901234", "12345678902134", new java.sql.Date(121230), "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas asd", new java.sql.Date(121230), "gaurav.png", "Sigma", "Sigma454", Some(1))

  //Mobile Insertion Test 
  test("MobileRepository:insert and get mobile name successfully ") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      assert(insertedMobile === Right(Some(1)))
    }
  }

  //Tests the insertion of a MobileUser with Duplicate Email
  test("MobileRepository:insert fails since email is duplicate ") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Insert it again to test duplicate entry 
      val insertedDuplicateMobile = MobileRepository.insertMobileUser(mobileUser)
      println(insertedDuplicateMobile)
      assert(insertedDuplicateMobile.isLeft)
    }
  }

  //Test the fetching of Mobile Record by an IMEID
  test("MobileRepository: get Mobile by IMEID ") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      MobileRepository.insertMobileUser(mobileUser)
      val mobileUserToCompareWith = MobileRepository.getMobileUserByIMEID(imeiInserted)
      assert(mobileUserToCompareWith.isEmpty==false)
    }
  }

  //Test the status change from Pending to Approved
  test("MobileRepository: change status from Pending to Approved and must return Right(1)") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Changes its status
      val returnValueOnChange = MobileRepository.changeStatusToApproveByIMEID(imeiInserted)
      assert(returnValueOnChange === Right(1))
    }
  }

  //Test the status change of Mobile from pending to DemandProof
  test("MobileRepository: change of Mobile from pending to DemandProof: must return Right(1)") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Changes its status
      val returnValueOnChange = MobileRepository.changeStatusToDemandProofByIMEID(imeiInserted)
      assert(returnValueOnChange === Right(1))
    }
  }

  //Test the status change of Mobile mobile registration (stolen or clean) 
  test("MobileRepository: Change registration type (Stolen or Clean): must return Right(1)") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Changes its status
      val returnValueOnChange = MobileRepository.changeRegTypeByIMEID(mobileUser)
      assert(returnValueOnChange === Right(1))
    }
  }

  //Test the Retrieval all mobile user with brand and model based on status 
  test("MobileRepository: Retrieval all mobile user with brand and model") {
    running(FakeApplication()) {
      //insert a brand record
      BrandRepository.insertBrand(Brand("Sigma", Some(1)))
      //Insert a Model Record
      ModelRepository.insertModel((Model("Sigma454", 1)))
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser((mobileUser))
      //Insert a Brand Record
      val returnValueOnChange = MobileRepository.getAllMobilesUserWithBrandAndModel("pending")
      assert(returnValueOnChange.isEmpty==false)
    }
  }

  //Test the deletion of mobile user 
  test("MobileRepository: delete a mobile user record") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      val returnValueOnChange = MobileRepository.deleteMobileUser(imeiInserted)
      assert(returnValueOnChange === Right(1))
    }
  }

  //Tests change status to Pending by IMEID
  test("MobileRepository: change of Mobile from pending to pending: must return Right(1)") {
    running(FakeApplication()) {
      //Insert a brand to avoid FK violation while inserting mobile record
      BrandRepository.insertBrand(brand)
      //Insert a model to avoid FK violation while inserting mobile record
      ModelRepository.insertModel(model)
      //Insert a Mobile Record first
      val insertedMobile = MobileRepository.insertMobileUser(mobileUser)
      //Changes its status
      val returnValueOnChange = MobileRepository.changeStatusToPendingByIMEID(imeiInserted)
      assert(returnValueOnChange === Right(1))
    }
  }  
}
