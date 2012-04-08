import sbt._

import Keys._
import AndroidKeys._

object BaithaDefaults {
  val settings =
    Defaults.defaultSettings ++
    AndroidBase.settings ++ Seq (
      name := "Baitha: The Scala/Android Toolkit",
      organization := "com.sattvik",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.1-1",
      scalacOptions ++= Seq (
        Opts.compile.deprecation,
        Opts.compile.unchecked,
        Opts.compile.optimise
      ),
      platformName in Android := "android-15"
    )

  val testSettings = AndroidTest.settings ++ settings
}

object BaithaBuild extends Build {
  lazy val main = Project(
    "baitha",
    file("."),
    settings = BaithaDefaults.settings ++ Seq (
      name := "baitha",
      libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "1.7.1" % "test",
        "org.mockito" % "mockito-core" % "1.9.0" % "test"
      ),
      crossScalaVersions := Seq(
        "2.8.1", "2.8.2", "2.9.0", "2.9.0-1", "2.9.1", "2.9.1-1"
      )
    )
  )

  lazy val test = Project(
    "baitha-test",
    file("test"),
    settings =
      BaithaDefaults.settings ++
      AndroidTest.androidSettings ++
      AndroidTest.settings ++ Seq (
        name := "baitha_test",
        libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.1" % "test"
      )
  ) dependsOn main
}
