package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.DateUtils.*

class CrawlerTest extends AnyFunSuite {

  test("crawl TSMC price on 2022-11-01") {
    val date = parseDate("2022-11-01")
    assert(391.5 == PriceCrawler.fetchPrice("2330.TW", date))
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

    val pricesMap = PriceCrawler.parseYahooJson(json)
    assert(391.5 == pricesMap("2022-11-01"))
    assert(395 == pricesMap("2022-11-02"))
  }
}