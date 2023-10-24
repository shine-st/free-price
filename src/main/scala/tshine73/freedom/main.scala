import org.joda.time.DateTime
import tshine73.freedom.core.PriceEntity
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.aws.DynamoDBUtils
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils, IOUtils}

import scala.util.Try

//import scala.annotation.MainAnnotation.Parameter

val tableName = "price"

@main
def main(parameters: String*): Unit = {
  //  fetchPrices(date)
  val date = parameters match
    case head :: _ => DateUtils.parseDate(head)
    case Nil => DateTime.now().minusDays(1).withMillisOfDay(0)


  val priceEntities = IOUtils.readFile("stocks")
    .map(_.split(","))
    .map(codeInfoArr =>
      val code = codeInfoArr(0)
      val priceEither = Try(PriceCrawler.fetchPrice(code, date)).toEither
      val priceEntity = PriceEntity(code, date, priceEither.getOrElse(-1), codeInfoArr(1))
      println(priceEntity)
      priceEntity
    )

  priceEntities.foreach(priceEntity => {
    save(priceEntity)
    writeSheet(priceEntity)
  })

}


def save(priceEntity: PriceEntity) = {
  DynamoDBUtils.putItem(PriceEntity.generateDynamodbItem(priceEntity), PriceEntity.tableName)
}

def writeSheet(priceEntity: PriceEntity) = {
  GoogleSheetUtils.updateValue(priceEntity.range, priceEntity.price)
}