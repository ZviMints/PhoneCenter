package metrics
import kamon.Kamon
import kamon.metric.instrument.Counter
object Metrics {
  val applicationStartedCounter: Counter = Kamon.metrics.counter("applicationStarted")
  val gotMessageCounter: Counter = Kamon.metrics.counter("gotMessage")
}
