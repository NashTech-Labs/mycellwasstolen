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
      "gs", "nokia", "glaxacy", "12345678901234", new java.sql.Date(new java.util.Date().getTime()), "983131313",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd")
  
  /*test("mobileDAL: insert mobile record into database successfully") {
  
    running(FakeApplication()) {
      val mobileId = MobileDAL.insertMobileUser(mobileUser)
      println("mobileId: " + mobileId)
      assert(mobileId.right.get.get > 0)
    }

  }*/

  /*test("DBLayerTesting: Get User by IMEID number successfully ") {
    
    running(FakeApplication()) {
      val user = MobileDAL.getMobileRecordByIMEID("12345678901234")
      assert(user.head.email === "gs@gmail.com")
    }
  }*/
  
  /*test("mobileDAL:insert and get mobile name successfully "){
    
    running(FakeApplication()){
      val brandname =MobileDAL.insertMobileName(brand)
      val mobilename= MobileDAL.getMobilesName
      println("mobilename:"+ mobilename)
      assert(mobilename.head.name==="nnokia")
    }
  }*/
  
  /*test("mobileDAL:insert mobileModel and get models by mobileId successfully"){
    
    running(FakeApplication()){
      val mobilemodel=MobileDAL.insertMobileModel(model)
      val getmobilemodel=MobileDAL.getMobileModelsById(1)
      println("mobilemodel: " + getmobilemodel)
      assert(getmobilemodel.head.mobileModel==="N72")
    }
  }*/
  test("mobileDAL : getMobileNamesById successfully"){
    running(FakeApplication()){
      val mobilenamebyId=MobileDAL.getMobileNamesById(2)
      println("getMobileNamesById: " + mobilenamebyId)
       assert(mobilenamebyId.head.name==="nokia")
      
    }
  }
}