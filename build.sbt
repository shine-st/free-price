ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "free-price"
  )


libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

libraryDependencies += "joda-time" % "joda-time" % "2.12.5"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.15"

libraryDependencies += "com.github.losizm" %% "little-json" % "9.0.0"

libraryDependencies += "software.amazon.awssdk" % "dynamodb" % "2.20.64"

libraryDependencies += "com.google.api-client" % "google-api-client" % "2.2.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.34.1"
libraryDependencies += "com.google.apis" % "google-api-services-sheets" % "v4-rev20220927-2.0.0"