package utils

import com.typesafe.plugin._
import play.api.Play.current
import play.api.i18n.Messages

object Common {

  def registerMessage(imeid: String): String = {
    Messages("messages.mobile.register", imeid, signature)
  }

  def signature(): String = {
    Messages("messages.signature")
  }

  def sendMail(email: String, subject: String, message: String): Unit = {
    val mail = use[MailerPlugin].email
    mail.setSubject(subject)
    mail.addRecipient(email)
    mail.addFrom(Messages("default.email.title"))
    mail.sendHtml(message)
  }
}
