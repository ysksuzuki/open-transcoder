package util

import java.io.File
import test.UnitSpec
import scala.io.Source
import scala.util.Properties
import scalax.file.Path
import scalax.file.PathMatcher.All

class IOUtiSpec extends UnitSpec {

  val url = "http://ciscobinary.openh264.org/openh264-linux64-v1.3.zip"
  val resources = "src/test/resources/util"

  before {
    Path.fromString(resources) * All foreach(_ deleteRecursively())
  }

  after {
    Path.fromString(resources) * All foreach(_ deleteRecursively())
  }

  override def beforeAll(): Unit = {
    val path = Path.fromString(resources)
    if (path.nonExistent) {
      path.doCreateDirectory()
    }
  }

  override def afterAll(): Unit = {
  }

  describe("download") {
    it("should download a file which is indicated") {
      assert(IOUtil.download(url, resources).get == "openh264-linux64-v1.3.zip")
    }
  }

  describe("writeText") {
    it("should write texts to a file which is indicated") {
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
  }

  describe("Extension") {
    it("should extract an extension from a file name") {
      url match {
        case Extension(extension) => assert("zip" == extension)
        case _ => fail()
      }
    }
    describe("when empty") {
      it("should return None") {
        val file = ""
        file match {
          case Extension(extension) => fail(s"extension = ${extension}")
          case _ => assert(true)
        }
      }
    }
    describe("when no full stop") {
      it("should return None") {
        val file = "/var/tmp/file"
        file match {
          case Extension(extension) => fail(s"extension = ${extension}")
          case _ => assert(true)
        }
      }
    }
  }

  describe("Filename") {
    it("should extract a file name from an uri") {
      url match {
        case Filename(name) => assert("openh264-linux64-v1.3.zip" == name)
        case _ => fail()
      }
    }
    describe("when empty") {
      it("should return None") {
        val file = ""
        file match {
          case Filename(name) => fail(s"name = ${name}")
          case _ => assert(true)
        }
      }
    }
    describe("when no separator") {
      it("should return a file name") {
        val file = "file.txt"
        file match {
          case Filename(name) => assert(name == file)
          case _ => fail()
        }
      }
    }
  }

  describe("Parent") {
    it("should extract parents from an uri") {
      val path = "/var/tmp/openh264-linux64-v1.3.zip"
      path match {
        case Parent(parent) => assert(List("", "var", "tmp") == parent)
        case _ => fail()
      }
    }
    describe("when empty") {
      it("should return None") {
        val file = ""
        file match {
          case Parent(parent) => fail(s"parent = ${parent}")
          case _ => assert(true)
        }
      }
    }
    describe("when no parent") {
      it("should return None") {
        val file = "file.txt"
        file match {
          case Parent(parent) => fail(s"parent = ${parent}")
          case _ => assert(true)
        }
      }
    }
  }

  describe("fileSearch") {
    it("should search files from a directory recursively") {
      val expected = List(
        new File("src/test/resources/util/hoge/fuga.txt"),
        new File("src/test/resources/util/hoge.txt"),
        new File("src/test/resources/util/hoge/fuga/fuga.txt")
      )
      prepareFiles()
      val result = IOUtil.fileSearch(new File(resources))
      assert(result == expected)
    }

    it("should search and filters files from a directory recursively") {
      val expected = List(
        new File("src/test/resources/util/hoge/fuga.txt"),
        new File("src/test/resources/util/hoge/fuga/fuga.txt")
      )
      prepareFiles()
      val result = IOUtil.fileSearch(new File(resources)) { file =>
        val pattern = """(fuga).*""".r
        file.getName match {
          case pattern(n) => true
          case _ => false
        }
      }
      assert(result == expected)
    }
  }

  def prepareFiles() = {
    Path.fromString(resources + "/hoge.txt").doCreateFile()
    Path.fromString(resources + "/hoge").doCreateDirectory()
    Path.fromString(resources + "/hoge/fuga").doCreateDirectory()
    Path.fromString(resources + "/hoge/fuga.txt").doCreateFile()
    Path.fromString(resources + "/hoge/fuga/fuga.txt").doCreateFile()
  }
}
