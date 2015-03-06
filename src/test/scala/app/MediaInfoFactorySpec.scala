package app

import model.{AudioInfo, VideoInfo, MediaInfo}
import test.UnitSpec

import scala.io.Source
import scala.util.Properties

class MediaInfoFactorySpec extends UnitSpec {

  val resources = "src/test/resources/app"

  before {
  }

  after {
  }

  describe("getMediaInfo") {
    it("should retrieve metadata of a h264 video from json strings") {
      val expected = MediaInfo("102.981000", "1199", VideoInfo("h264", "408", "720", "29.97"), AudioInfo("aac"))
      val json = {
        Source.fromFile(resources + "/h264json.txt").getLines().mkString(Properties.lineSeparator)
      }
      val actual = MediaInfoFactory.getMediaInfo(json)
      assert(expected == actual)
    }
    it("should retrieve metadata of a mpeg2 video from json strings") {
      val expected = MediaInfo("129.330000", "876", VideoInfo("mpeg2video", "720", "932", "29.97"),AudioInfo("aac"))
      val json = {
        Source.fromFile(resources + "/mpeg2json.txt").getLines().mkString(Properties.lineSeparator)
      }
      val actual = MediaInfoFactory.getMediaInfo(json)
      assert(expected == actual)
    }
    it("should retrieve metadata of a flv video from json strings") {
      val expected = MediaInfo("21.420368", "430", VideoInfo("vp6f", "320", "240", "30.00"),AudioInfo("mp3"))
      val json = {
        Source.fromFile(resources + "/flvjson.txt").getLines().mkString(Properties.lineSeparator)
      }
      val actual = MediaInfoFactory.getMediaInfo(json)
      assert(expected == actual)
    }
  }
}
