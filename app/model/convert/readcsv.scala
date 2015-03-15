package model.convert
import java.io.FileReader
import java.util.ArrayList
import scala.collection.JavaConversions.asScalaBuffer
import au.com.bytecode.opencsv.CSVReader
import model.dals.MobileDAL.insertMobileUser
import model.domains.Domain.Mobile
import model.domains.Domain.Status
import play.api.Application
import controllers.Application
import controllers.Assets
import java.io.File
import play.api.Play._
import play.api.Logger
import model.domains.Domain.Brand
import model.domains.Domain.MobileModels
object readcsv {
  def convert(file: File) = {
    var mobile1: scala.collection.mutable.Map[String, scala.collection.immutable.List[String]] = scala.collection.mutable.Map()
    mobile1 = scala.collection.mutable.Map()
    var array = new ArrayList[ArrayList[String]]
    val reader = new CSVReader(new FileReader(file))
    var nextLine: Array[String] = Array()
    try {
      while ((nextLine = reader.readNext()) != null) {
        var list = new ArrayList[String]
        Logger.info("--------------")
        for (i <- 0 until 3) {
          list.add(nextLine(i))
        }
        array.add(list);
      }
    } catch {
      case ex: Exception =>
        Logger.info("----" + ex)
    }
    Logger.info("---" + array(1))
    reader.close();
    Logger.info(array.length + "---------------------")
    var newmobile1: List[String] = Nil
    for (i <- 1 until array.length) {
      println("data inseert loop")
      Logger.info("---" + i)
      val res = model.dals.MobileDAL.insertMobileModel((MobileModels(array(i)(0), augmentString(array(i)(1)).toInt)))
    }
  }
}
