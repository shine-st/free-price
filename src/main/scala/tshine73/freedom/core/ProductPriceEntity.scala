package tshine73.freedom.core

import org.joda.time.DateTime
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.utils.DateUtils


enum Units:
  case KG, PIECE, G, TKG

object Units:
  def apply(s: String) = Units.valueOf(s)

enum Location(val name: String):
  case Costco extends Location("Costco")
  case PxMart extends Location("全聯")
  case Market extends Location("Market")
  case RtMart extends Location("大潤發")
end Location

object Location:
  def apply(s: String) = s.toLowerCase match
    case "costco" => Costco
    case "全聯" => PxMart
    case "market" => Market
    case "大潤發" => RtMart

case class ProductPriceEntity(
                               date: DateTime,
                               item: String,
                               price: Double,
                               count: Int,
                               unit: Units,
                               location: Location,
                               category: String,
                               promotion: Boolean = false,
                               capacity: Option[Int] = None,
                               brand: Option[String] = None
                             ):
  override def toString: String =
    f"date [$date], item [$item], price [$price]"


object ProductPriceEntity:
  val tableName = "Product-Price"

  def generateDynamodbItem(entity: ProductPriceEntity) =
    val itemValues = collection.mutable.Map.empty[String, AttributeValue]

    itemValues.put("date", AttributeValue.builder().s(entity.date.toString(DateUtils.dateFormat)).build())
    itemValues.put("item", AttributeValue.builder().s(entity.item).build())
    itemValues.put("price", AttributeValue.builder().n(entity.price.toString).build())
    itemValues.put("count", AttributeValue.builder().n(entity.count.toString).build())
    itemValues.put("unit", AttributeValue.builder().s(entity.unit.toString).build())
    itemValues.put("location", AttributeValue.builder().s(entity.location.name).build())
    itemValues.put("category", AttributeValue.builder().s(entity.category).build())
    itemValues.put("promotion", AttributeValue.builder().bool(entity.promotion).build())
    itemValues.put("brand", AttributeValue.builder().s(entity.brand.getOrElse("")).build())

    if entity.capacity.nonEmpty then
      itemValues.put("capacity", AttributeValue.builder().n(entity.capacity.map(_.toString).getOrElse("")).build())

    itemValues.toMap

