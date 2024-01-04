package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.stock.core.StockPriceCore
import tshine73.freedom.utils.DateUtils
import tshine73.freedom.utils.DateUtils.*

class StockPriceCoreTest extends AnyFunSuite {
  val code = "2330.TW"

  test("crawl TSMC price on 2022-11-01") {
    val date = parseDate("2022-11-01")
    assert(StockPriceCore.fetchPrice(code, date) == 391.5)
  }

  test("generate yahoo financial url") {
    val urlWithoutDate: String = StockPriceCore.getYahooAPI(code)
    assert(!urlWithoutDate.contains("period"))

    val startDate = parseDate("2022-11-01")
    val endDate = parseDate("2022-11-02")

    val urlWithDate: String = StockPriceCore.getYahooAPI(code, Some(startDate), Some(endDate))
    assert(urlWithDate.contains("period"))

    var period1Index = urlWithDate.indexOf("period1")
    var startTimestamp = urlWithDate.slice(period1Index + 8, period1Index + 8 + 10)
    assert(startTimestamp == DateUtils.dateToTimestamp(startDate).toString)

    var period2Index = urlWithDate.indexOf("period2")
    var endTimestamp = urlWithDate.slice(period2Index + 8, period2Index + 8 + 10)
    assert(endTimestamp == DateUtils.dateToTimestamp(endDate).toString)


    val urlWithStartDate: String = StockPriceCore.getYahooAPI(code, Some(startDate))
    period1Index = urlWithStartDate.indexOf("period1")
    startTimestamp = urlWithStartDate.slice(period1Index + 8, period1Index + 8 + 10)

    period2Index = urlWithStartDate.indexOf("period2")
    endTimestamp = urlWithStartDate.slice(period2Index + 8, period2Index + 8 + 10)
    assert(startTimestamp == DateUtils.dateToTimestamp(startDate).toString)
    assert(endTimestamp == DateUtils.dateToTimestamp(startDate.plusDays(1)).toString)
  }


  test("parse yahoo api response") {
    val json =
      """
        |{"chart":{"result":[{"meta":{"currency":"TWD","symbol":"2330.TW","exchangeName":"TAI",
        |    "instrumentType":"EQUITY","firstTradeDate":946947600,"regularMarketTime":1682487002,"gmtoffset":28800,
        |    "timezone":"CST","exchangeTimezoneName":"Asia/Taipei","regularMarketPrice":491.5,"chartPreviousClose":390.0,
        |    "priceHint":2,"currentTradingPeriod":{"pre":{"timezone":"CST","start":1682470800,"end":1682470800,
        |    "gmtoffset":28800},"regular":{"timezone":"CST","start":1682470800,"end":1682487000,"gmtoffset":28800},
        |    "post":{"timezone":"CST","start":1682487000,"end":1682487000,"gmtoffset":28800}},"dataGranularity":"1d",
        |    "range":"","validRanges":["1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"]},"timestamp":[
        |    1667264400,1667350800],"indicators":{"quote":[{"volume":[41426496,22535650],"open":[388.5,391.0],
        |    "low":[386.0,388.5],"close":[391.5,395.0],"high":[393.0,395.0]}],"adjclose":[{"adjclose":[387.1646728515625,
        |    390.62591552734375]}]}}],"error":null}}
        |""".stripMargin

    val pricesMap = StockPriceCore.parseYahooJson(json)
    assert(391.5 == pricesMap("2022-11-01"))
    assert(395 == pricesMap("2022-11-02"))


    val estJson =
      """
        |{"chart":{"result":[{"meta":{"currency":"TWD","symbol":"2330.TW","exchangeName":"TAI",
        |    "instrumentType":"EQUITY","firstTradeDate":946947600,"regularMarketTime":1682487002,"gmtoffset":28800,
        |    "timezone":"EST","exchangeTimezoneName":"America/New_York","regularMarketPrice":491.5,"chartPreviousClose":390.0,
        |    "priceHint":2,"currentTradingPeriod":{"pre":{"timezone":"CST","start":1682470800,"end":1682470800,
        |    "gmtoffset":28800},"regular":{"timezone":"CST","start":1682470800,"end":1682487000,"gmtoffset":28800},
        |    "post":{"timezone":"CST","start":1682487000,"end":1682487000,"gmtoffset":28800}},"dataGranularity":"1d",
        |    "range":"","validRanges":["1d","5d","1mo","3mo","6mo","1y","2y","5y","10y","ytd","max"]},"timestamp":[
        |    1667264400,1667350800],"indicators":{"quote":[{"volume":[41426496,22535650],"open":[388.5,391.0],
        |    "low":[386.0,388.5],"close":[391.5,395.0],"high":[393.0,395.0]}],"adjclose":[{"adjclose":[387.1646728515625,
        |    390.62591552734375]}]}}],"error":null}}
        |""".stripMargin

    val estPricesMap = StockPriceCore.parseYahooJson(estJson)
    print(estPricesMap)
    assert(391.5 == estPricesMap("2022-10-31"))
    assert(395 == estPricesMap("2022-11-01"))
  }
}