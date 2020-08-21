lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := """CallProducer"""
organization := "ariel.university"
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.8"

libraryDependencies += guice

// Tests
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// Mongo
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.18.8-play27"

// Kafka
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"

// Logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

// Json
libraryDependencies += "com.beachape" %% "enumeratum" % "1.5.12"
libraryDependencies += "com.beachape" %% "enumeratum-play-json" % "1.5.12"



