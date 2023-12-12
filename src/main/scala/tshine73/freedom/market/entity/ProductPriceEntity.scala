package tshine73.freedom.market.entity

import com.google.api.services.sheets.v4.model.ValueRange
import org.joda.time.DateTime
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.utils.DateUtils

import scala.jdk.CollectionConverters.*
import scala.util.Try


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
                               id: Int,
                               date: DateTime,
                               item: String,
                               price: Double,
                               count: Int,
                               unit: Units,
                               location: Location,
                               category: String,
                               promotion: Boolean = false,
                               capacity: Option[Double] = None,
                               brand: Option[String] = None,
                               staticValue: String = ProductPriceEntity.staticValueMap("static-value")
                             ):
  override def toString: String =
    f"id[$id], date [$date], item [$item], price [$price]"


object ProductPriceEntity:
  val tableName = "product-price"
  val staticValueMap = Map("static-value" -> "1")
  val primaryIdIndexName = "static-value-id-index"
  val primaryIdColumnName = "id"
  val dateIndexName = "date-index"

  def generateDynamodbItem(entity: ProductPriceEntity) =
    val itemValues = collection.mutable.Map.empty[String, AttributeValue]

    itemValues.put("id", AttributeValue.builder().n(entity.id.toString).build())
    itemValues.put("date", AttributeValue.builder().s(entity.date.toString(DateUtils.dateFormat)).build())
    itemValues.put("item", AttributeValue.builder().s(entity.item).build())
    itemValues.put("price", AttributeValue.builder().n(entity.price.toString).build())
    itemValues.put("count", AttributeValue.builder().n(entity.count.toString).build())
    itemValues.put("unit", AttributeValue.builder().s(entity.unit.toString).build())
    itemValues.put("location", AttributeValue.builder().s(entity.location.name).build())
    itemValues.put("category", AttributeValue.builder().s(entity.category).build())
    itemValues.put("promotion", AttributeValue.builder().bool(entity.promotion).build())

    if entity.capacity.nonEmpty then
      itemValues.put("capacity", AttributeValue.builder().n(entity.capacity.map(_.toString).getOrElse("")).build())

    if entity.brand.isDefined then
      itemValues.put("brand", AttributeValue.builder().s(entity.brand.get).build())

    itemValues.put("static-value", AttributeValue.builder().s(entity.staticValue).build())

    itemValues.toMap

  def parseGoogleSheetData(head: List[String], data: List[List[String]]) =
    val headMap = head.zipWithIndex.toMap
    data.map(values =>
      Try {
        val capacity = headMap.get("capacity")
          .map(values(_))
          .filter(_ != "")
          .map(_.toDouble)

        val brand = headMap.get("brand")
          .filter(i => i < values.size)
          .map(values(_))


        ProductPriceEntity(
          -1,
          DateUtils.parseDate(values(headMap("date"))),
          values(headMap("item")),
          values(headMap("price")).toDouble,
          values(headMap("count")).toInt,
          Units(values(headMap("units of measurement"))),
          Location(values(headMap("location"))),
          values(headMap("category")),
          values(headMap("promotion")).toBoolean,
          capacity,
          brand,
          staticValueMap("static-value")
        )
      }.toEither
    )