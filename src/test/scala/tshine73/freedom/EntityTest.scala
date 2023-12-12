package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import tshine73.freedom.market.entity.{Location, ProductPriceEntity, Units}
import tshine73.freedom.stock.entity.PriceEntity
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.{DateUtils, IOUtils}

import scala.collection.immutable.List

class EntityTest extends AnyFunSuite {
  test("generate price dynamodb items") {
    val priceEntity = PriceEntity("2330.TW", DateUtils.parseDate("2022-11-01"), 73.73)
    val itemMap = PriceEntity.generateDynamodbItem(priceEntity)

    assert(priceEntity.code == itemMap("code").s)
    assert(priceEntity.date.toString(DateUtils.dateFormat) == itemMap("date").s)
    assert(priceEntity.price == itemMap("price").n.toDouble)
  }

  test("generate product price dynamodb items") {
    val productPriceEntity = ProductPriceEntity(Int.MaxValue, DateUtils.parseDate("1988-03-12"), "item", 22.0, 0, Units.KG, Location.Costco, "c1", capacity = Some(300))
    val itemMap = ProductPriceEntity.generateDynamodbItem(productPriceEntity)

    assert(productPriceEntity.id == itemMap("id").n.toInt)
    assert(productPriceEntity.date.toString(DateUtils.dateFormat) == itemMap("date").s)
    assert(productPriceEntity.item == itemMap("item").s)
    assert(productPriceEntity.price == itemMap("price").n.toDouble)
    assert(productPriceEntity.count == itemMap("count").n.toInt)
    assert(productPriceEntity.unit.toString == itemMap("unit").s)
    assert(productPriceEntity.location.toString == itemMap("location").s)
    assert(productPriceEntity.category == itemMap("category").s)
    assert(productPriceEntity.promotion == itemMap("promotion").bool())
    assert(productPriceEntity.brand.getOrElse("") == itemMap("brand").s)
    assert(productPriceEntity.capacity.get == itemMap("capacity").n.toDouble)
  }

  test("parse google sheet data") {
    val head = List("date", "item", "price", "count", "units of measurement", "capacity", "location", "category", "promotion", "brand")
    val data = List(
      List("2023-10-25", "Olive Oil", "499", "1", "KG", "2.00", "Costco", "Sauce", "FALSE", "Kirkland"),
      List("2023-10-25", "Tofu", "40", "3", "PIECE", "", "Costco", "Ingredient", "FALSE"),
      List("2023-10-25", "Tofu", "ad", "3", "PIECE", "", "Costco", "Ingredient", "FALSE")
    )
    val entities: List[Either[Throwable, ProductPriceEntity]] = ProductPriceEntity.parseGoogleSheetData(head, data)

    assert(entities.filter(_.isRight).size == 2)

    for
      first <- entities.head
      second <- entities(1)
    do
      assert(first.date.toString(DateUtils.dateFormat) == data.head(0))
      assert(first.item == data.head(1))
      assert(first.price == data.head(2).toDouble)
      assert(first.capacity.get == data.head(5).toDouble)
      assert(first.brand.get == data.head(9))

      assert(second.date.toString(DateUtils.dateFormat) == data(1)(0))
      assert(second.item == data(1)(1))
      assert(second.price == data(1)(2).toDouble)
      assert(second.capacity.isEmpty)
      assert(second.brand.isEmpty)
  }

}
