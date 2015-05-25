import sbt._
import Keys._

object BaithtaBuild extends Build {
  val globalSettings = super.settings ++ Seq(
    name := "Baitha: The Scala/Android Toolkit",
    organization := "com.sattvik",
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    javacOptions ++= Seq(
      "-source", "1.6",
      "-target", "1.6",
      "-encoding", "UTF-8"
    ),
    scalacOptions ++= Seq (
      Opts.compile.deprecation,
      Opts.compile.unchecked,
      Opts.compile.optimise,
      "-feature"
    )
  )

  lazy val main = Project(
    "baitha",
    file("."),
    settings = globalSettings ++ Seq (
      name := "baitha",
      version := "0.1.1-SNAPSHOT",
      libraryDependencies ++= Seq (
        "com.google.android" % "android" % "4.1.1.4" % "provided",
        "org.mockito" % "mockito-core" % "2.0.8-beta" % "test",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test" exclude("org.scala-lang", "scala-reflect")
      )
    )
  )
}
