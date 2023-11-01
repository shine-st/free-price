package tshine73.freedom.utils.aws

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

import java.util.NoSuchElementException
import scala.jdk.CollectionConverters.*
import scala.util.Try

object DynamoDBUtils {
  private val ddb: DynamoDbClient = DynamoDbClient.builder()
    .build()


  //  def sendRequest(request: DynamoDbRequest, action: DynamoDbClient => DynamoDbResponse): Either[Throwable, DynamoDbResponse] =
  //    Try(action(ddb)).toEither

  //  def extractResponse()

  def putItem(item: Map[String, AttributeValue], tableName: String) =
    val request = PutItemRequest.builder()
      .tableName(tableName)
      .item(item.asJava)
      .build()


    var statusCode: Int = 0

    try {
      val response = ddb.putItem(request)
      println(tableName + " was successfully updated")
      statusCode = response.sdkHttpResponse().statusCode()
    } catch {
      case e: ResourceNotFoundException =>
        e.printStackTrace()
        //        System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName)
        println("Be sure that it exists and that you've typed its name correctly!")
      case e: DynamoDbException =>
        println(e.getMessage)
    }

    statusCode == 200

  def getItemByKey(keyMap: Map[String, AttributeValue], tableName: String): Map[String, AttributeValue] = {
    val request = GetItemRequest.builder()
      .key(keyMap.asJava)
      .tableName(tableName)
      .build()

    var itemMap: Map[String, AttributeValue] = Map.empty
    try {
      val returnedItem = ddb.getItem(request).item
      if (returnedItem != null)
        itemMap = returnedItem.asScala.toMap
    } catch {
      case e: DynamoDbException =>
        println(e.getMessage)
    }

    itemMap
  }

  def deleteItem(keyMap: Map[String, AttributeValue], tableName: String) = {
    val deleteReq = DeleteItemRequest.builder()
      .tableName(tableName)
      .key(keyMap.asJava)
      .build()

    var statusCode: Int = 0

    try {

      val response = ddb.deleteItem(deleteReq)
      statusCode = response.sdkHttpResponse().statusCode()
    }
    catch {
      case e: DynamoDbException =>
        println(e.getMessage)
    }

    statusCode == 200
  }

  def getMaximumValue(table: String, index: String, column: String, staticValueMap: Map[String, String]): Map[String, AttributeValue] =
    val keyConditionExpression = f"#staticValue = :v_static"
    val expressionAttributeNames = Map("#staticValue" -> staticValueMap.keys.head).asJava
    val expressionAttributeValues = Map(":v_static" -> AttributeValue.builder().s(staticValueMap.values.head).build())
      .asJava

    var itemMap: Map[String, AttributeValue] = Map.empty

    try {
      val request = QueryRequest.builder()
        .tableName(table)
        .indexName(index)
        .keyConditionExpression(keyConditionExpression)
        .expressionAttributeNames(expressionAttributeNames)
        .expressionAttributeValues(expressionAttributeValues)
        .projectionExpression(column)
        .scanIndexForward(false)
        .limit(1)
        .build()

      val response = ddb.query(request)
      itemMap = response.items().asScala.head.asScala.toMap
    }
    catch {
      case e: DynamoDbException =>
        throw e
      case e: NoSuchElementException =>
        e.printStackTrace()
    }

    itemMap
}
