package services

import metrics.Metrics
import javax.inject.{Inject, Singleton}
import model.Call

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DashboardService @Inject()(implicit val ec: ExecutionContext)  {
  def update(call: Call) = {
    received()
    balance(true, 1)
    graphByCity(call.city)
    graphByTopic(call.topic)
    graphByGender(call.gender)
    graphByLanguage(call.language)
  }

  def update(totalWaitingCalls: Int) = {
    balance(false, totalWaitingCalls)
  }

  // Showing rangeSampler for (+) number of calls that answers and (-) for number of waiting calls
  private[services] def balance(inc: Boolean, amount: Int) = inc match {
    case true =>  Metrics.CallsBalanceRangeSampler.increment(amount)
    case false =>  Metrics.CallsBalanceRangeSampler.decrement(amount)
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
