package metrics
import kamon.Kamon
import kamon.metric.instrument.Counter
object Metrics {
  val gotMessageCounter: Counter = Kamon.metrics.counter("gotMessage")
}
