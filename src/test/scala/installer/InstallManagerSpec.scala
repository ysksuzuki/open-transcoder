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
    Path.fromString("lib/ffmpeg").copyTo(Path.fromString(resources + "/ffmpeg"))
    Path.fromString("lib/ffprobe").copyTo(Path.fromString(resources + "/ffprobe"))
  }

  after {
    Path.fromString(resources) * All foreach(_ deleteRecursively())
  }

  "The install function" should "install a OpenH264 library" in {
    val config = ConfigFactory.load()
    InstallManager.install(config)
    assert(new File(resources + "/" + libName).exists())
    assert(new File(resources + "/" + infoName).exists())
  }
}
