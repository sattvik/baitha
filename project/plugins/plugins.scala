import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val android = "org.scala-tools.sbt" % "sbt-android-plugin" % "0.5.2-SNAPSHOT"

  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea  = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.4.0"
}
