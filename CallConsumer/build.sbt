lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAgent)

name := """CallConsumer"""
organization := "ariel.university"
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.8"

libraryDependencies += guice

// Tests
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// Kafka
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"

// Cache
libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.7.2"
libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.4.0"

// Kamon
libraryDependencies += "io.kamon" %% "kamon-bundle" % "2.1.0"
libraryDependencies += "io.kamon" %% "kamon-apm-reporter" % "2.1.0"

// Logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

// Json
libraryDependencies += "com.beachape" %% "enumeratum" % "1.5.12"
libraryDependencies += "com.beachape" %% "enumeratum-play-json" % "1.5.12"



