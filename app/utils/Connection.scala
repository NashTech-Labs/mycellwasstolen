package utils

import scala.slick.session.Database
import play.api.db.DB
import play.api.Play.current

object Connection {

   def databaseObject(): Database = {
   Database.forDataSource(DB.getDataSource())
  }

}
