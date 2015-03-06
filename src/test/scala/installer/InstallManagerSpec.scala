package installer

import java.io.File
import com.typesafe.config.ConfigFactory
import test.UnitSpec
import scalax.file.Path
import scalax.file.PathMatcher.All

class InstallManagerSpec extends UnitSpec {

  val resources = "src/test/resources/installer"
  val libName = "libopenh264.so.0"
  val infoName = "gmpopenh264.info"

  before {
    Path.fromString(resources) * All foreach(_ deleteRecursively())
    Path.fromString("bin/ffmpeg").copyTo(Path.fromString(resources + "/ffmpeg"))
    Path.fromString("bin/ffprobe").copyTo(Path.fromString(resources + "/ffprobe"))
    Path.fromString("bin/run.sh").copyTo(Path.fromString(resources + "/run.sh"))
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

  describe("install") {
    it("should install a OpenH264 library") {
      val config = ConfigFactory.load()
      InstallManager(config).install()
      assert(new File(resources + "/" + libName).exists())
      assert(new File(resources + "/" + infoName).exists())
    }
  }
}
