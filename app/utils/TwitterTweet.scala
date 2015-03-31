package utils

import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.ResponseList
import twitter4j.QueryResult
import twitter4j.Query
import twitter4j.Paging
import twitter4j.auth.RequestToken
import play.api.Play
import twitter4j.Status
import play.api.Logger
import twitter4j.TwitterException
import play.api.i18n.Messages

object TwitterTweet {
  val MAX_TWITTER_SIZE = 140
  
  def tweetForStolen(imeid: String): String = {
    Messages("messages.mobile.tweetForStolen", imeid, signature)
  }
  
  def tweetForClean(imeid: String): String = {
    Messages("messages.mobile.tweetForClean", imeid, signature)
  }
  
  def signature(): String = {
    Messages("messages.link")
  }

  def tweetAMobileRegistration(message: String): Unit = {
    try {
      val twitter = new TwitterFactory().getInstance()
      // Authorising with your Twitter Application credentials
      val consumer_key = Play.current.configuration.getString("consumer_key").get
      val consumer_secret = Play.current.configuration.getString("consumer_secret").get
      val access_key = Play.current.configuration.getString("access_token").get
      val access_token = Play.current.configuration.getString("access_token_secret").get
      twitter.setOAuthConsumer(consumer_key, consumer_secret)
      twitter.setOAuthAccessToken(new AccessToken(access_key, access_token))
      val title = ""
      twitter.updateStatus(message)
    } catch {
      case ex: Exception => Logger.info("Somehow coudn't tweet the messege")
    }
  }

}
