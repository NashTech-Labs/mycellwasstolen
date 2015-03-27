name := """mycellwasstolen"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
 "org.postgresql" % "postgresql" % "9.4-1200-jdbc4",
 "com.typesafe.slick" %% "slick" % "2.1.0",
 "net.liftweb" %% "lift-json" % "2.6",
 "org.scalatest" %   "scalatest_2.11" %  "2.2.2" %  "test",
 "com.typesafe.play" %% "play-mailer" % "2.4.0",
 "org.mockito" % "mockito-all" % "1.8.5" %  "test",
 "junit"  %  "junit"  %  "4.11" %  "test",
 "com.restfb" % "restfb" % "1.6.12",
 "org.twitter4j" % "twitter4j-core" % "4.0.2",
 "com.amazonaws" % "aws-java-sdk" % "1.6.10",
 "net.sf.opencsv" % "opencsv" % "2.1",
 "org.seleniumhq.selenium" % "selenium-java" % "2.45.0"%  "test",
  "com.h2database" % "h2" % "1.3.166" % "test",
   "org.scalastyle" %% "scalastyle" % "0.6.0"
)
