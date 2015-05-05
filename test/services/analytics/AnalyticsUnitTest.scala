package services.analytics
import model.repository.{ BrandRepository, AuditRepository, ModelRepository, MobileRepository }
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
 
/**
 * Test Analytics Services 
 */
class AnalyticsUnitTest extends Specification with Mockito {

  "MychartDataformatter" should {

    "format Pie Chart Data" in {
      val mockedAuditRepository = mock[AuditRepository]
      mockedAuditRepository.getTopNLostBrands(1) returns Some(List(("Nokia", 2)), 3)
      val chartDataFormatter = new AnalyticsService(mockedAuditRepository)
      val formattedData: List[(String, Float)] = chartDataFormatter.formatPieChartData(1)
      formattedData === List(("Others", (33.333336).toFloat), ("Nokia", (66.66667).toFloat))
    }

    "format Pie Chart with Blank Data" in {
      val mockedAuditRepository = mock[AuditRepository]
      mockedAuditRepository.getTopNLostBrands(1) returns None
      val chartDataFormatter = new AnalyticsService(mockedAuditRepository)
      val formattedData: List[(String, Float)] = chartDataFormatter.formatPieChartData(1)
      formattedData === List(("NoData", 0.toFloat))
    }
    
    "format TimeSeries Chart Data" in {
      val mockedAuditRepository = mock[AuditRepository]
      mockedAuditRepository.getPerDayRegistration returns Some(List((new java.sql.Date(0),1)))
      val chartDataFormatter = new AnalyticsService(mockedAuditRepository)
      val formattedData = chartDataFormatter.formatTimeSeriesChartData
      formattedData === List((new java.sql.Date(0),1))
    }
    
     "format TimeSeries Chart With No Data" in {
      val mockedAuditRepository = mock[AuditRepository]
      mockedAuditRepository.getPerDayRegistration returns None
      val chartDataFormatter = new AnalyticsService(mockedAuditRepository)
      val formattedData = chartDataFormatter.formatTimeSeriesChartData
      formattedData === List((new java.sql.Date(0),0))
    }

  }

}
