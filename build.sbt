import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.lkalwa",
      scalaVersion := "2.11.12",
      crossScalaVersions := Seq("2.11.12", "2.12.10"),
      version      := "0.76"
    )),
    name := "scala-streamable-jsonapi",
    libraryDependencies += scalaTest,
    libraryDependencies += scalaTic,
    libraryDependencies += jackson,
    libraryDependencies += scalaParser
  )

homepage := Some(url("https://github.com/lkalwa/scala-streamable-jsonapi"))

scmInfo := Some(ScmInfo(url("https://github.com/lkalwa/scala-streamable-jsonapi"),
  "git@github.com:lkalwa/scala-streamable-jsonapi.git"))

developers := List(
  Developer("lkalwa", "Leszek Kalwa", "leszek.kalwa@gmail.com", url("https://github.com/lkalwa")),
  Developer("klobuczek", "Heinrich Klobuczek", "heinrich@mail.com", url("https://github.com/klobuczek"))
)

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

//coverageEnabled := true