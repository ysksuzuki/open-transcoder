package app

import com.typesafe.config.Config
import model.{MediaInfo, LogLevel, BasicCommand, CommandLineArgs}
import util.{Parent, Extension}

object Transcoder {

  def transcode(config: Config, args: CommandLineArgs) = {
    val mediaInfo = {
      MediaInfoFactory.getMediaInfo(
        getMetadata(config.getString("command.probe.metadata"), args.logLevel, args.in))
    }
    val (command, param) = getTranscodeCommand(config, args, mediaInfo)
    command.execute(param)
  }

  private def getMetadata(command: String, logLevel: LogLevel, params: String*): String = {
    Command(BasicCommand, command, logLevel).execute(params)
  }

  private def getTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo): (CommandBase, Seq[String]) = {
    args.out1 match {
      case Extension(extension) => {
        extension match {
          case "mp4" => getMp4TranscodeCommand(config, args, mediaInfo)
          case "m3u8" => getHlsTranscodeCommand(config, args, mediaInfo)
          case "webm" => getWebMTranscodeCommand(config, args, mediaInfo)
          case _@ex => throw new Exception(s"Format ${ex} is not supported")
        }
      }
      case _ => throw new Exception("""Couldn't detect a target format""")
    }
  }

  private def getMp4TranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    (
      // Command
      Command(BasicCommand, config.getString("command.transcode.mp4"), args.logLevel),
      // Parameter
      Seq(
        args.in,
        if (mediaInfo.videoInfo.codec_name == "h264") "copy" else "libopenh264",
        if (mediaInfo.audioInfo.codec_name == "aac") "copy" else "libvo_aacenc",
        args.out1,
        args.logLevel.level
      )
    )
  }

  private def getHlsTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    (
      // Command
      Command(BasicCommand, config.getString("command.transcode.hls"), args.logLevel),
      // Parameter
      Seq(
        args.in,
        if (mediaInfo.videoInfo.codec_name == "h264") "copy" else "libopenh264",
        if (mediaInfo.audioInfo.codec_name == "aac") "copy" else "libvo_aacenc",
        args.out1,
        if (!args.out2.isEmpty) args.out2
        else {
          val segment = config.getString("fileName.segment")
          args.out1 match {
            case Parent(parent) => List(parent, List(segment)).flatten.mkString("/")
            case _ => segment
          }
        },
        args.logLevel.level
      )
    )
  }

  private def getWebMTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    (
      // Command
      Command(BasicCommand, config.getString("command.transcode.webm"), args.logLevel),
      // Parameter
      Seq(args.in, args.out1, args.logLevel.level)
    )
  }
}
