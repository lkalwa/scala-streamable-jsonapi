import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  lazy val scalaTic = "org.scalactic" %% "scalactic" % "3.0.5"
  lazy val jackson = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.8.0"
  lazy val scalaParser =  "com.typesafe.play" %% "play-json" % "2.6.7"
}
