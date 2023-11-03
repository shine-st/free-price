package tshine73.freedom.market

import com.google.api.services.sheets.v4.model.ValueRange
import org.joda.time.DateTime
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.core.{Location, ProductPriceEntity, Units}
import tshine73.freedom.core.ProductPriceEntity.*
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils}
import tshine73.freedom.core.Location.*
import tshine73.freedom.utils.aws.DynamoDBUtils

import scala.jdk.CollectionConverters.*
import scala.util.Try


@main
def main(parameters: String*): Unit =
  val productPriceSheetId = "1ZBHuHyJCz6OpCkpfGEZ5ADDsli58vkGJEmHLTPm0wOU"
  val ranges: ValueRange = GoogleSheetUtils.getValues(productPriceSheetId, "ProductPrice!A1:J")
  val values = ranges.getValues().asScala
  val head = values.head.asScala.map(_.toString).toList
  val data = values.tail.map(_.asScala.map(_.toString).toList).toList
  val entities = ProductPriceEntity.parseGoogleSheetData(head, data)

  //  val dateSet = entities.foldLeft(List.empty[Option[String]])((l, e) => e.toOption.map(_.date.toString(DateUtils.dateFormat)) :: l)
  //    .filter(_.isDefined)
  //    .map(_.get)
  //    .toSet


  var maximumId = DynamoDBUtils.getMaximumValue(tableName, primaryIdIndexName, primaryIdColumnName, staticValueMap)
    .get(primaryIdColumnName)
    .map(_.n().toInt)
    .getOrElse(0)

  println(maximumId)
  maximumId += 1

  val deleted = collection.mutable.Set.empty[String]

  for
    e <- entities
    entity <- e
  do
    if !deleted.contains(entity.date.toString(DateUtils.dateFormat)) then
      val date = entity.date.toString(DateUtils.dateFormat)
      DynamoDBUtils.deleteItemByIndexKey(Map("date" -> AttributeValue.builder().s(date).build()), ProductPriceEntity.tableName, ProductPriceEntity.dateIndexName)
      deleted.add(date)

    DynamoDBUtils.putItem(generateDynamodbItem(entity.copy(id = maximumId)), tableName)
    maximumId += 1
