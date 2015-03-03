package app

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import installer.{ConfigManager, InstallManager}
import model._
import org.slf4j.LoggerFactory

import scala.io.Source

object AppMain {

  def main(args: Array[String]): Unit = {
    val subCommand = args.headOption.map { sub =>
        if (sub.startsWith("-")) "" else sub
      } getOrElse("")
    val status: ExitStatus = {
      SubCommand(subCommand) map {
        case Transcode => {
          val transArgs = {
            if (subCommand.isEmpty) args
            else args.tail
          }
          transcode(transArgs)
        }
        case Config => configure()
        case Install => install()
      } getOrElse(Failure)
    }
    System.exit(status.code)
  }

  private def transcode(args: Array[String]) = {
    parseArgs(args.mkString(" ")) map { commandArgs =>
      val logger = Logger(LoggerFactory.getLogger(commandArgs.logLevel.logger))
      logger.info(s"transcode start. args=${args.mkString(" ")}")
      try {
        val config = loadConfig()
        Transcoder.transcode(config, commandArgs)
        Success
      } catch {
        case ex: Throwable => {
          println(ex.getMessage)
          Failure
        }
      }
    } getOrElse { Failure }
  }

  private def configure() = {
    try {
      ConfigManager.configure()
      println(
        s"""configure succeeded.
           |open-transcoder.conf was created here.
           |If you want change settings, please modify it.
           |After that, run install.
           |
           |[command]
           |java -jar open-transcoder.jar install
         """.stripMargin)
      Success
    } catch {
      case ex: Throwable => {
        println(s"configure failed!!")
        println(ex.getMessage)
        Failure
      }
    }
  }

  private def install() = {
    try {
      val config = loadConfig()
      InstallManager.install(config)
      val dollar = "$"
      println(
        s"""
           |install succeeded.
           |OpenH264 library was installed in ${config.getString("openh264.dest")}.
           |
           |OpenH264 Video Codec provided by Cisco Systems, Inc.
           |License:
           |http://www.openh264.org/BINARY_LICENSE.txt"
           |
           |Please add your shared library path.
           |Linux:
           | export LD_LIBRARY_PATH=${config.getString("openh264.dest")}:${dollar}LD_LIBRARY_PATH
           |Windows:
           |
           |
         """.stripMargin)
      Success
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
        Failure
      }
    }
  }

  private def loadConfig() = {
    val file = new File("open-transcoder.conf")
    if (file.exists()) ConfigFactory.parseFile(file)
    else ConfigFactory.load()
  }

  private def parseArgs(args: String): Option[CommandLineArgs] = {
    val opts: Map[String, (String, String)] = parse(args)
    if (opts.contains("h") || opts.contains("-help")) {
      printHelp()
      None
    } else if (opts.contains("li") || opts.contains("-license")) {
      printLicense()
      None
    } else {
      try {
        val in = checkArg {
          opts.getOrElse("i", opts.getOrElse("-input", throw new Exception("error: -i or --input is required")))._1
        } { in: String =>
          if (!in.isEmpty) in
          else throw new Exception("error: input file is not specified")
        }
        val (out1, out2) = checkArg {
          opts.getOrElse("o", opts.getOrElse("-output", throw new Exception("error: -o or --output is required")))
        } { out =>
          if (!out._1.isEmpty) out
          else throw new Exception("error: output file is not specified")
        }
        val logLevel = opts.getOrElse("l", opts.getOrElse("-logLevel", (Info.level, "")))._1
        Some(
          CommandLineArgs(in, out1, out2, LogLevel(logLevel).getOrElse(Info))
        )
      } catch {
        case ex: Throwable => {
          println(ex.getMessage)
          printHelp()
          None
        }
      }
    }
  }

  private def parse(args: String): Map[String, (String, String)] = {
    val OptPattern = """-(\S+)\s*([^-]\S+)?\s*([^-]\S+)?""".r
    OptPattern.findAllIn(args).matchData.map { m =>
      m.group(1) -> (strCheck(m.group(2)), strCheck(m.group(3)))
    }.toMap
  }

  private def strCheck(str: String) = {
    if (str == null) ""
    else str
  }

  private def checkArg[T](f: => T)(validate: T => T) = {
    trye {
      f
    }.right.map(validate) match {
      case Right(a) => a
      case Left(e) => throw e
    }
  }

  private def trye[T](f: => T)(implicit onError: Throwable => Either[Throwable,T] = { t:Throwable => Left(t) }): Either[Throwable,T] = {
    try{
      Right(f)
    } catch {
      case c: Throwable => onError(c)
    }
  }

  private def printHelp() = {
    println(
      """
        |open-transcoder
        | Media transcoding tool with FFmpeg and OpenH264
        |
        |OpenH264 Video Codec provided by Cisco Systems, Inc.
        |
        |[Sub Command]
        | transcode (default)
        | config
        | install
        |
        |[Usage - transcode]
        |java -jar open-transcoder.jar [options]
        |
        |[options]
        |-i  | --input  <file>    (required)
        |-o  | --output <file>... (required)
        |-l  | --logLevel <quiet|error|info|warning>
        |-h  | --help
        |-li | --license
        |
        |[Example]
        |# mp4
        | java -jar open-transcoder.jar -i input.mp4 -o output.mp4 -l quiet
        |# hls
        | java -jar open-transcoder.jar -i input.mp4 -o playlist.m3u8 -l quiet
        |# webm
        | java -jar open-transcoder.jar -i input.mp4 -o output.webm -l quiet
        |
        |[Usage - config]
        |java -jar open-transcoder.jar config
        |
        |[Usage - install]
        |java -jar open-transcoder.jar install
        |
      """.stripMargin)
  }

  private def printLicense() = {
    Source.fromURL("http://www.openh264.org/BINARY_LICENSE.txt").getLines().foreach(println)
  }

}


