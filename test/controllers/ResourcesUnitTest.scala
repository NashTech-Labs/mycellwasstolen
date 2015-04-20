/*package controllers

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.Play.current
import play.api.cache.Cache
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import model.repository.User

class ResourcesTestCases extends Specification with Mockito {

  val username = "admin"
  val user = User("admin", "knol2013")

  "ResourcesTesting: contactUs" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = Resources.contactUs(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }

  "ResourcesTesting: blog" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = Resources.blog(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }
  
  "ResourcesTesting: FAQs" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = Resources.faq(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }
  
  "ResourcesTesting: discussionforum" in {
    running(FakeApplication()) {
      Cache.set(username, user)
      val result = Resources.discussionforum(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
    }
  }
}
*/