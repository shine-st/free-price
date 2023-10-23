package tshine73.freedom.utils

import scala.io.Source

object IOUtils {

  def readFile(fileName: String) = {
    Source.fromResource(fileName).getLines.toList
  }
}
