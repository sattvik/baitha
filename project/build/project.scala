import sbt._

trait Defaults {
  def androidPlatformName = "android-7"
}
class Parent(info: ProjectInfo) extends ParentProject(info) {
  override def shouldCheckOutputDirectories = false
  override def updateAction = task { None }

  lazy val main  = project(".", "baitha", new LibraryProject(_))

  class LibraryProject(info: ProjectInfo) extends AndroidLibraryProject(info) with Defaults
}
