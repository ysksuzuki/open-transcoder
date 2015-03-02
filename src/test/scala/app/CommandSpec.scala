package app

import java.io.File
import com.typesafe.config.ConfigFactory
import model.LogLevel
import test.UnitSpec

class CommandSpec extends UnitSpec {

  before {
  }

  after {
  }

  "The execute function" should "execute a command which transcodes a media file to mp4" in {
    val expected = "/usr/bin/ffmpeg -i input.mp4 -c:v libopenh264 -c:a libvo_aacenc -movflags faststart output.mp4 -loglevel quiet"
    val config = ConfigFactory.load()
    val command = new BasicCommand(config.getString("command.transcode.mp4"), LogLevel("quiet")) with CommandMoc
    command.execute(Seq("input.mp4", "libopenh264", "libvo_aacenc", "output.mp4", "quiet"))
    assert(expected == command.commandTest(0))
  }

  "The execute function" should "execute a command which transcodes a media file to hls" in {
    val expected = "/usr/bin/ffmpeg -i input.mp4 -vcodec libopenh264 -acodec libvo_aacenc -flags -global_header -f segment -segment_list_flags cache -segment_time 10 -segment_list_size 720 -segment_format mpegts -segment_list playlist.m3u8 segment%d.ts -loglevel quiet"
    val config = ConfigFactory.load()
    val command = new BasicCommand(config.getString("command.transcode.hls"), LogLevel("quiet")) with CommandMoc
    command.execute(Seq("input.mp4", "libopenh264", "libvo_aacenc", "playlist.m3u8", "segment%d.ts", "quiet"))
    assert(expected == command.commandTest(0))
  }

  "The execute function" should "execute a command which transcodes a media file to webm" in {
    val expected = "/usr/bin/ffmpeg -i input.mp4 -f webm -vcodec libvpx -acodec libvorbis -aq 90 -ac 2 output.webm -loglevel quiet"
    val config = ConfigFactory.load()
    val command = new BasicCommand(config.getString("command.transcode.webm"), LogLevel("quiet")) with CommandMoc
    command.execute(Seq("input.mp4", "output.webm", "quiet"))
    assert(expected == command.commandTest(0))
  }

  "The execute function" should "execute a command which probes a media file" in {
    val expected = "/usr/bin/ffprobe -v quiet -print_format json -show_format -show_streams input.mp4"
    val config = ConfigFactory.load()
    val command = new BasicCommand(config.getString("command.probe.metadata"), LogLevel("quiet")) with CommandMoc
    command.execute(Seq("input.mp4"))
    assert(expected == command.commandTest(0))
  }
}

trait CommandMoc extends BasicCommand {
  var commandTest: Seq[String] = Seq()

  override protected def executeCommand(params: Seq[String], file: Option[File]): String = {
    commandTest = commandTest :+ command.format(params: _*)
    ""
  }
}
