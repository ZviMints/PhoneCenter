package services

import metrics.Metrics
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DashboardService @Inject()(implicit val ec: ExecutionContext)  {
  def update() = {
    graph1()
    graph2()
    graph3()
  }

  private[services] def graph1() = Metrics.gotMessageCounter.increment() // Some graph
  private[services] def graph2() = Metrics.gotMessageCounter.increment() // Some graph
  private[services] def graph3() = Metrics.gotMessageCounter.increment() // Some graph
}
