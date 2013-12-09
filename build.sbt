name := "mycellwasstolen"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.scalatest" %   "scalatest_2.10" %  "2.0.M5b" %  "test",
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
  "org.mockito" % "mockito-all" % "1.8.5",
   "junit"  %  "junit"  %  "4.11")     

play.Project.playScalaSettings

org.scalastyle.sbt.ScalastylePlugin.Settings
