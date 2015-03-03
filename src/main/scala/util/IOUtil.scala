package util

import java.io.File

import scala.annotation.tailrec
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

  def fileSearch(file: File)(implicit f: File => Boolean = f => true) = {
    @tailrec
    def fileSearchInner(files: List[File], result: List[File]): List[File] = {
      if (files.forall(_.isFile)) {
        result ::: files.filter(f)
      } else {
        fileSearchInner(files.filter(_.isDirectory).flatMap(_.listFiles()), files.filter(_.isFile).filter(f) ::: result)
      }
    }
    fileSearchInner(List(file), Nil)
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
