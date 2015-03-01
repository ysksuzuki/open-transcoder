package util

import java.io.File

import scalax.io.Resource
import sbt._

object IOUtil {

  def writeText(file: String, text: String, append: Boolean = false) = {
    Using.fileWriter(IO.utf8, append)(new File(file)) { out => out.write(text) }
  }

  def extension(uri: String) = {
    uri match {
      case Extension(extension) => Some(extension)
      case _ => None
    }
  }
  def filename(uri: String) = {
    uri match {
      case Filename(filename) => Some(filename)
      case _ => None
    }

  }
  def download(url: String, dest: String = "data") = {
    val data = Resource.fromURL(url).byteArray
    url match {
      case Filename(file) => {
        Resource.fromFile(new java.io.File(dest, file)).write(data)
        Some(file)
      }
      case _ => None
    }
  }
}

trait FileExtractor {
  def extract(uri: String, separator: String) = {
   uri.split(separator).reverse.toList.headOption
  }
}

object Filename extends FileExtractor {
 def unapply(url: String) = extract(url, "/")
}

object Extension extends FileExtractor {
  def unapply(path: String): Option[String] = extract(path, "\\.")
}
