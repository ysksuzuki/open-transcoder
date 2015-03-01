package app

import model._
import org.json4s.JsonAST._
import org.json4s.jackson.JsonMethods._

import scala.math.BigDecimal.RoundingMode
import scala.util.matching.Regex

object MediaInfoFactory {
  def getMediaInfo(jsonStr: String): MediaInfo = {
    val json = parse(jsonStr)
    MediaInfo(
      compact(render(json \ "format" \ "duration")).replaceAll("\"", ""),
      MediaInfoUtil.calcBitRate(compact(render(json \ "format" \ "bit_rate")).replaceAll("\"", "")),
      getVideoInfo(json),
      getAudioInfo(json)
    )
  }
  private def getVideoInfo(json: JValue) = {
    val videoInfos: Seq[(String, BigInt, BigInt, String)] = {
      for {
        JObject(video) <- json
        JField("codec_type", JString(codec_type)) <- video
        JField("codec_name", JString(codec_name)) <- video
        JField("width", JInt(width)) <- video
        JField("height", JInt(height)) <- video
        JField("r_frame_rate", JString(r_frame_rate)) <- video
        if codec_type == "video"
      } yield (codec_name, width, height, r_frame_rate)
    }
    videoInfos.headOption map { case (codec_name, width, height, r_frame_rate) =>
      VideoInfo(
        codec_name,
        width.toString(),
        height.toString(),
        MediaInfoUtil.calcFrameRate(r_frame_rate))
    } getOrElse(VideoInfo())
  }
  private def getAudioInfo(json: JValue) = {
    val audioInfos: Seq[String] = {
      for {
        JObject(audio) <- json
        JField("codec_type", JString(codec_type)) <- audio
        JField("codec_name", JString(codec_name)) <- audio
        if codec_type == "audio"
      } yield (codec_name)
    }
    audioInfos.headOption map { codec_name =>
      AudioInfo(codec_name)
    } getOrElse(AudioInfo())
  }
}

object MediaInfoUtil {
  def calcFrameRate(r_frame_rate: String) = {
    Option(r_frame_rate) match {
      case Some(x) =>
        val pattern: Regex = """(\d+)/([1-9][0-9]*)""".r
        x match {
          case pattern(a, b) =>
            (BigDecimal(a) / BigDecimal(b)).setScale(2, RoundingMode.HALF_UP).toString()
          case _ =>
            throw new Exception(s"Illegal frame rate r_frame_rate=$r_frame_rate")
        }
      case None => throw new Exception(s"Illegal frame rate r_frame_rate=null")
    }
  }

  def calcBitRate(bit_rate: String) = {
    Option(bit_rate) match {
      case Some(x) =>
        if (isDigit(x)) {
          (BigDecimal(x) / BigDecimal("1000")).setScale(0, RoundingMode.HALF_UP).toString()
        } else {
          throw new Exception("Illegal bit rate bit_rate=%s".format(bit_rate))
        }
      case None => throw new Exception("Illegal bit rate bit_rate=null")
    }
  }
  def isDigit(str: String) = !str.isEmpty && str.forall(Character.isDigit)
}
