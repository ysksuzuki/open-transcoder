package installer

import java.io.File

import test.UnitSpec

import scalax.file.Path
import scalax.file.PathMatcher.All

class ConfigManagerSpec extends UnitSpec {

  val resources = "src/test/resources/installer"
  val conf = "/open-transcoder.conf"

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

  describe("configure") {
    it("should create a configuration file") {
      ConfigManager().configure(resources + conf)
      assert(new File(resources + conf).exists())
    }
  }
}
