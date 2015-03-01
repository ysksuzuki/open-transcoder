package util

import scala.util.Properties

object PlatformUtil {
  val os = Properties.osName.toLowerCase()
  def isLinux = os.startsWith("linux")
  def isWindows = os.startsWith("windows")
}
