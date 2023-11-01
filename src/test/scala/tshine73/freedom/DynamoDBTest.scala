package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.core.{Location, PriceEntity, ProductPriceEntity, Units}
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.DateUtils
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.aws.DynamoDBUtils
import tshine73.freedom.utils.aws.DynamoDBUtils.*

import scala.jdk.CollectionConverters.*

class DynamoDBTest extends AnyFunSuite {
  val priceEntity = PriceEntity("2330.TW", DateUtils.parseDate("2022-11-01"), 300.05)
  val priceEntityKeyMap = Map("code" -> AttributeValue.builder().s(priceEntity.code).build(), "date" -> AttributeValue.builder().s(priceEntity.date.toString(DateUtils.dateFormat)).build())

  val productPriceEntity = ProductPriceEntity(Int.MaxValue.toString, DateUtils.parseDate("1988-03-12"), "item", 0.0, 0, Units.KG, Location.Costco, "")
  val productPriceEntityKeyMap = Map("id" -> AttributeValue.builder().s(productPriceEntity.id).build())


  test("write data") {
    assert(putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName))
    deleteItem(priceEntityKeyMap, PriceEntity.tableName)
  }

  test("get item by primary key") {
    putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName)
    val item = DynamoDBUtils.getItemByKey(priceEntityKeyMap, PriceEntity.tableName)

    assert(priceEntity.price.toString == item("price").n())
    deleteItem(priceEntityKeyMap, PriceEntity.tableName)
  }

  test("delete by primary key") {
    assert(putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName))
    assert(deleteItem(priceEntityKeyMap, PriceEntity.tableName))
  }

  test("get maximum value from table") {
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity), ProductPriceEntity.tableName)
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity.copy(id = "1234")), ProductPriceEntity.tableName)
    val maximumValue = getMaximumValue(ProductPriceEntity.tableName, ProductPriceEntity.primaryIdIndexName, ProductPriceEntity.primaryIdColumnName, ProductPriceEntity.staticValueMap)(ProductPriceEntity.primaryIdColumnName).s()
    assert(maximumValue == Int.MaxValue.toString)
    deleteItem(productPriceEntityKeyMap, ProductPriceEntity.tableName)
    deleteItem(Map("id" -> AttributeValue.builder().s("1234").build()), ProductPriceEntity.tableName)
  }
}