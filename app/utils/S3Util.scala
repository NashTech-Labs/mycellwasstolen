package utils

import play.api.Play
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import play.api.Logger
import scala.concurrent._
import ExecutionContext.Implicits.global

trait S3UtilComponent {

  val bucketName = Play.current.configuration.getString("aws_bucket_name").get
  val AWS_ACCESS_KEY = Play.current.configuration.getString("aws_access_key").get
  val AWS_SECRET_KEY = Play.current.configuration.getString("aws_secret_key").get
  val mcwsAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
  val amazonS3Client = new AmazonS3Client(mcwsAWSCredentials)

  /**
   * Store file to standard bucket on amazonS3
   * @param documentName, name of file
   * @param fileToSave the file which you want to store
   */
  def store(documentName: String, fileToSave: File): Future[Boolean] = {
    Future {
      try {
        amazonS3Client.putObject(bucketName, documentName, fileToSave)
        Logger.info(s"Successfully upload : [ Bucket : ${bucketName} ] [ document : ${documentName}]"); true
      } catch {
        case ex: Exception => Logger.info("UNABLE TO STORE FILE", ex); false
      }
    }
  }

  /**
   * Delete Object from standard bucket on amazonS3
   * @param key key is the unique key of the object which you want to delete
   */
  def delete(key: String): Future[Boolean] = {
    Future {
      try {
        amazonS3Client.deleteObject(bucketName, key)
        Logger.info(s" Successfully delete: [ Bucket : ${bucketName} ] [ Key : ${key}]"); true
      } catch {
        case ex: Exception =>
          Logger.info("UNABLE TO DELETE FILE", ex); false
      }
    }
  }
}
object S3Util extends S3UtilComponent
