package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.core.{Location, PriceEntity, ProductPriceEntity, Units}
import tshine73.freedom.crawler.FinancialCrawler
import tshine73.freedom.utils.DateUtils
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.aws.DynamoDBUtils
import tshine73.freedom.utils.aws.DynamoDBUtils.*

import scala.jdk.CollectionConverters.*

class DynamoDBTest extends AnyFunSuite {
  val priceEntity = PriceEntity("2330.TW", DateUtils.parseDate("2022-11-01"), 300.05)
  val priceEntityKeyMap = Map("code" -> AttributeValue.builder().s(priceEntity.code).build(), "date" -> AttributeValue.builder().s(priceEntity.date.toString(DateUtils.dateFormat)).build())

  val productPriceEntity = ProductPriceEntity(Int.MaxValue, DateUtils.parseDate("1988-03-12"), "item", 0.0, 0, Units.KG, Location.Costco, "")
  val productPriceEntityKeyMap = Map("id" -> AttributeValue.builder().n(productPriceEntity.id.toString).build())


  test("write data") {
    assert(putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName))
    deleteItemByKey(priceEntityKeyMap, PriceEntity.tableName)
  }

  test("get item by primary key") {
    putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName)
    val item = getItemByKey(priceEntityKeyMap, PriceEntity.tableName)

    assert(priceEntity.price.toString == item("price").n())
    deleteItemByKey(priceEntityKeyMap, PriceEntity.tableName)
  }

  test("get item by index key") {
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity), ProductPriceEntity.tableName)
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity.copy(id = productPriceEntity.id - 1)), ProductPriceEntity.tableName)
    val indexKeyMap = Map("date" -> AttributeValue.builder().s(productPriceEntity.date.toString(DateUtils.dateFormat)).build())
    val items = getItemsByIndex(indexKeyMap, ProductPriceEntity.tableName, ProductPriceEntity.dateIndexName)
    assert(items.size == 2)

    deleteItemByIndexKey(indexKeyMap, ProductPriceEntity.tableName, ProductPriceEntity.dateIndexName)
  }

  test("delete item by primary key") {
    assert(putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName))
    assert(deleteItemByKey(priceEntityKeyMap, PriceEntity.tableName))
  }

  test("delete item by index") {
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity), ProductPriceEntity.tableName)
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity.copy(id = productPriceEntity.id - 1)), ProductPriceEntity.tableName)
    val indexKeyMap = Map("date" -> AttributeValue.builder().s(productPriceEntity.date.toString(DateUtils.dateFormat)).build())

    assert(deleteItemByIndexKey(indexKeyMap, ProductPriceEntity.tableName, ProductPriceEntity.dateIndexName))
    assert(getItemsByIndex(indexKeyMap, ProductPriceEntity.tableName, ProductPriceEntity.dateIndexName).size == 0)
  }


  test("get maximum value from table") {
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity), ProductPriceEntity.tableName)
    putItem(ProductPriceEntity.generateDynamodbItem(productPriceEntity.copy(id = 1234)), ProductPriceEntity.tableName)
    val maximumValue = getMaximumValue(ProductPriceEntity.tableName, ProductPriceEntity.primaryIdIndexName, ProductPriceEntity.primaryIdColumnName, ProductPriceEntity.staticValueMap)(ProductPriceEntity.primaryIdColumnName).n().toInt
    assert(maximumValue == Int.MaxValue)
    deleteItemByKey(productPriceEntityKeyMap, ProductPriceEntity.tableName)
    deleteItemByKey(Map("id" -> AttributeValue.builder().n("1234").build()), ProductPriceEntity.tableName)
  }
}