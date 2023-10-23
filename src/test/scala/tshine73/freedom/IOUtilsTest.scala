package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.utils.{DateUtils, IOUtils}
import tshine73.freedom.utils.DateUtils.*

class IOUtilsTest extends AnyFunSuite {

  test("read first line") {
    val fileName = "stocks"
    assert(IOUtils.readFile(fileName).head == "AAPL,Price!B3")
  }

}
