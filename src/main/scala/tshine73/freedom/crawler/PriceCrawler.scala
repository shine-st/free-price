package tshine73.freedom.crawler

import sttp.client3.*
import tshine73.freedom.utils.DateUtils
import little.json.*
import little.json.Implicits.{*, given}
import org.joda.time.DateTime

import scala.language.implicitConversions


object PriceCrawler:
  private val yahooApi = "https://query1.finance.yahoo.com/v8/finance/chart/%s?period1=%d&period2=%d&interval=1d&events=history"

  def fetchPrice(code: String, date: DateTime): Double =
    val dateStr = date.toString(DateUtils.dateFormat)
    val backend = HttpClientSyncBackend()
    val start = DateUtils.dateToTimestamp(date)
    val end = start + (3600 * 24)
    val url = yahooApi.format(code, start, end)
    println(f"url: $url")

    val response = basicRequest.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:104.0) Gecko/20100101 Firefox/104.0")
      .get(uri"$url")
      .send(backend)


    response.body
      .map(parseYahooJson)
      .map(_(dateStr)) match {
      case Right(value) => value
      case Left(message) => println(f"fetch price error: $message")
        -1.0
    }

  def parseYahooJson(jsonStr: String) =
    val json = Json.parse(jsonStr)

    val resultMap = json \ "chart" \ "result" \ 0
    val timestamps = (resultMap \ "timestamp").as[Seq[Int]]
    val prices = (resultMap \ "indicators" \ "quote" \ 0 \ "close").as[Seq[Double]]

    timestamps.map(timestamp => DateUtils.timestampToDate(timestamp).toString(DateUtils.dateFormat))
      .zip(prices)
      .toMap
