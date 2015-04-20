package viewtemplates;

import org.specs2.mutable._
import model.repository._
import play.api.Play.current
import play.api.cache.Cache
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.mvc.Security
import utils._
import play.api.mvc.Security
import java.util.{ Date, Calendar }
import play.api.test.WithApplication
import controllers.ReverseAdminController
import controllers.ReverseMobileController
import controllers.ReverseAuditController
import controllers.ReverseApplication
import controllers.ReverseAuthController

class TemplateTest extends Specification{
  
  //ReverseAdminController

   "get mobiles URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.requestsList("pending").url must contain("mobiles")

      val javaScriptTest = new controllers.javascript.ReverseAdminController()
      javaScriptTest.requestsList.name must contain("mobiles")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.requestsList("test").toString must contain("mobiles")
    }
   
   "get approve URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.approve("123456789012347","approved") .url must contain("approve")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.approve("123456789012347","approved").toString must contain("approve")
    }
   
   "get proofDemanded URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.proofDemanded("123456789012347","approved") .url must contain("proofDemanded")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.proofDemanded("123456789012347","approved").toString must contain("proofDemanded")
    }
   
   "get pending URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.pending("123456789012347") .url must contain("pending")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.pending("123456789012347").toString must contain("pending")
    }
   
   "get pending URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.changeMobileRegType("123456789012347") .url must contain("changeMobileRegType")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.changeMobileRegType("123456789012347").toString must contain("changeMobileRegType")
    }
   
    "get pending URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.changeMobileRegType("123456789012347") .url must contain("changeMobileRegType")
      
       val javaScriptTest = new controllers.javascript.ReverseAdminController()
      javaScriptTest.changeMobileRegType.name must contain("changeMobileRegType")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.changeMobileRegType("123456789012347").toString must contain("changeMobileRegType")
    }
    
    "get deleteMobile URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.deleteMobile("123456789012347") .url must contain("delete")
      
       val javaScriptTest = new controllers.javascript.ReverseAdminController()
      javaScriptTest.deleteMobile.name must contain("delete")

      val refTest = new controllers.ref.ReverseAdminController()
      refTest.deleteMobile("123456789012347").toString must contain("delete")
    }
    
    "get brandRegisterForm URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.brandRegisterForm .url must contain("brandForm")
      
      val refTest = new controllers.ref.ReverseAdminController()
      refTest.brandRegisterForm.toString must contain("")
    }
     
     "get modelRegistrationForm URL via ReverseAdminController" in new WithApplication {
      val testController = new ReverseAdminController()
      testController.modelRegistrationForm .url must contain("modelRegistrationForm")
      
      val refTest = new controllers.ref.ReverseAdminController()
      refTest.modelRegistrationForm.toString must contain("")
    }
    
    //ReverseMobileController
    
    "get mobileRegistrationForm URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.secureMobileRegistrationForm().url must contain("register-stolen-phone")
      
       val refTest = new controllers.ref.ReverseMobileController()
      refTest.secureMobileRegistrationForm.toString must contain("")
    }
    
     "get mobileRegistrationSecureForm URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.stolenMobileRegistrationForm().url must contain("register-new-phone")
      
      val refTest = new controllers.ref.ReverseMobileController()
      refTest.stolenMobileRegistrationForm.toString must contain("")
    }
     
     "get mobileRegistration URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.saveMobileUser().url must contain("mobileRegistration")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.saveMobileUser().toString must contain("mobileRegistration")
    }
          
     "get checkMobileStatus URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.checkMobileStatus("123456789012347","user") .url must contain("mobileStatus")
      
       val javaScriptTest = new controllers.javascript.ReverseMobileController()
      javaScriptTest.checkMobileStatus.name must contain("checkMobileStatus")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.checkMobileStatus("123456789012347","user").toString must contain("checkMobileStatus")
    }
     
     "get getModels URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.getModels(1) .url must contain("mobileModel")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.getModels(1).toString must contain("getModels")
    }
     
      "get mobileStatus URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.mobileStatus .url must contain("mobileStatusForm")
    }
      
       "get isImeiExist URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.isImeiExist("123456789012347") .url must contain("isImeiExist")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.isImeiExist("123456789012347").toString must contain("isImeiExist")
    }
       
       "get saveBrand URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.saveBrand .url must contain("saveMobileName")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.saveBrand.toString must contain("saveBrand")
    }
       
        "get saveModel URL via ReverseMobileController" in new WithApplication {
      val testController = new ReverseMobileController()
      testController.saveModel .url must contain("createMobileModel")

      val refTest = new controllers.ref.ReverseMobileController()
      refTest.saveModel.toString must contain("saveModel")
    }
        
   //ReverseAuditController
     "get auditPage URL via ReverseAuditController" in new WithApplication {
      val testController = new ReverseAuditController()
      testController.auditPage .url must contain("auditpage")

      val refTest = new controllers.ref.ReverseAuditController()
      refTest.auditPage.toString must contain("auditPage")
    }   
     
     "get timestampsByIMEI URL via ReverseAuditController" in new WithApplication {
      val testController = new ReverseAuditController()
      testController.timestampsByIMEI .url must contain("auditIMEID")

      val refTest = new controllers.ref.ReverseAuditController()
      refTest.timestampsByIMEI.toString must contain("timestampsByIMEI")
    }

     "get allTimestamps URL via ReverseAuditController" in new WithApplication {
      val testController = new ReverseAuditController()
      testController.allTimestamps .url must contain("auditAllRecords")

      val refTest = new controllers.ref.ReverseAuditController()
      refTest.allTimestamps.toString must contain("allTimestamps")
    }
     
     "get registrationRecordsByYear URL via ReverseAuditController" in new WithApplication {
      val testController = new ReverseAuditController()
      testController.registrationRecordsByYear("2015") .url must contain("registrationRecord")

      val refTest = new controllers.ref.ReverseAuditController()
      refTest.registrationRecordsByYear("2015").toString must contain("registrationRecord")
    }

     //ReverseAuthController
     
     "get login URL via ReverseAuthcontroller" in new WithApplication {
      val testController = new ReverseAuthController()
      testController.login().url must contain("login")
      
      val refTest = new controllers.ref.ReverseAuthController()
      refTest.login().toString must contain("login")

    }
     
     "get authenticate URL via ReverseAuthcontroller" in new WithApplication {
      val testController = new ReverseAuthController()
      testController.authenticate().url must contain("authenticate")
      
      val refTest = new controllers.ref.ReverseAuthController()
      refTest.authenticate().toString must contain("authenticate")

    }
     
     "get logout URL via ReverseAuthcontroller" in new WithApplication {
      val testController = new ReverseAuthController()
      testController.logout().url must contain("logout")
      
      val refTest = new controllers.ref.ReverseAuthController()
      refTest.logout().toString must contain("logout")
    }
     
     //ReverseApplication
     
     "get index URL via ReverseApplication" in new WithApplication {
      val testController = new ReverseApplication()
      testController.index().url must contain("")
      
      val refTest = new controllers.ref.ReverseApplication()
      refTest.index().toString must contain("")
    }
     
     "get javascriptRoutes URL via ReverseApplication" in new WithApplication {
      val testController = new ReverseApplication()
      testController.javascriptRoutes().url must contain("")
      
      val refTest = new controllers.ref.ReverseApplication()
      refTest.javascriptRoutes().toString must contain("")
    }
}
