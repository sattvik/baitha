import sbt._

trait Defaults extends BaseAndroidProject {
  def androidPlatformName = "android-12"
  override def compileOptions = {
    super.compileOptions ++ Seq(
      Unchecked, Optimise, Deprecation)
  }
}

class Parent(info: ProjectInfo) extends ParentProject(info) {
  override def shouldCheckOutputDirectories = false

  override def updateAction = task {None}

  lazy val main = project(".", "baitha", new LibraryProject(_))
  lazy val test = project("test", "baitha-test", new LibraryTestProject(_), main)

  class LibraryProject(info: ProjectInfo)
      extends AndroidLibraryProject(info) with Defaults {
    val scalatest = "org.scalatest" % "scalatest_2.8.1" % "1.5.1" % "test"
    val mockito   = "org.mockito" % "mockito-core" % "1.8.5" % "test"
  }

  class LibraryTestProject(info: ProjectInfo)
      extends AndroidLibraryTestProject(info) with Defaults {
    val scalatest = "org.scalatest" % "scalatest_2.8.1" % "1.5.1"
    val mockito   = "org.mockito" % "mockito-core" % "1.8.5"
  }
}
