package tshine73.freedom.market

import org.joda.time.DateTime
import tshine73.freedom.core.{Location, ProductPriceEntity, Units}
import tshine73.freedom.utils.GoogleSheetUtils
import tshine73.freedom.core.Location.*
import tshine73.freedom.utils.aws.DynamoDBUtils

@main
def main(parameters: String*): Unit =
  val priceSheetId = "1ZBHuHyJCz6OpCkpfGEZ5ADDsli58vkGJEmHLTPm0wOU"
  //  println(GoogleSheetUtils.getValues(priceSheetId, "ProductPrice!A2:J20"))


//  val entity = ProductPriceEntity("", DateTime.now(), "onion", 32.2, 1, Units("PIECE"), Location("Costco"), "Ingredient")
//  DynamoDBUtils.putItem(ProductPriceEntity.generateDynamodbItem(entity), ProductPriceEntity.tableName)
