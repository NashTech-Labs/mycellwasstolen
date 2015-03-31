package utils

import play.api.Play
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import play.api.Logger

trait S3UtilComponent {

  val bucketName = Play.current.configuration.getString("aws_bucket_name").get
  val AWS_ACCESS_KEY = Play.current.configuration.getString("aws_access_key").get
  val AWS_SECRET_KEY = Play.current.configuration.getString("aws_secret_key").get
  val mcwsAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
  val amazonS3Client = new AmazonS3Client(mcwsAWSCredentials)

  /**
   * Store file to standard bucket on S3
   */
  def store(documentName: String, fileToSave: File) = {

    amazonS3Client match {

      case (_amazonS3Client) =>
        try {
          amazonS3Client.putObject(bucketName, documentName, fileToSave)
        } catch {
          case ex: Exception => Logger.error(ex.getMessage(), ex); false
        }
      case _ => Logger.info("We could not connect to Amazon Server at this moment")
      None
    }
  }

  /**
   * Delete file from standard bucket on S3
   */
  def delete(fileKeyName: String): Boolean = {
    try {
      amazonS3Client.deleteObject(bucketName, fileKeyName)
      true
    } catch {
      case ex: Exception =>
        Logger.error(ex.getMessage(), ex)
        false
    }
  }

  /**
   * Checks if the file exists on the standard bucket of S3
   */

  /*def doesFileExist(fileKeyName: String): Boolean = {
    try {
      amazonS3Client.getObjectMetadata(bucketName, fileKeyName)
      true
    } catch {
      case ex: Exception => Logger.error(ex.getMessage(), ex)
      false
    }
  }*/

}

object S3Util extends S3UtilComponent