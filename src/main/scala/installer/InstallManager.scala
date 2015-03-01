package installer

import java.io.File
import java.net.URL
import com.typesafe.config.Config
import sbt.IO
import util.PlatformUtil

trait Installer {
  val url: String
  val dest: String
  def install() = {
    download()
    configure()
  }
  def download() = IO.unzipURL(new URL(url), new File(dest))
  def configure()
}

class WindowsInstaller(override val url: String, override val dest: String) extends Installer {
  override def configure() = {}
}

class LinuxInstaller(override val url: String, override val dest: String) extends Installer {
  override def configure() = {}
}

object InstallManager {

  def install(config: Config) = {
    val installer = {
      if (PlatformUtil.isLinux) {
        new LinuxInstaller(config.getString(""), config.getString(""))
      } else if (PlatformUtil.isWindows) {
        new LinuxInstaller(config.getString(""), config.getString(""))
      } else {
        new LinuxInstaller(config.getString(""), config.getString(""))
      }
    }
    installer.install()
  }
}
