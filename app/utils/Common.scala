package utils

import com.typesafe.plugin._
import play.api.i18n.Messages

object Common {
  
  def registerMessage(name: String, userId: String, password: String): String = {
    Messages("messages.user.register", name, userId, signature)
  }
  
  def signature(): String = {
    Messages("messages.signature")
  }
  
 /* def sendMailWithReplyTo(email: String, subject: String, message: String, replyTo: String): Unit = {
    val mail = use[MailerPlugin].email
    mail.setSubject(subject)
    mail.addRecipient(email)
    mail.addFrom(Messages("default.email.title"))
    mail.setReplyTo(replyTo)
    mail.sendHtml(message)
  }*/

}