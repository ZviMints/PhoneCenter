addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.2")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.11.0")

resolvers ++= Seq( Resolver.bintrayIvyRepo("kamon-io", "sbt-plugins"))
addSbtPlugin("io.kamon" % "sbt-kanela-runner-play-2.8" % "2.0.6")


