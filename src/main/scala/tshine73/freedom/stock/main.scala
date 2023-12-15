import org.joda.time.DateTime
import tshine73.freedom.stock.core.StockPriceCore
import tshine73.freedom.stock.entity.PriceEntity
import tshine73.freedom.utils.aws.DynamoDBUtils
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils, IOUtils}

import scala.util.Try

//import scala.annotation.MainAnnotation.Parameter

val tableName = "price"
@main
def main(parameters: String*): Unit =
  val date = parameters match
    case head :: _ => DateUtils.parseDate(head)
    case Nil => DateTime.now().minusDays(1).withMillisOfDay(0)


  val priceEntities = StockPriceCore.prepareStockCode()
    .map(codeInfoArr =>
      val code = codeInfoArr._1
      val priceEither = Try(StockPriceCore.fetchPrice(code, date)).toEither
      val priceEntity = PriceEntity(code, date, priceEither.getOrElse(-1), codeInfoArr._2)
      println(priceEntity)
      priceEntity
    )

  priceEntities.filter(_.price != -1)
    .foreach(priceEntity => {
      save(priceEntity)
      writeSheet(priceEntity)
    })


def save(priceEntity: PriceEntity) =
  DynamoDBUtils.putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName)

def writeSheet(priceEntity: PriceEntity) =
  GoogleSheetUtils.updateValue(StockPriceCore.assetSpreadsheetId, priceEntity.range, priceEntity.price)
