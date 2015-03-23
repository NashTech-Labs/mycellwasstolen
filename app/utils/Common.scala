package utils
import play.api.Play.current
import play.api.i18n.Messages
import play.api.libs.mailer.MailerPlugin
import play.api.libs.mailer.Email

object Common {

  def stolenRegisterMessage(imeid: String): String = {
    Messages("messages.mobile.stolenregister", imeid, signature)
  }

  def cleanRegisterMessage(imeid: String): String = {
    Messages("messages.mobile.cleanregister", imeid, signature)
  }

  def demandProofMessage(imeid: String): String = {
    Messages("messages.mobile.demandProof", imeid, signature)
  }

  def approvedMessage(imeid: String): String = {
    Messages("messages.mobile.approved", imeid, signature)
  }

  def deleteUserMessage(imeid: String): String = {
    Messages("messages.mobile.delete", imeid, signature)
  }

  def signature(): String = {
    Messages("messages.signature")
  }

  def sendMail(email: String, subject: String, message: String): Unit = {
    val emailBuilder = Email(
      subject,
      Messages("default.email.title"),
      Seq(email), bodyHtml = Some(message))
    MailerPlugin.send(emailBuilder)
    
  }
}
