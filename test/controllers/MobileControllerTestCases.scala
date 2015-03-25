
package controllers

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.Security
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import model.repository.User

class MobileControllerTestCases extends Specification{
   val user=User("admin","knol2013")
  val cachedUser=User("admin","knol2013")

  }
