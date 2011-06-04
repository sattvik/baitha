import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val androidRepo = "sattvik-android-repo" at "http://sattvik.github.com/maven/"
  val android = "org.scala-tools.sbt" % "sbt-android-plugin" %
                "0.5.2-SNAPSHOT-dan-2011060301"

  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.4.0"
}
