lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := """serviceA"""
organization := "ariel.university"
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.8"

libraryDependencies += guice

// Tests
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// Mongo
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.18.8-play27"

// Kafka
libraryDependencies +=   "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.4"

// Cache
libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.7.2"
libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.4.0"

// Kamon
libraryDependencies += "io.kamon" %% "kamon-scala" % "0.6.7"
libraryDependencies += "io.kamon" %% "kamon-play-2.6" % "0.6.8"
libraryDependencies += "io.kamon" %% "kamon-datadog" % "0.6.8"

