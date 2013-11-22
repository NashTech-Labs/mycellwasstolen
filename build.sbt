name := "mycellwasstolen"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
   "org.scalatest" %   "scalatest_2.10" %  "2.0.M5b" %  "test",
    "junit"  %  "junit"  %       "4.11"   
)     

play.Project.playScalaSettings
