package installer

import scala.io.Source

object ConfigManager {

  def configure() = {
    Source.fromURL(getClass.getClassLoader.getResource("application.conf")).getLines().foreach(println)
  }
}
