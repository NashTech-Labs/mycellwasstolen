package model.dals

import org.scalatest.FunSuite
import model.domains.Domain._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.Logger
import org.scalatest.BeforeAndAfter

class mobileDALTest extends FunSuite{
  
  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand=Brand("nokia",date)
  val model=MobileModels("N72",1)
  val mobileUser = Mobile(
      "gauravs", "nokia", "glaxacy", "12345678901234", "12-05-2013", "+91 9839839830",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd","12-17-2013","gaurav.png","Sigma","Sigma454")
  
  test("mobileDAL: insert mobile record into database successfully") {
  
    running(FakeApplication()) {
      val mobileId = MobileDAL.insertMobileUser(mobileUser)
      println("mobileId: " + mobileId)
      assert(mobileId.right.get.get > 0)
    }

  }

  test("DBLayerTesting: Get User by IMEID number successfully ") {
    
    running(FakeApplication()) {
      val user = MobileDAL.getMobileRecordByIMEID("12345678901234")
      assert(user.head.email === "gs@gmail.com")
    }
  }
  
  test("mobileDAL:insert and get mobile name successfully "){
    
    running(FakeApplication()){
      val brandname =MobileDAL.insertMobileName(brand)
      val mobilename= MobileDAL.getMobilesName
      println("mobilename:"+ mobilename)
      assert(mobilename.head.name==="nnokia")
    }
  }
  
  test("mobileDAL:insert mobileModel and get models by mobileId successfully"){
    
    running(FakeApplication()){
      val mobilemodel=MobileDAL.insertMobileModel(model)
      val getmobilemodel=MobileDAL.getMobileModelsById(1)
      println("mobilemodel: " + getmobilemodel)
      assert(getmobilemodel.head.mobileModel==="N72")
    }
  }
  test("mobileDAL : getMobileNamesById successfully"){
    running(FakeApplication()){
      val mobilenamebyId=MobileDAL.getMobileNamesById(2)
      println("getMobileNamesById: " + mobilenamebyId)
       assert(mobilenamebyId.head.name==="nokia")
      
    }
  }
}