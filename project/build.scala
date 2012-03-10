import sbt._

import Keys._
import AndroidKeys._

object BaithaDefaults {
  val settings =
    Defaults.defaultSettings ++
    AndroidProject.androidSettings ++ Seq (
      name := "Baitha: The Scala/Android Toolkit",
      organization := "com.sattvik",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.8.2",
      scalacOptions ++= Seq (
        Opts.compile.deprecation,
        Opts.compile.unchecked,
        Opts.compile.optimise
      ),
      platformName in Android := "android-12"
    )

  val testSettings = AndroidTest.settings ++ settings
}

object BaithaBuild extends Build {
  lazy val main = Project(
    "baitha",
    file("."),
    settings = BaithaDefaults.settings ++ Seq (
      libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "1.7.1" % "test",
        "org.mockito" % "mockito-core" % "1.9.0" % "test"
      )
    )
  )

  //lazy val test = Project(
    //"baitha-test",

  //)
}
