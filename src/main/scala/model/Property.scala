package model

case class StringProperty (
  key: String,
  value: String,
  indent: String = "    ",
  separator: String = " = "
) {
  override def toString() = s"""${indent}${key}${separator}"${value}""""
}
