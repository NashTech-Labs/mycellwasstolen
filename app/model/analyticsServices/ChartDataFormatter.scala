package model.analyticsServices

import model.repository.{ BrandRepository, AuditRepository, ModelRepository, MobileRepository }

/**
 * Provides Analytics services for rendering charts on the views,
 * formatting chart data coming from the data access layer.
 */

class AnalyticsService(auditRepo: AuditRepository) extends AuditRepository with ChartDataFormatter {

  /**
   * @param n top Brands which has been lost the most
   * @return List of top n lost brands with their lost_count from mobile table along with
   * their share of lost percentage theft  e.g.
   * (List(('A',33.33),('B', 33.33),('C',33.33))
   * (n< number of brands)
   */
  def formatPieChartData(n: Int): List[(String, Float)] = {
    auditRepo.getTopNLostBrands(n) match {
      case Some((listofModelCount, totalTheft)) => {
        val totalCounts = listofModelCount.map { case (model, modelCount) => modelCount }
        val sumOfCounts = totalCounts.sum
        val otherCounts = totalTheft - sumOfCounts
        val floatValues = listofModelCount.map({ case (modelName, modelCount) => (modelName, (modelCount.toFloat / totalTheft.toFloat) * 100) })
        val otherCountTuple = ("Others", otherCounts.toFloat / totalTheft.toFloat * 100)
        val dataWithOthersShare = otherCountTuple :: floatValues
        dataWithOthersShare
      }
      case _ =>
        List(("NoData", 0.toFloat))
    }
  }

/**
 * Converts a a tuple (YYYY-MM-DD,registrationCount) into high chart
 *  JSON data format (Date.UTC(YYYY,DD,MM),n) 
 */
  
  def formatTimeSeriesChartData: List[(java.sql.Date, Int)] = {
    auditRepo.getPerDayRegistration match {
      case Some(countList) => {
        countList.map { case (date, count) => (date,count) }
      }
      case None =>
        List((new java.sql.Date(0),0))
    }
  }
}

/**
 * Provides methods to format raw data from data access layer
 * into different chart data input formats.
 */
trait ChartDataFormatter {
  def formatPieChartData(n: Int): List[(String, Float)]
  def formatTimeSeriesChartData: List[(java.sql.Date, Int)]
}

object AnalyticsService extends AnalyticsService(AuditRepository)