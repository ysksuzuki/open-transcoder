package app

import com.typesafe.config.Config
import model.{MediaInfo, LogLevel, BasicCommand, CommandLineArgs}
import util.IOUtil

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

  private def getTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    val extension = IOUtil.extension(args.out1).getOrElse("")
    extension match {
      case "mp4"  => getMp4TranscodeCommand(config, args, mediaInfo)
      case "m3u8" => getHlsTranscodeCommand(config, args, mediaInfo)
      case "webm" => getWebMTranscodeCommand(config, args, mediaInfo)
      case _      => getHlsTranscodeCommand(config, args, mediaInfo)
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
        args.out2,
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
