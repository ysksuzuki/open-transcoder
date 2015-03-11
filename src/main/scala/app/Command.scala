package app

import java.io.File
import model._
import scala.sys.process.Process
import org.slf4j.LoggerFactory

trait CommandBase {
  val command: String
  val logLevel: LogLevel
  val logger = LoggerFactory.getLogger(logLevel.logger)
  def execute(params: Seq[String] = Seq(), file: Option[File] = None): String = {
    logger.info(s"command: ${command.format(params: _*)}")
    val result = executeCommand(params, file)
    logger.info("command end.")
    result
  }
  protected def executeCommand(params: Seq[String], file: Option[File] = None): String
}

class BasicCommand(val command: String, val logLevel: LogLevel) extends CommandBase {
  override protected def executeCommand(params: Seq[String], file: Option[File]): String = {
    Process(command.format(params: _*)).!!
  }
}

class WithEnvCommand(val command: String, val logLevel: LogLevel, val extraEnv: (String, String)*) extends CommandBase {
  override protected def executeCommand(params: Seq[String], file: Option[File]): String = {
    Process(command.format(params: _*), None, extraEnv: _*).!!
  }
}

class RedirectCommand(val command: String, val logLevel: LogLevel) extends CommandBase {
  protected def executeCommand(params: Seq[String], file: Option[File]): String = {
    file map { file =>
      (Process(command.format(params: _*)) #>> file).!!
    } getOrElse {""}
  }
}

object Command {
  def apply(commandType: CommandType, command: String, logLevel: LogLevel) = {
    commandType match {
      case BasicCommand => new BasicCommand(command, logLevel)
      case RedirectCommand => new RedirectCommand(command, logLevel)
    }
  }
  def apply(command: String, logLevel: LogLevel, extraEnv: (String, String) *) = {
     new WithEnvCommand(command, logLevel, extraEnv: _*)
  }
}
