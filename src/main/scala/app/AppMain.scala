package app

import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.Logger
import model._
import org.slf4j.LoggerFactory

import scala.io.Source

object AppMain {


  def configLoad = {
    val config = ConfigFactory.load(config)
    ConfigFactory.load
    ConfigFactory.load("open-transcoder")
  }

  def main(args: Array[String]) {
    parseArgs(args.mkString(" ")) map { commandArgs =>
      val logger = Logger(LoggerFactory.getLogger(commandArgs.logLevel.logger))
      logger.info(s"main start. args=${args.mkString(" ")}")
      val status =
        trye {
          val config = ConfigFactory.load()
          Transcoder.transcode(config, commandArgs)
          Success
        } match {
          case Right(status) => status
          case Left(ex)  => {
            println(ex.getMessage)
            Failure
          }
        }
      System.exit(status.code)
    } getOrElse { System.exit(Failure.code) }
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
      trye {
        val in = checkArg {
          opts.get("i").getOrElse {
            opts.get("-input").getOrElse {
              throw new Exception("error: -i or --input is required")
            }
          }._1
        } { in: String =>
          if (!in.isEmpty) in
          else throw new Exception("error: input file is not specified")
        }
        val (out1, out2) = checkArg {
          opts.get("o").getOrElse {
            opts.get("-output").getOrElse {
              throw new Exception("error: -o or --output is required")
            }
          }
        } { out =>
          if (!out._1.isEmpty) out
          else throw new Exception("error: output file is not specified")
        }
        val logLevel = opts.get("l").getOrElse {
          opts.get("-logLevel").getOrElse(("quiet", ""))
        }._1
        Some(
          CommandLineArgs(in, out1, out2, LogLevel(logLevel))
        )
      } match {
        case Right(a) => a
        case Left(e) => {
          println(e.getMessage)
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
    }.right.map(validate(_)) match {
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
        |[Usage]
        |java -jar open-transcoder.jar [options]
        |
        |[options]
        |-i  | --input  <file>    (required)
        |-o  | --output <file>... (required)
        |-l  | --logLevel <quiet|error|info|warning>
        |-h  | --help
        |-li | --license
      """.stripMargin)
  }

  private def printLicense() = {
    Source.fromURL("http://www.openh264.org/BINARY_LICENSE.txt").getLines().foreach(println _)
  }

}


