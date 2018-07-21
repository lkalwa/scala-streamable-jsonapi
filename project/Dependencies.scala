import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  lazy val scalaTic = "org.scalactic" %% "scalactic" % "3.0.5"
  lazy val jackson = "org.codehaus.jackson" % "jackson-jaxrs" % "1.9.13"
  lazy val scalaParser =  "net.liftweb" %% "lift-json" % "3.3.0"
}
