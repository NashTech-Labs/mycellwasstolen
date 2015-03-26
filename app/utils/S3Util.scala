package utils

import play.api.Play
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File

trait S3UtilComponent {

  def store(documentName: String, fileToSave: File) = {
    val bucketName = Play.current.configuration.getString("aws_bucket_name").get
    val AWS_ACCESS_KEY = Play.current.configuration.getString("aws_access_key").get
    val AWS_SECRET_KEY = Play.current.configuration.getString("aws_secret_key").get
    val mcwsAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
    val amazonS3Client = new AmazonS3Client(mcwsAWSCredentials)
    amazonS3Client.putObject(bucketName, documentName, fileToSave)
  }

}

object S3Util extends S3UtilComponent