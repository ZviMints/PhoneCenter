package metrics

import kamon.Kamon
import kamon.metric.instrument.Counter

object Metrics {

  /* Healthcheck */
  val successCounter: Counter = Kamon.metrics.counter("Healthcheck", tags = Map("state" -> "OK"))
  val failCounter: Counter = Kamon.metrics.counter("Healthcheck", tags = Map("state" -> "Fail"))
}
