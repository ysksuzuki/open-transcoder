package installer

import java.io.File

import model.StringProperty
import util.{PlatformUtil, IOUtil}

import scala.io.Source
import scala.util.Properties

trait Configure {
  val ffmpeg: String
  val ffprobe: String
  def configure(file: String = "open-transcoder.conf") = {
    val header =
      """include classpath("application.conf")
        |
        |""".stripMargin
    val contents = Source.fromURL(getClass.getClassLoader.getResource("application.conf"))
      .getLines().map {
      case line@Property(key) => {
        key match {
          case "transcode" => StringProperty("transcode", ffmpeg)
          case "probe" => StringProperty("probe", ffprobe)
          case "dest" => {
            val dest = {
              val file = new File(ffmpeg)
              if (ffmpeg.isEmpty) file.getAbsolutePath
              else file.getAbsoluteFile.getParent
            }
            StringProperty("dest", dest)
          }
          case _ => "//" + line
        }
      }
      case line => line
    } mkString Properties.lineSeparator
    IOUtil.writeText(file, header + contents)
  }
  protected def getAbsolutePath(pattern: String): String = {
    val current = new File(".").getAbsoluteFile.getParent
    IOUtil.fileSearch(new File(current)) {
      _.getName.matches(pattern)
    }.headOption.map(_.getAbsolutePath).getOrElse("")
  }
}

class WindowsConfigure extends Configure {
  override val ffmpeg =  getAbsolutePath("""ffmpeg*.exe""")
  override val ffprobe = getAbsolutePath("""ffprobe*.exe""")
}

class LinuxConfigure extends Configure {
  override val ffmpeg =  getAbsolutePath("""ffmpeg*""")
  override val ffprobe = getAbsolutePath("""ffprobe*""")
}

object ConfigManager {
  def apply() = {
    if (PlatformUtil.isLinux) new LinuxConfigure()
    else if (PlatformUtil.isWindows) new WindowsConfigure()
    else new LinuxConfigure()
  }
}

object Property {
  def unapply(line: String) = {
    val list = line.replaceAll("\\s", "").split("=").toList
    if (list.length >= 2) list.headOption
    else None
  }
}


