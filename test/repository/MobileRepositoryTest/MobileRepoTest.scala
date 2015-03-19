package repository.MobileRepositoryTest

import play.api.test.FakeApplication
import play.api.test.Helpers._
import utils.Connection
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import model.repository.Brand
import model.repository.Mobile
import utils.DBUtils.Status
import model.repository.MobileRepository
import model.repository.BrandRepository
import model.repository.ModelRepository

/**
 * Class MobileRepoTest: Unit tests the methods in MobileRepository.
 */

class MobileRepoTest extends FunSuite with BeforeAndAfterEach with MobileRepository with BrandRepository with ModelRepository {

  
  
}