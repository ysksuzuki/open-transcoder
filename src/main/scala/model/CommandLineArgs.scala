package model

case class CommandLineArgs(
  in: String = "",
  out1: String = "",
  out2: String = "",
  logLevel: LogLevel = Quiet
)


