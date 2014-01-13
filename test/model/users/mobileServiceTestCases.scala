package model.users

import org.scalatest.FunSuite
import model.domains.Domain._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.Logger
import org.scalatest.BeforeAndAfter

class MobileServiceTestCases extends FunSuite {

  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand=Brand("nokia",date)
  val model=MobileModels("N72",6)
  val mobileUser = Mobile(
      "gbs", 1, 5, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd","12-17-2013","gaurav.png","Sigma","Sigma454")
      
  test("mobileService: insert mobile record into database successfully") {
  
    running(FakeApplication()) {
      val mobileId = MobileService.mobileRegistration(mobileUser)
      println("mobileId: " + mobileId)
      assert(mobileId.isRight)
    }
}
  
 test("getMobileRecordByIMEID: getMobileRecordByIMEID successfully"){
  
   running(FakeApplication())
   {
     val  mobilerecordbyid=MobileService.getMobileRecordByIMEID("12345678901234")
     println("mobilerecordbyid:"+ mobilerecordbyid)
     assert(mobilerecordbyid.get.email==="gs@gmail.com")
   }
}
  
   test("getMobilesName: getMobilesName successfully"){
  
   running(FakeApplication())
   {
     val  mobileName=MobileService.getMobilesName()
     println("mobileName:"+ mobileName)
     assert(mobileName.head.name==="nokia")
   }
  }
      
   test("getMobileModelsById: getMobileModelsById successfully"){
  
   running(FakeApplication())
   {
     val  mobileModel=MobileService.getMobileModelsById(1)
     println("mobileModel:"+ mobileModel)
     assert(mobileModel.head.mobileModel==="N72")
   }
  }
      
   test("isImeiExist: isImeiExist successfully"){
  
   running(FakeApplication())
   {
     val  isImeiExist=MobileService.isImeiExist("12345678901234")
     println("isImeiExist:"+ isImeiExist)
     assert(isImeiExist.==(false))
   }
  }  
      

    test("addMobileName: addMobileName successfully"){
  
   running(FakeApplication())
   {
     val  addMobileName=MobileService.addMobileName(brand)
     println("addMobileName:"+ addMobileName)
     assert(addMobileName.isLeft)
   }
  }
      
      test("createMobileModel: createMobileModel successfully"){
  
   running(FakeApplication())
   {
     val  createMobileModel=MobileService.createMobileModel(model)
     println("createMobileModel:"+ createMobileModel)
     assert(createMobileModel.isRight)
   }
  }
}