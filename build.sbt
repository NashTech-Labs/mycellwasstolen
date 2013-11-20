name := "mycellwasstolen"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  anorm,
  cache
)     

play.Project.playScalaSettings
