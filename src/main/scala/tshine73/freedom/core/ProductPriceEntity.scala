package tshine73.freedom.core

import org.joda.time.DateTime
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.utils.DateUtils

case class ProductPriceEntity(date: DateTime, item: String, price: Double, count: Int, unit: String, capacity: Int, location: String, category: String, promotion: Boolean):
  override def toString: String =
    f"date [$date], item [$item], price [$price]"


object ProductPriceEntity:
  val tableName = "ProductPrice"

  def generateDynamodbItem(entity: ProductPriceEntity) =
    val itemValues = collection.mutable.Map.empty[String, AttributeValue]

    itemValues.put("date", AttributeValue.builder().s(entity.date.toString(DateUtils.dateFormat)).build())
    itemValues.put("item", AttributeValue.builder().s(entity.item).build())
    itemValues.put("price", AttributeValue.builder().n(entity.price.toString).build())
    itemValues.put("count", AttributeValue.builder().n(entity.count.toString).build())
    itemValues.put("unit", AttributeValue.builder().s(entity.unit).build())
    itemValues.put("capacity", AttributeValue.builder().n(entity.capacity.toString).build())
    itemValues.put("location", AttributeValue.builder().s(entity.location).build())
    itemValues.put("category", AttributeValue.builder().s(entity.category).build())
    itemValues.put("promotion", AttributeValue.builder().bool(entity.promotion).build())

    itemValues.toMap

