package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.market.entity.{Location, ProductPriceEntity, Units}
import tshine73.freedom.stock.core.StockPriceCore
import tshine73.freedom.stock.entity.PriceEntity
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.{DateUtils, IOUtils}

import scala.collection.immutable.List

class CoreTest extends AnyFunSuite {

  test("prepare stock code") {
    val codes = StockPriceCore.prepareStockCode()
    assert(codes(0)._1 == "^TWII")
    assert(codes(0)._2 == "C2")
  }

}
