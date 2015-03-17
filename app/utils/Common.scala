package utils

import com.typesafe.plugin._
import play.api.Play.current
import play.api.i18n.Messages

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
    val mail = use[MailerPlugin].email
    mail.setSubject(subject)
    mail.setRecipient(email)
    mail.setFrom(Messages("default.email.title"))
    mail.sendHtml(message)
  }
}
