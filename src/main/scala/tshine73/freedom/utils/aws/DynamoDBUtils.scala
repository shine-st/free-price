package tshine73.freedom.utils.aws

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.model.{AttributeValue, DeleteItemRequest, DynamoDbException, GetItemRequest, PutItemRequest, PutItemResponse, ResourceNotFoundException}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import scala.jdk.CollectionConverters.*

object DynamoDBUtils {
  val ddb = DynamoDbClient.builder()
    .build()

  def putItem(item: Map[String, AttributeValue], tableName: String) = {
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
  }

  def getItem(keyMap: Map[String, AttributeValue], tableName: String): Map[String, AttributeValue] = {
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
}
