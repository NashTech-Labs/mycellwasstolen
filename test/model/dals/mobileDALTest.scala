package model.dals

 import scala.slick.driver.PostgresDriver._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.FunSuite
import scala.slick.session.Session
import model.dals.MobileDAL._
import model.domains.Domain._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.Connection
import model.domains._

class mobileDALTest extends FunSuite with BeforeAndAfterEach {

  val brand = Brand("nokia", "12-17-2013")
  val model = MobileModels("N72",1)
  val mobileUser = Mobile(
    "gauravs", 1, 2, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454", Some(1))

    
    test("mobileDAL:insert and get mobile name successfully ") {

    running(FakeApplication()) {
      val brandname = MobileDAL.insertMobileName(brand)
      val mobilename = MobileDAL.getMobilesName
      assert(mobilename.head.name === "nokia")
    }
  }
  
  
  test("mobileDAL:insert mobileModel and get models by mobileId successfully") {

    running(FakeApplication()) {
      val mobilemodel = MobileDAL.insertMobileModel(model)
      val modelname = MobileDAL.getMobileModelById(2)
      assert(modelname.head.mobileModel=== "N72")
    }
  }
    
    
  test("mobileDAL: insert mobile record into database successfully") {

    running(FakeApplication()) {
      MobileDAL.insertMobileUser(mobileUser)
      val mobileId = MobileDAL.getMobileRecordByIMEID("12345678901234")
      println("mobileId: " + mobileId.isEmpty)
      assert(mobileId.isEmpty === false)
    }
  }
  
   test("DBLayerTesting: Get User by IMEID number successfully ") {

    running(FakeApplication()) {
      val user = MobileDAL.getMobileRecordByIMEID("12345678901234")
      assert(user.head.email === "gs@gmail.com")
    }
  }

  

  
  test("mobileDAL : getMobileNamesById successfully") {
    running(FakeApplication()) {
      val mobilenamebyId = MobileDAL.getMobileNamesById(2)
      println("getMobileNamesById: " + mobilenamebyId)
      assert(mobilenamebyId.head.name === "nokia")

    }
  }
  test("mobileDAL:getMobileNames") {

    running(FakeApplication()) {

      val mobilename = MobileDAL.getMobilesName()
      assert(mobilename.head.name === "nokia")

    }

  }
  test("mobileDAL:getMobileModelById") {
    running(FakeApplication()) {

      val mobilemodel = MobileDAL.getMobileModelById(1)
      assert(mobilemodel.head.mobileModel === "N72")

    }

  }
  test("mobileDAL:getAllMobilesWithBrandAndModel") {
    running(FakeApplication()) {
      val allmobile = MobileDAL.getAllMobilesWithBrandAndModel(Status.pending.toString)
      println("get all mobile: " + allmobile)
      assert(allmobile.head._1.imeiMeid === mobileUser.imeiMeid)

    }

  }

  
    test("mobileDAL:change mobile status to pending"){
      
      running(FakeApplication()){
        
        
      	val status=MobileDAL.changeStatusToDemandProofByIMEID(mobileUser)
      	println(status)
         assert(status.isLeft===true)
       
       Mobiles.ddl.dropStatements 
      }
    }

  test("mobileDAL:change status to approve by IMEID") {
    running(FakeApplication()) {
      val mobileUser = Mobile(
        "gauravs", 1, 1, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
        "gs@gmail.com", "stolen", Status.approved, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454", Some(1))
      val status1 = MobileDAL.changeStatusToApproveByIMEID(mobileUser)
      val mobile = MobileDAL.getMobileRecordByIMEID(mobileUser.imeiMeid)
      assert(mobile.head.mobileStatus.id === Status.approved.id)
     Mobiles.ddl.dropStatements
    }

  }


   
   test("mobileDAL:change registration type"){
      
      running(FakeApplication()){
         val mobileUser = Mobile(
        "gauravs", 1, 1, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
        "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "gaurav.png", "Sigma", "Sigma454", Some(1))
        
      	val status=MobileDAL.changeStatusToDemandProofByIMEID(mobileUser)
      	println(status)
         assert(status.isLeft===true)
       Mobiles.ddl.dropStatements 
      }
    }
   
   
  
  
}