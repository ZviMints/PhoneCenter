lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := """serviceA"""
organization := "ariel.university"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"
apiVersion := ApiVersion(1, 0)
maintainer := "zvimints@gmail.com and eilon26@gmail.com"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.playq" %% "scalatestplus-play" % "5.0.0" % Test

import Dependencies._
libraryDependencies ++=
    Kamon ++
    Mongo ++
    Cache

