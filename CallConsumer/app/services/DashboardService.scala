package services

import metrics.Metrics
import javax.inject.{Inject, Singleton}
import model.Call

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DashboardService @Inject()(implicit val ec: ExecutionContext)  {
  def update(call: Call) = {
    received()
    graphByCity(call.city)
    graphByTopic(call.topic)
    graphByGender(call.gender)
    graphByLanguage(call.language)
  }

  // Showing all the calls that received from kafka
  private[services] def received() = Metrics.CallReceivedCounter.increment()

  // Showing graph for calls by city
  private[services] def graphByCity(city: String) = Metrics.GraphByCity(city).increment()

  // Showing graph for calls by topic
  private[services] def graphByTopic(topic: String) = Metrics.GraphByTopic(topic).increment()

  // Showing graph for calls by gender
  private[services] def graphByGender(gender: String) = Metrics.GraphByGender(gender.equalsIgnoreCase("Male")).increment()

  // Showing graph for calls by language
  private[services] def graphByLanguage(language: String) = Metrics.GraphByLanguage(language).increment()

}
