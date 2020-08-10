package com.vatbox.deadsea.common

import kamon.Kamon
import kamon.metric.instrument.Counter

/** Zvi Mints - zvi.mints@vatbox.com */
object Metrics {

  /* Healthcheck */
  val successCounter: Counter = Kamon.metrics.counter("Healthcheck", tags = Map("state" -> "OK"))
  val failCounter: Counter = Kamon.metrics.counter("Healthcheck", tags = Map("state" -> "Fail"))
}
