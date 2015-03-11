package installer

import java.io.File
import java.net.URL
import app.Command
import com.typesafe.config.Config
import model.{Quiet, BasicCommand}
import sbt.IO
import util.PlatformUtil

trait Installer {
  val url: String
  val dest: String
  val libName: String
  def install() = {
    download()
    configure()
  }
  def download() = IO.unzipURL(new URL(url), new File(dest))
  def configure()
}

class WindowsInstaller(
        override val url: String, override val dest: String, override val libName: String) extends Installer {
  override def configure() = {
    new File(dest, libName).renameTo(new File(dest, "libopenh264.dll"))
  }
}

class LinuxInstaller(
        override val url: String, override val dest: String, override val libName: String) extends Installer {
  override def configure() = {
    new File(dest, libName).renameTo(new File(dest, "libopenh264.so.0"))
    Command(BasicCommand, s"chmod 775 ${dest}/ffmpeg", Quiet).execute()
    Command(BasicCommand, s"chmod 775 ${dest}/ffprobe", Quiet).execute()
    Command(BasicCommand, s"chmod 775 ${dest}/run.sh", Quiet).execute()
  }
}

object InstallManager {

  def apply(config: Config) = {
    if (PlatformUtil.isWindows) {
      new WindowsInstaller(
        config.getString("openh264.windows.url"), config.getString("openh264.dest"), config.getString("openh264.windows.libName"))
    } else {
      new LinuxInstaller(
        config.getString("openh264.linux.url"), config.getString("openh264.dest"), config.getString("openh264.linux.libName"))
    }
  }
}
