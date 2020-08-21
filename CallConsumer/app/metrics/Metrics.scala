package metrics

import kamon.Kamon

object Metrics {
  val applicationStartedCounter= Kamon.counter("applicationStarted").withoutTags()
  val CallReceivedCounter = Kamon.counter("CallReceived").withoutTags()
}
