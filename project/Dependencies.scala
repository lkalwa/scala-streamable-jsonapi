import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % "test"
  lazy val scalaTic = "org.scalactic" %% "scalactic" % "3.0.8"
  lazy val jackson = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.9.9"
  lazy val scalaParser =  "com.typesafe.play" %% "play-json" % "2.7.4"
}
