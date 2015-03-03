package installer

import java.io.File

import model.StringProperty
import util.IOUtil

import scala.io.Source
import scala.util.Properties
import scala.util.matching.Regex

object ConfigManager {

  def configure(file: String = "open-transcoder.conf") = {
    val ffmpeg =  getAbsolutePath("""(ffmpeg)*""".r)
    val ffprobe = getAbsolutePath("""(ffprobe)*""".r)
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

  private def getAbsolutePath(pattern: Regex): String = {
    val current = new File(".").getAbsoluteFile.getParent
    IOUtil.fileSearch(new File(current)) { file =>
      file.getName match {
        case pattern(n) => true
        case _ => false
      }
    }.headOption.map(_.getAbsolutePath).getOrElse("")
  }
}

object Property {
  def unapply(line: String) = {
    val list = line.replaceAll("\\s", "").split("=").toList
    if (list.length >= 2) list.headOption
    else None
  }
}


