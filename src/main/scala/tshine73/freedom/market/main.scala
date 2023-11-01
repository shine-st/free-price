package tshine73.freedom.market

import com.google.api.services.sheets.v4.model.ValueRange
import org.joda.time.DateTime
import tshine73.freedom.core.{Location, ProductPriceEntity, Units}
import tshine73.freedom.core.ProductPriceEntity.*
import tshine73.freedom.utils.GoogleSheetUtils
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

  var maximumId = DynamoDBUtils.getMaximumValue(tableName, primaryIdIndexName, primaryIdColumnName, staticValueMap)
    .get(primaryIdColumnName)
    .map(_.s().toInt)
    .getOrElse(0)

  
  maximumId += 1
  for
    e <- entities
    entity <- e
  do
    DynamoDBUtils.putItem(generateDynamodbItem(entity.copy(id = maximumId.toString)), tableName)
    maximumId += 1


//    val entires = GoogleSheetUtils.
//    values.tail.map(v => v.zipw)


//  val entity = ProductPriceEntity("", DateTime.now(), "onion", 32.2, 1, Units("PIECE"), Location("Costco"), "Ingredient")
//  DynamoDBUtils.putItem(ProductPriceEntity.generateDynamodbItem(entity), ProductPriceEntity.tableName)
