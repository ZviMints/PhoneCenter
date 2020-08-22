package metrics

import kamon.Kamon

object Metrics {
  val CallReceivedCounter = Kamon.counter("CallReceived", "Total Received Calls").withoutTags()
  def GraphByCity(city: String) = Kamon.counter("CallByCity", "By city of the call").withTag("city",city)
  def GraphByTopic(topic: String) = Kamon.counter("CallByTopic", "By topic of the call").withTag("topic",topic)
  def GraphByLanguage(language: String) = Kamon.counter("CallByLanguage", "By language of the call").withTag("language",language)
  def GraphByGender(male: Boolean) = Kamon.counter("CallByGender", "By gender of the call").withTag("male",male)
  val TotalWaitingCallsRangeSampler = Kamon.rangeSampler("totalWaitingCalls").withoutTags()

}
