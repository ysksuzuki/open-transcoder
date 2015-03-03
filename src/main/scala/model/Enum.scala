package model

object LogLevel {
  val table = Map(
    Quiet.level -> Quiet,
    Error.level -> Error,
    Warning.level -> Warning,
    Info.level -> Info
  )
  def apply(level: String) = table.get(level)
}

sealed abstract class LogLevel(val level: String, val logger: String) {}

case object Quiet extends LogLevel("quiet", "QuietLogger")
case object Error extends LogLevel("error", "ErrorLogger")
case object Warning extends LogLevel("warning", "WarnLogger")
case object Info extends LogLevel("info", "InfoLogger")
case object Debug extends LogLevel("debug", "DebugLogger")

sealed abstract class CommandType {}

case object BasicCommand extends CommandType
case object RedirectCommand extends CommandType

sealed abstract class ExitStatus(val code: Int) {}

case object Success extends ExitStatus(0)
case object Failure extends ExitStatus(1)

sealed abstract class SubCommand(val subCommand: String) {}

case object Transcode extends SubCommand("transcode")
case object Config extends SubCommand("config")
case object Install extends SubCommand("install")

object SubCommand {
  val table = Map(
    Transcode.subCommand -> Transcode,
    Config.subCommand -> Config,
    Install.subCommand -> Install,
    "" -> Transcode
  )
  def apply(subCommand: String): Option[SubCommand] = table.get(subCommand)
}




