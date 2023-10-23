package tshine73.freedom

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import tshine73.freedom.crawler.PriceCrawler
import tshine73.freedom.utils.{DateUtils, GoogleSheetUtils}
import tshine73.freedom.utils.DateUtils.*
import tshine73.freedom.utils.aws.DynamoDBUtils
import scala.jdk.CollectionConverters.*

class GoogleSheetTest extends AnyFunSuite {

  test("write data to sheet cell") {
    assert(GoogleSheetUtils.getValues("History Performance!X1").getValues == null)
    GoogleSheetUtils.updateValue("History Performance!X1", 12345)
    assert(GoogleSheetUtils.getValues("History Performance!X1").getValues().get(0).asScala.head.toString == "12345")
    GoogleSheetUtils.deleteValue("History Performance!X1")
    assert(GoogleSheetUtils.getValues("History Performance!X1").getValues == null)
  }


}