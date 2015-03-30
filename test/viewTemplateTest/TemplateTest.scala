
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import model.repository.{ Brand, Mobile, Model, MobileRegisterForm, User }

class TemplateTest extends Specification {

  "render errorPage template" in new WithApplication {
    views.html.errorPage.render("Messege from error page")
    views.html.errorPage.ref("Messege from error page")
    views.html.errorPage.f("Messege from error page")
  }

  "render footer template" in new WithApplication {
    views.html.footer.render("rendering footer")
    views.html.footer.ref("rendering footer")
    views.html.footer.f("rendering footer")
  }

  /*  "mobile registrationform template" in new WithApplication {
    val registrationForm = MobileRegisterForm("userManish",1,1,
        "123456789012347","1234","20-02-2015","9988776678",
        "reseamanish@gmail.com","pending",
        "no document","hello desc","Nokia","Nonokia")
        
    val brands  = List(Brand("nokia", "12-17-2013",Some(1)))
    val user  = User("manish@knoldus.com","secret hai")
    implicit val request: RequestHeader = FakeRequest().withSession()

    views.html.mobileRegistrationForm.render(play.api.data.Form[modll],brands,user, request.flash)
    views.html.mobileRegistrationForm.ref(registrationForm,brands,user)
    views.html.mobileRegistrationForm.f(registrationForm,brands,user)
  }
*/
  
}

