package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.utils.{DateUtils, IOUtils}
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.core.PriceEntity

class CoreTest extends AnyFunSuite {
  test("generate price dynamodb items") {
    val priceEntity = PriceEntity("2330.TW", DateUtils.parseDate("2022-11-01"), 73.73)
    val itemMap = PriceEntity.generateDynamodbItem(priceEntity)

    assert(priceEntity.code == itemMap("code").s)
    assert(priceEntity.date.toString(DateUtils.dateFormat) == itemMap("date").s)
    assert(priceEntity.price == itemMap("price").n.toDouble)
  }


}
