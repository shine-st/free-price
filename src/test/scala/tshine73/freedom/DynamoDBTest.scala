package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.core.PriceEntity
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.DateUtils
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.aws.DynamoDBUtils

class DynamoDBTest extends AnyFunSuite {
  val priceEntity = PriceEntity("2330.TW", DateUtils.parseDate("2022-11-01"), 300.05)
  val tableName = PriceEntity.tableName

  def getKey() = {
    Map("code" -> AttributeValue.builder().s(priceEntity.code).build(),
      "date" -> AttributeValue.builder().s(priceEntity.date.toString(DateUtils.dateFormat)).build()
    )
  }

  def generateItem() = {
    PriceEntity.generateDynamodbItem(priceEntity)
  }

  test("write data") {
    assert(DynamoDBUtils.putItem(generateItem(), tableName))
    DynamoDBUtils.deleteItem(getKey(), tableName)
  }

  test("get by primary key") {
    DynamoDBUtils.putItem(generateItem(), tableName)
    val item = DynamoDBUtils.getItem(getKey(), tableName)

    assert(priceEntity.price.toString == item("price").n())
    DynamoDBUtils.deleteItem(getKey(), tableName)
  }

  test("delete by primary key") {
    assert(DynamoDBUtils.putItem(generateItem(), tableName))
    assert(DynamoDBUtils.deleteItem(getKey(), tableName))
  }
}