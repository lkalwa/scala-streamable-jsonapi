import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.0" % "test"
  lazy val scalaTic = "org.scalactic" %% "scalactic" % "3.2.0" % "test"
  lazy val jackson = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.11.0"
  lazy val scalaParser = "com.typesafe.play" %% "play-json" % "2.9.1" % "test"
}
