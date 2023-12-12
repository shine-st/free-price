package tshine73.freedom.stock.entity

import org.joda.time.DateTime
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils}

case class PriceEntity(code: String, date: DateTime, price: Double, range: String = "") {
  override def toString: String = {
    f"code [$code], date [$date], price [$price]"
  }
}

object PriceEntity {
  val tableName = "price"

  def generateDynamodbItem(priceEntity: PriceEntity) =
    val itemValues = collection.mutable.Map.empty[String, AttributeValue]

    itemValues.put("code", AttributeValue.builder().s(priceEntity.code).build())
    itemValues.put("date", AttributeValue.builder().s(priceEntity.date.toString(DateUtils.dateFormat)).build())
    itemValues.put("price", AttributeValue.builder().n(priceEntity.price.toString).build())

    itemValues.toMap


}
