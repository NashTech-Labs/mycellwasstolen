name := "mycellwasstolen"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
   jdbc,
   cache,
   "com.typesafe.slick" %% "slick" % "1.0.1",
   "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "net.liftweb" %% "lift-json" % "2.5-M4",
   "org.scalatest" %   "scalatest_2.10" %  "2.0.M5b" %  "test",
   "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
   "org.mockito" % "mockito-all" % "1.8.5" %  "test",
   "junit"  %  "junit"  %  "4.11" %  "test",
   "com.restfb" % "restfb" % "1.6.12",
   "org.twitter4j" % "twitter4j-core" % "3.0.5",
   "com.amazonaws" % "aws-java-sdk" % "1.6.10",
   "org.seleniumhq.selenium" % "selenium-java" % "2.37.1"%  "test")     

play.Project.playScalaSettings

org.scalastyle.sbt.ScalastylePlugin.Settings
