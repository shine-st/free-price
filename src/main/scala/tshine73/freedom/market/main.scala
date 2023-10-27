package tshine73.freedom.market

import tshine73.freedom.utils.GoogleSheetUtils

@main
def main(parameters: String*): Unit =
  val priceSheetId = "1ZBHuHyJCz6OpCkpfGEZ5ADDsli58vkGJEmHLTPm0wOU"
  println(GoogleSheetUtils.getValues(priceSheetId, "ProductPrice:A2:J20"))