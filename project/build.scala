import sbt._
import Keys._

object BaithtaBuild extends Build {
  val globalSettings = super.settings ++ Seq(
    name := "Baitha: The Scala/Android Toolkit",
    organization := "com.sattvik",
    scalaVersion := "2.10.5",
    javacOptions ++= Seq(
      "-source", "1.6",
      "-target", "1.6",
      "-encoding", "UTF-8"
    ),
    scalacOptions ++= Seq (
      Opts.compile.deprecation,
      Opts.compile.unchecked,
      Opts.compile.optimise
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
        "org.mockito" % "mockito-core" % "2.0.7-beta" % "test",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test" exclude("org.scala-lang", "scala-reflect")
      )
    )
  )
}
