package installer

import test.UnitSpec

class ConfigManagerSpec extends UnitSpec {
  "The execute function" should "execute a command which transcodes a media file to mp4" in {
    ConfigManager.configure()
  }
}
