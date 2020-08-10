import sbt._

object Dependencies {

  lazy val Kamon = Seq(
    "io.kamon" %% "kamon-system-metrics" % "0.6.7",
    "io.kamon" %% "kamon-scala" % "0.6.7",
    "io.kamon" %% "kamon-play-2.6" % "0.6.8",
    "io.kamon" %% "kamon-akka-2.5" % "0.6.8",
    "io.kamon" %% "kamon-datadog" % "0.6.8"
  ).map(_.exclude("org.asynchttpclient", "async-http-client"))


  val Mongo = Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % "0.18.8-play27"
  )

  val Cache = Seq(
    "com.typesafe.play" %% "play-cache" % "2.7.2",
    "com.github.karelcemus" %% "play-redis" % "2.4.0"
  )

}
