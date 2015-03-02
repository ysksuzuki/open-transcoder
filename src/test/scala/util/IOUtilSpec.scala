package util

import test.UnitSpec
import scala.io.Source
import scala.util.Properties
import scalax.file.Path
import scalax.file.PathMatcher.All

class IOUtiSpec extends UnitSpec {

  val url = "http://ciscobinary.openh264.org/openh264-linux64-v1.3.zip"
  val resources = "src/test/resources/util"

  before {
    Path.fromString(resources) * All foreach(_ delete())
  }

  after {
    Path.fromString(resources) * All foreach(_ delete())
  }

  "The download function" should "downloads a file which is indicated" in {
    assert(IOUtil.download(url, resources).get == "openh264-linux64-v1.3.zip")
  }

  "The writeText function" should "writes a text to a file which is indicated" in {
    val text =
      """
        |This is a sample text.
        |hogehoge
        |fugafuga
      """.stripMargin
    val appendText =
      """
        |
        |This is a append text.
      """.stripMargin
    val file = resources + "/writeText.txt"
    IOUtil.writeText(file, text)
    var source = Source.fromFile(file)
    assert(text == source.getLines().mkString(Properties.lineSeparator))

    IOUtil.writeText(file, appendText, true)
    source = Source.fromFile(file)
    assert(text + appendText == source.getLines().mkString(Properties.lineSeparator))

  }

  "The extension function" should "extracts an extension from a file name" in {
    assert("zip" == IOUtil.extension(url).get)
  }

  "The filename function" should "extracts an file name from an uri" in {
    assert("openh264-linux64-v1.3.zip" == IOUtil.filename(url).get)
  }
}
