package tshine73.freedom.utils

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets, GoogleCredential}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.{Sheets, SheetsScopes}
import com.google.api.services.sheets.v4.model.{FindReplaceRequest, Request, Sheet, Spreadsheet, SpreadsheetProperties, UpdateSpreadsheetPropertiesRequest, UpdateValuesResponse, ValueRange}
import com.sun.org.apache.xpath.internal.operations.Plus

import java.io.{FileInputStream, FileNotFoundException, IOException, InputStream, InputStreamReader}
import java.security.GeneralSecurityException
import java.util
import java.util.Collections
import scala.jdk.CollectionConverters.*


object GoogleSheetUtils {
  private val APPLICATION_NAME = "Google Sheets API"
  private val JSON_FACTORY = GsonFactory.getDefaultInstance
  private val TOKENS_DIRECTORY_PATH = "tokens"
  private val spreadsheetId = "1H1-OcOoDI9CRkOit1RoOaAE5WJ_HVVOT7jP7fMG50mU"


  private val SCOPES = List(SheetsScopes.SPREADSHEETS).asJava
  private val CREDENTIALS_FILE_PATH = "/credentials.json"

  private val service = initService()

  private def initService() = {
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
      .setApplicationName(APPLICATION_NAME)
      .build()
  }

  private def getCredentials(HTTP_TRANSPORT: NetHttpTransport) = {
    val in = GoogleSheetUtils.getClass.getResourceAsStream(CREDENTIALS_FILE_PATH)
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
    }
    val clientSecrets =
      GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in))

    // Build flow and trigger user authorization request.
    val flow = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
      .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
      .setAccessType("offline")
      .build()
    val receiver = new LocalServerReceiver.Builder().setPort(8888).build()
    AuthorizationCodeInstalledApp(flow, receiver).authorize("user")

  }


  def getValues(range: String) = {
    var result: ValueRange = null

    try {
      // Gets the values of the cells in the specified range.
      result = service.spreadsheets.values.get(spreadsheetId, range).execute
      val numRows = if (result.getValues != null)
        result.getValues.size
      else
        0

      println(f"$numRows rows retrieved.")
    } catch {
      case e: GoogleJsonResponseException =>
        val error = e.getDetails
        if (error.getCode eq 404)
          println(f"Spreadsheet not found with id '$spreadsheetId'.\n")
        else
          throw e
    }

    result
  }

  def updateValue(range: String, value: AnyVal) = {
    var result: UpdateValuesResponse = null
    try {
      // Updates the values in the specified range.
      val body = new ValueRange()
        .setValues(List(List(value).asJava).asJava)

      result = service.spreadsheets().values().update(spreadsheetId, range, body)
        .setValueInputOption("RAW")
        .execute()
      println(f"${result.getUpdatedCells()} cells updated.")
    } catch {
      case e: GoogleJsonResponseException =>
          throw e
    }

    result
  }


  def deleteValue(range: String) = {
    var result: UpdateValuesResponse = null
    try {
      // Updates the values in the specified range.
      val body = new ValueRange()
        .setValues(List(List("").asJava).asJava)

      result = service.spreadsheets().values().update(spreadsheetId, range, body)
        .setValueInputOption("RAW")
        .execute()

      println(f"${result.getUpdatedCells()} cell deleted.")
    } catch {
      case e: GoogleJsonResponseException =>
          throw e
    }

    result
  }


}
