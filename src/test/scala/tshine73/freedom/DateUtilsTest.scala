package tshine73.freedom

import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.utils.DateUtils.*

class DateUtilsTest extends AnyFunSuite {
  
  test("timestamp to date string") {
    assert("2022-11-01" == timestampToDate(1667260800).toString(dateFormat))

    assert("2022-10-31" == timestampToDate(1667260800, "EST").toString(dateFormat))
  }


  test("parse date string to DateTime") {
    val date = parseDate("2022-11-01")
    assert("2022-11-01 00:00:00" == date.toString("yyyy-MM-dd HH:mm:ss"))
    assert(DateTimeZone.UTC == date.getZone)
  }

  test("date to UTC timestamp") {
    val date = parseDate("2022-11-01").withZone(DateTimeZone.forID("Asia/Taipei"))
    assert("2022-11-01 08:00:00" == date.toString("yyyy-MM-dd HH:mm:ss"))
    assert(1667260800 == dateToTimestamp(date))
  }

}
