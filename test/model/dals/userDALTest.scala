package model.dals

import org.scalatest.FunSuite
import model.domains.domain._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.Logger

class userDALTest extends FunSuite {

  test("userDAL: ") {

    val mobileUser = MobileRegister(
      "gs", "nokia", "glaxacy", "313dsd", new java.sql.Date(new java.util.Date().getTime()), 983131313,
      "gs@gmail.com", "ddas  asd")

    running(FakeApplication()) {
      val mobileId = userDal.insertMobileUser(mobileUser)
      println("mobileId: " + mobileId)
      assert(mobileId.right.get.get > 0)
    }

  }

}