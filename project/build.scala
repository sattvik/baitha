import sbt._

import Keys._
import AndroidKeys._

object BaithaDefaults {
  val settings =
    Defaults.defaultSettings ++
    AndroidBase.settings ++ Seq (
      name := "Baitha: The Scala/Android Toolkit",
      organization := "com.sattvik",
      version := "0.1.0",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq (
        Opts.compile.deprecation,
        Opts.compile.unchecked,
        Opts.compile.optimise
      ),
      platformName in Android := "android-17"
    )
}

object BaithaBuild extends Build {
  lazy val main = Project(
    "baitha",
    file("."),
    settings = BaithaDefaults.settings ++ Seq (
      name := "baitha",
      libraryDependencies ++= Seq (
        "com.google.android" % "support-v4" % "r7",
        "org.mockito" % "mockito-core" % "1.9.5" % "test",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      ),
      crossScalaVersions := Seq(
        "2.9.0", "2.9.0-1", "2.9.1", "2.9.1-1", "2.9.2", "2.10.0"
      )
    )
  )
}
