package model

case class StringProperty (
  key: String,
  value: String,
  indent: String = "    ",
  separator: String = " = "
) {
  override def toString() = {
    val quote = {
      if (escapePattern(value)) "\"\"\""
      else "\""
    }
    s"""${indent}${key}${separator}${quote}${value}${quote}"""
  }
  private def escapePattern(value: String) = value.matches("""^.*[\\'"${}].*$""")
}
