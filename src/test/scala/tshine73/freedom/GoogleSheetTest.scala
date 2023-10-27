package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.GoogleSheetUtils.*
import tshine73.freedom.utils.aws.DynamoDBUtils
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils}

import scala.jdk.CollectionConverters.*

class GoogleSheetTest extends AnyFunSuite {
  val assetSpreadsheetId = "1H1-OcOoDI9CRkOit1RoOaAE5WJ_HVVOT7jP7fMG50mU"

  test("write data to sheet cell") {
    assert(getValues(assetSpreadsheetId, "History Performance!X1").getValues == null)
    updateValue(assetSpreadsheetId, "History Performance!X1", 12345)
    assert(getValues(assetSpreadsheetId, "History Performance!X1").getValues().get(0).asScala.head.toString == "12345")
    deleteValue(assetSpreadsheetId, "History Performance!X1")
    assert(getValues(assetSpreadsheetId, "History Performance!X1").getValues == null)
  }


}