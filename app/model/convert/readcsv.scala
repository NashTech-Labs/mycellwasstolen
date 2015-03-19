package model.convert

import java.io.FileReader
import java.util.ArrayList
import scala.collection.JavaConversions.asScalaBuffer
import au.com.bytecode.opencsv.CSVReader
import play.api.Application
import controllers.Application
import controllers.Assets
import java.io.File
import play.api.Play._

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
        for (i <- 0 until 3) {
          list.add(nextLine(i))
        }
        array.add(list);
      }
    } catch {

      case ex: Exception =>
        println(ex)
    }
    println(array(1))
    reader.close();

    println(array.length + "---------------------")
    var newmobile1: List[String] = Nil
    for (i <- 1 until array.length) {

     // val res = (Brand(array(i)(0), array(i)(1), Some(augmentString(array(i)(2)).toInt)))
    }

  }

}