package model.convert

import java.io.FileReader
import java.util.ArrayList

import scala.collection.JavaConversions.asScalaBuffer

import au.com.bytecode.opencsv.CSVReader
import model.dals.MobileDAL.insertMobileUser
import model.domains.Domain.Mobile
import model.domains.Domain.Status


object readcsv extends App{
def convert() = {
  var mobile1: scala.collection.mutable.Map[String, scala.collection.immutable.List[String]] = scala.collection.mutable.Map()
 
  mobile1 = scala.collection.mutable.Map()
  var array = new ArrayList[ArrayList[String]]
  val reader = new CSVReader(new FileReader(s"/home/ujali/Desktop/mobile.csv"))
  var nextLine: Array[String] = Array()
  try {
    while ((nextLine = reader.readNext()) != null) {
      var list = new ArrayList[String]
      println("--------------")
      for (i <- 0 until 15) {
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
        println(i)
    val status = array(i)(9) match {
      case "Approved" => Status(1)
      case "ProofDemanded" => Status(2)
      case "Pending" => Status(0)
    }
    val res = insertMobileUser(Mobile(array(i)(0), augmentString(array(i)(1)).toInt, augmentString(array(i)(2)).toInt, array(i)(3), array(i)(4), array(i)(5), array(i)(6), array(i)(7), array(i)(8), status, array(i)(10), array(i)(11), array(i)(12), array(i)(13), array(i)(14)))
  } 
  
  
/* val res = insertMobileName(Brand(array(i)(0), array(i)(1)))
  
 val res = insertMobileModel(MobileModels(array(i)(0), augmentString(array(i)(1)).toInt))*/  
    
  
  
  
}
}