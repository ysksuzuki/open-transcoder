package test

import org.scalatest._

abstract class UnitSpec extends FunSpec
                                  with Matchers
                                  with OptionValues
                                  with Inside
                                  with Inspectors
                                  with BeforeAndAfter
                                  with BeforeAndAfterAll
