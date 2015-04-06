package utils

import twitter4j._
import twitter4j.auth.{ RequestToken, AccessToken }
import play.api.Play
import play.api.Logger
import twitter4j.TwitterException
import play.api.i18n.Messages
import scala.concurrent._
import ExecutionContext.Implicits.global

trait TwitterTweet {

  //tweet message for stolen mobile
  def tweetForStolen(imeid: String): String = {
    Messages("messages.mobile.tweetForStolen", imeid)
  }

  // tweet message for clean mobile
  def tweetForClean(imeid: String): String = {
    Messages("messages.mobile.tweetForClean", imeid)
  }

  /**
   * tweets message on MCWS twitter page
   * @param message
   */
  def tweetAMobileRegistration(message: String): Future[Boolean] = {
    Future {
      try {
        val twitter = new TwitterFactory().getInstance()
        // Authorising with your Twitter Application credentials
        val consumer_key = Play.current.configuration.getString("consumer_key").get
        val consumer_secret = Play.current.configuration.getString("consumer_secret").get
        val access_key = Play.current.configuration.getString("access_token").get
        val access_token = Play.current.configuration.getString("access_token_secret").get
        twitter.setOAuthConsumer(consumer_key, consumer_secret)
        twitter.setOAuthAccessToken(new AccessToken(access_key, access_token))
        twitter.updateStatus(message)
        true
      } catch {
        case ex: Exception => Logger.info("Somehow coudn't tweet the messege"); false
      }
    }
  }
}
object TwitterTweet extends TwitterTweet

