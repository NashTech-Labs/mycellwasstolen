package Router


import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Security
import play.api.cache.Cache
import model.domains.Domain._
import model.dals.MobileDAL
import model.users.MobileService
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Play.current

class RouterTest extends Specification{
  val date = new java.sql.Date(new java.util.Date().getTime())
   val brand=Brand("nokia","12-17-2013")
  val model=MobileModels("N72",1)
  val mobileUser = Mobile(
      "gauravs", 1, 5, "12345678901234", "12345678902134", "12-05-2013", "+91 9839839830",
      "gs@gmail.com","stolen",Status.pending, "ddas  asd","12-17-2013","gaurav.png","Sigma","Sigma454")
  

    /**
  * Home page
  */
  
    

  "respond to the index Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

 
  "redirect to contact us" in {

    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/contact-us"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }
  
  
  "redirect to blog" in {

    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/blog"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }
  }

  "get show example form" in{
  
running(FakeApplication()){
  
  val Some(result)=route(FakeRequest(GET,"/showExampleForm"))
  status(result) must equalTo(OK)
  contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
  
  
} 
  
}
  "post show example form" in{
    
    running(FakeApplication()){
      val Some(result)=route(FakeRequest(POST,"/handleFormExample"))
      
      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
  
      
      
    }
    
  }
  
  /**
   * Registration
   */
  
  
  "direct to register stolen phone " in{
  
running(FakeApplication()){
  
  val Some(result)=route(FakeRequest(GET,"/register-stolen-phone"))
  status(result) must equalTo(OK)
  contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
  
  
} 
  
}
  
  "direct to register new phone " in{
  
running(FakeApplication()){
  
  val Some(result)=route(FakeRequest(GET,"/register-new-phone"))
  status(result) must equalTo(OK)
  contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
  
  
} 
  
}
  
  "posting mobile registration " in{
  
running(FakeApplication()){
  
  val Some(result)=route(FakeRequest(POST,"/mobileRegistration"))
  status(result) must equalTo(400)
  contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
  
  
} 
  
}
  
  "Imei existence " in{
  
running(FakeApplication()){
  
  val Some(result)=route(FakeRequest(GET,"/isImeiExist?imeid=12345678902134"))
  status(result) must equalTo(400)
} 
  
}
  
  /**
   * Java script routes
   */
  
  
  "JavascriptRoutes Action" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/javascriptRoutes"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/javascript")
    }
  }
  
  
  
  "GetImeidList" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/mobile?imeid=12345678901234"))
      status(result) must equalTo(200)
     contentType(result) must beSome("application/json")
    }
  }
  
  "mobile status" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/mobileStatus"))
      
      status(result) must equalTo(200)
      contentType(result) must beSome("text/html") 
     
    }
  }
  
  
  "mobile model" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/mobileModel?id=1"))
      status(result) must equalTo(200)
      contentType(result) must beSome("application/json")
      
    }
  }
  
  "approve status" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/approve?imeid=12345678901234"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
      
    }
  }
  
   "proof demanded status" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/proofDemanded?imeid=12345678901234"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
      
    }
  }
  
   "pending status" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/pending?imeid=12345678901234"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
      
    }
  }
  
   "Send mail for demand proof" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/sendMailForDemandProof?imeid=12345678901234"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
      
    }
  }
   
   
    "change mobile registration" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/changeMobileRegType?imeid=12345678901234"))
      status(result) must equalTo(200)
      contentType(result) must beSome("text/plain")
      
    }
  }
   
    
 
   
    
/**
 * Admin Pages
 */
  
    "redirect to login" in {
      running(FakeApplication()){
        val Some(result) = route(FakeRequest(GET, "/login"))
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    
      }
    }
    
  
    "authenticate login" in{
      running(FakeApplication()){
      val Some(result) = route(FakeRequest(POST,"/authenticate").withFormUrlEncodedBody("email" -> "admin", "password" -> "knol2013").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(303)
  
      }
    }

    "login authentication failed" in {
       running (FakeApplication()){
    	  val Some(result) = route(FakeRequest(POST,"/authenticate").withFormUrlEncodedBody("email" -> "test", "password" -> "pass").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(400)
    
   
       }
 }

  
  "logout Action" in {

    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/logout"))
      status(result) must equalTo(303)
      contentType(result) must be(None)
    }
  }
 
   "mobile status admin" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET, "/admin/mobiles?status=pending"))
      status(result) must equalTo(303)
      contentType(result) must be(None)
      
      
    }
  }
  
   
   "mobile ajax call admin" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/admin/mobiles?status=pending"))
      status(result) must equalTo(303)
      contentType(result) must be(None)
     
      
      
    }
  }
  
   
   
   "redirect to brand register form" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/admin/brandForm"))
      status(result) must equalTo(303)
      contentType(result) must be(None)
      
    }
  }
   
   "saving mobile name" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(POST,"/saveMobileName").withFormUrlEncodedBody("name" -> "nokia").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(303)
      contentType(result) must be(None)

      
    }
  }
   
 "mobile model form" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/admin/createMobileModelForm"))
      status(result) must equalTo(303)
     contentType(result) must be(None)
      
    }
  }
  
 "mobile registration type form" in {
    running(FakeApplication()){
      
      val Some(result) = route(FakeRequest(GET,"/admin/changeMobileRegType"))
      status(result) must equalTo(303)
     contentType(result) must be(None)
      
    }
  }
 
 "mobile model post" in {
    running(FakeApplication()){
   val Some(result) = route(FakeRequest(POST,"/createMobileModel").withFormUrlEncodedBody("mobileName" -> "1","mobileModel"->"N72").withHeaders(CONTENT_TYPE ->
        "application/x-www-form-urlencoded"))
      status(result) must equalTo(303)
     contentType(result) must be(None)
      
    }
  }
 
 
 
}