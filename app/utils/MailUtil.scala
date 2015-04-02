package utils
import play.api.Play.current
import play.api.i18n.Messages
import play.api.libs.mailer.MailerPlugin
import play.api.libs.mailer.Email
import model.repository.Mobile
import play.api.Logger

trait MailUtil {

  // message for stolen registration
  def stolenRegisterMessage(imeid: String): String = {
    Messages("messages.mobile.stolenregister", imeid, signature)
  }

  //message for clean registration
  def cleanRegisterMessage(imeid: String): String = {
    Messages("messages.mobile.cleanregister", imeid, signature)
  }

  // message for demand proof
  def demandProofMessage(imeid: String): String = {
    Messages("messages.mobile.demandProof", imeid, signature)
  }

  // message for approved of mobile user
  def approvedMessage(imeid: String): String = {
    Messages("messages.mobile.approved", imeid, signature)
  }

  // message for delete mobile user
  def deleteMessage(imeid: String): String = {
    Messages("messages.mobile.delete", imeid, signature)
  }

  // message for change mobile registration to stolen
  def changeMobileRegTypeStolen(imeid: String): String = {
    Messages("messages.mobile.changeMobileRegTypeStolen", imeid, signature)
  }

  //// message for change mobile registration to clean
  def changeMobileRegTypeClean(imeid: String): String = {
    Messages("messages.mobile.changeMobileRegTypeClean", imeid, signature)
  }

  def signature(): String = {
    Messages("messages.signature")
  }

  /**
   * Sends mail to mobile user
   * @param email of mobile user
   * @param subject of mail
   * @param message
   */
  def sendMail(email: String, subject: String, message: String): Unit = {
    try {
      val emailBuilder = Email(
        subject,
        Messages("default.email.title"),
        Seq(email), bodyHtml = Some(message))
      MailerPlugin.send(emailBuilder)
    } catch {
      case ex: Exception => Logger.error("MobileController:sendEmail failed please check current mail configuration")
    }
  }
}
object MailUtil extends MailUtil
