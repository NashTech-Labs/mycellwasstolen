
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import java.io.File
import java.text.SimpleDateFormat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import play.api._
import play.api.Play.current
import play.api.cache.Cache
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import utils._
import model.repository.User
import java.util.Date
import java.sql.Timestamp

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

    "mobile registrationform template" in new WithApplication {
    val user  = Some(User("manish@knoldus.com","secrethai"))
    val html=new play.twirl.api.Html("It <em>finally</em> works!")
    views.html.audit.audit.ref(user)(html)
    views.html.audit.audit.f(user)(html)
  }
}

