package app

import com.typesafe.config.Config
import model.{MediaInfo, LogLevel, BasicCommand, CommandLineArgs}
import util.{PlatformUtil, Parent, Extension}

object Transcoder {
  def transcode(config: Config, args: CommandLineArgs) = {
    val transcoder = {
      if (PlatformUtil.isWindows) new WindowsTranscoder()
      else new LinuxTranscoder()
    }
    transcoder.transcode(config, args)
  }
}

trait Transcoder {
  def transcode(config: Config, args: CommandLineArgs) = {
    val mediaInfo = {
      MediaInfoFactory.getMediaInfo(
        getMetadata(config, args.logLevel, args.in))
    }
    val (command, param) = getTranscodeCommandAndParam(config, args, mediaInfo)
    command.execute(param)
  }
  protected def getMetadata(config: Config, logLevel: LogLevel, params: String*): String
  protected def getTranscodeCommand(command: String, logLevel: LogLevel, dest: String): CommandBase;
  protected def getTranscodeCommandAndParam(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo): (CommandBase, Seq[String]) = {
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

  protected def getMp4TranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    val command = getTranscodeCommand(
      config.getString("command.transcode.mp4"), args.logLevel, config.getString("openh264.dest"))
    val parameter = Seq(
      args.in,
      if (mediaInfo.videoInfo.codec_name == "h264") "copy" else "libopenh264",
      if (mediaInfo.audioInfo.codec_name == "aac") "copy" else "libvo_aacenc",
      args.out1,
      args.logLevel.level)
    (command, parameter)
  }

  protected def getHlsTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    val command = getTranscodeCommand(
      config.getString("command.transcode.hls"), args.logLevel, config.getString("openh264.dest"))
    val parameter = Seq(
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
      args.logLevel.level)
    (command, parameter)
  }

  protected def getWebMTranscodeCommand(config: Config, args: CommandLineArgs, mediaInfo: MediaInfo) = {
    val command = getTranscodeCommand(
      config.getString("command.transcode.webm"), args.logLevel, config.getString("openh264.dest"))
    val parameter = Seq(args.in, args.out1, args.logLevel.level)
    (command, parameter)
  }
}

class LinuxTranscoder extends Transcoder {
  override protected def getMetadata(config: Config, logLevel: LogLevel, params: String*): String = {
    Command(config.getString("command.probe.metadata"),
      logLevel, ("LD_LIBRARY_PATH", config.getString("openh264.dest"))).execute(params)
  }
  override protected def getTranscodeCommand(command: String, logLevel: LogLevel, dest: String): CommandBase = {
    Command(command, logLevel, ("LD_LIBRARY_PATH", dest))
  }
}

class WindowsTranscoder extends Transcoder {
  override protected def getMetadata(config: Config, logLevel: LogLevel, params: String*): String = {
    Command(BasicCommand, config.getString("command.probe.metadata"), logLevel).execute(params)
  }
  override protected def getTranscodeCommand(command: String, logLevel: LogLevel, dest: String): CommandBase = {
    Command(BasicCommand, command, logLevel)
  }
}

