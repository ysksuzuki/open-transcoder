package installer

import java.io.File
import java.net.URL

import sbt.IO

trait Installer {
  val url: String
  val dest: String
  def install() = IO.unzipURL(new URL(url), new File(dest))
  def configure()
}

class WindowsInstaller(override val url: String, override val dest: String) extends Installer {
  override def configure() = {}
}

class LinuxInstaller(override val url: String, override val dest: String) extends Installer {
  override def configure() = {}
}

object InstallManager {
}
