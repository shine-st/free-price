package tshine73.freedom.utils

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat

object DateUtils {
  val dateFormat = "yyyy-MM-dd"

  def timestampToDate(timestamp: Int) = {
    new DateTime(timestamp * 1000L, DateTimeZone.UTC)
  }

  def parseDate(date:String) = {
    DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC)
      .parseDateTime(date)
  }

  def dateToTimestamp(date:DateTime) = {
    date.getMillis / 1000
  }
}
