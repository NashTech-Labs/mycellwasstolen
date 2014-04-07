package model.users

import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.specs2.mock.Mockito

import model.dals.MobileDALComponent
import model.domains.Domain.Brand
import model.domains.Domain.Mobile
import model.domains.Domain.MobileModels
import model.domains.Domain.Status
import model.users.MobileService.mobileRegistration

class mobileServiceTest extends FunSuite with Mockito {

  val mobileUser = Mobile(
    "gs", 1, 5, "12345678901234", "123456789012678", "12-05-2013", "+91 9839839830",
    "gs@gmail.com", "stolen", Status.pending, "ddas  asd", "12-17-2013", "harshita.png", "Sigma", "Sigma454")

  val date = new java.sql.Date(new java.util.Date().getTime())
  val brand = Brand("nokia", "12-17-2013")
  val model = MobileModels("N72", 6)
<<<<<<< HEAD
  
    val mockedMobileDALObject = mock[MobileDALComponent]
=======
 
  val mockedMobileDALObject = mock[MobileDALComponent]
>>>>>>> 20208b56f8ce266c03704805c79f25803e384b12

  val mobileService = new MobileService(mockedMobileDALObject)

  test("Testing: register mobile successfully ") {

    when(mockedMobileDALObject.insertMobileUser(mobileUser)).thenReturn(Right(model.id))
    val regMobile = mobileService.mobileRegistration(mobileUser)
    assert(regMobile.isRight === true)

  }

  test("Testing: get mobile record") {
    when(mockedMobileDALObject.getMobileRecordByIMEID("12345678901234")).thenReturn(List(mobileUser))
    val result = mobileService.getMobileRecordByIMEID("12345678901234")
    assert(result.head === mobileUser)
  }

  test("Testing: mobile names ") {

    when(mockedMobileDALObject.getMobilesName).thenReturn(List(brand))
    val result = mobileService.getMobilesName
    assert(result.length >= 1)
  }

  test("Testing: get Mobile Models By Id successfully") {

    when(mockedMobileDALObject.getMobileModelsById(1)).thenReturn(List(model))
    val result = mobileService.getMobileModelsById(1)
    assert(result != None)
  }

  test("Testing: get Mobile Names By Id successfully") {
    when(mockedMobileDALObject.getMobileNamesById(1)).thenReturn(List(brand))
    val result = mobileService.getMobileNamesById(1)
    assert(brand.name === "nokia")

  }

  test("Testing: is Imei Exist") {
    when(mockedMobileDALObject.getMobileRecordByIMEID("12345678901234")).thenReturn(List(mobileUser))
    val result = mobileService.isImeiExist("12345678901234")
    assert(result === true)

  }
  
  test("Testing: is Imei does not Exist") {
    when(mockedMobileDALObject.getMobileRecordByIMEID("12349999999999")).thenReturn(List())
    val result = mobileService.isImeiExist("12349999999999")
    assert(result === false)

  }
  
  
  test("Testing: add Mobile Name") {
    when(mockedMobileDALObject.insertMobileName(brand)).thenReturn(Right(model.id))
    val result = mobileService.addMobileName(brand)
    assert(result.isRight === true)
  }
  
 
  test("Testing: create Mobile Model") {
    when(mockedMobileDALObject.insertMobileModel(model)).thenReturn(Right(model.id))
    val result = mobileService.createMobileModel(model)
    assert(result.isRight === true)
  }
<<<<<<< HEAD

  test("Testing: get All Mobiles With Brand And Model") {
=======
 /* 
  test("Testing:does not create Mobile Model") {
    when(mockedMobileDALObject.insertMobileModel(model)).thenReturn(Left("mobile model cannot be inserted"))
    val result = mobileService.createMobileModel(model)
    assert(result.isLeft === "error" )
  }
 */ 
   test("Testing: get All Mobiles With Brand And Model") {
>>>>>>> 20208b56f8ce266c03704805c79f25803e384b12
    when(mockedMobileDALObject.getAllMobilesWithBrandAndModel("pending")).thenReturn(List())
    val result = mobileService.getAllMobilesWithBrandAndModel("pending")
    assert(result != None)
  }
   
   test("Testing: change  Status To Approve By IMEID") {
    when(mockedMobileDALObject.changeStatusToApproveByIMEID(mobileUser))thenReturn(Right(1))
    val result = mobileService.changeStatusToApprove(mobileUser)
    assert(result === true)
  }
   
    test("Testing: does not change  Status To Approve By IMEID") {
    when(mockedMobileDALObject.changeStatusToApproveByIMEID(mobileUser))thenReturn(Left("error"))
    val result = mobileService.changeStatusToApprove(mobileUser)
    assert(result === false)
  }
   
   
   test("Testing: change Status To Demand Proof") {
    when(mockedMobileDALObject.changeStatusToDemandProofByIMEID(mobileUser))thenReturn(Right(1))
    val result = mobileService.changeStatusToDemandProof(mobileUser)
    assert(result === true)
  }
   
   test("Testing: does not change Status To Demand Proof") {
    when(mockedMobileDALObject.changeStatusToDemandProofByIMEID(mobileUser))thenReturn(Left("error"))
    val result = mobileService.changeStatusToDemandProof(mobileUser)
    assert(result === false)
  }
   
   test("Testing: get Mobile Model By Id") {
    when(mockedMobileDALObject.getMobileModelById(1))thenReturn(List(model))
    val result = mobileService.getMobileModelById(1)
    assert(result === Some(model))
  }
   
   test("Testing: change Reg Type By IMEID") {
    when(mockedMobileDALObject.changeRegTypeByIMEID(mobileUser))thenReturn(Right(1))
    val result = mobileService.changeRegTypeByIMEID(mobileUser)
    assert(result=== true)
  }
   
   test("Testing: does not change Reg Type By IMEID") {
    when(mockedMobileDALObject.changeRegTypeByIMEID(mobileUser))thenReturn(Left("error"))
    val result = mobileService.changeRegTypeByIMEID(mobileUser)
    assert(result=== false)
  }
   
   test("Testing: change Status To Pending") {
    when(mockedMobileDALObject.changeStatusToPendingByIMEID(mobileUser))thenReturn(Right(1))
    val result = mobileService.changeStatusToPending(mobileUser)
    assert(result=== true)
  }
   
   test("Testing: does not change Status To Pending") {
    when(mockedMobileDALObject.changeStatusToPendingByIMEID(mobileUser))thenReturn(Left("error"))
    val result = mobileService.changeStatusToPending(mobileUser)
    assert(result=== false)
  }
   
}