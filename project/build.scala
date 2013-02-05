import sbt._

import Keys._
import AndroidKeys._

object BaithaDefaults {
  val settings =
    Defaults.defaultSettings ++
    AndroidBase.settings ++ Seq (
      name := "Baitha: The Scala/Android Toolkit",
      organization := "com.sattvik",
      version := "0.1.0-SNAPSHOT",
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
      libraryDependencies <+= scalaVersion(scalatestDependency(_)),
      libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test",
      crossScalaVersions := Seq(
        "2.8.1", "2.8.2", "2.9.0", "2.9.0-1", "2.9.1", "2.9.1-1", "2.9.2", "2.10.0"
      )
    )
  )

  def scalatestDependency(scalaVersion: String) = scalaVersion match {
    case "2.10.0" => "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
    case "2.8.1"  => "org.scalatest" %% "scalatest" % "1.8" % "test"
    case "2.8.2"  => "org.scalatest" %% "scalatest" % "1.8" % "test"
    case _ => "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  }
}
