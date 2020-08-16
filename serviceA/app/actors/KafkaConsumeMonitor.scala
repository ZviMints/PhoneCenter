package actors

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import akka.Done
import com.google.inject.Inject
import database.CallDao
import metrics.Metrics
import model.Call
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.cache.NamedCache
import services.KafkaService

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

object KafkaConsumeMonitor {
  case object ConsumeFromKafka
}


class KafkaConsumeMonitor @Inject()(kafkaService: KafkaService,
                                    conf: Configuration,
                                    @NamedCache("serviceA") cache: AsyncCacheApi)(implicit val ec: ExecutionContext) extends MonitorActor {


  val expiration: FiniteDuration = conf.get[FiniteDuration]("serviceA.cache.expiration")

  override protected def InitialDelay: FiniteDuration = conf.get[FiniteDuration]("serviceA.consumer-actor.initial_delay")

  override protected def TickInterval: FiniteDuration = conf.get[FiniteDuration]("serviceA.consumer-actor.tick_interval")

  override protected def onTick(): Unit = {
    self ! ConsumeFromKafka
  }

  protected def myReceive: Receive = {
    case ConsumeFromKafka => consumeFromKafka()
  }

  override def receive: Receive = myReceive.orElse(super.receive)

  def consumeFromKafka() = {
    kafkaService.consumeFromKafka match {
      case Nil => { logger.warn("[KafkaConsumeMonitor] - There NO messages available in Kafka") }
      case calls =>
        logger.warn(s"[KafkaConsumeMonitor] - consumed messages from Kafka with calls = $calls")
        calls.map { call =>
        for {
          _ <- cache.set("key", call, expiration)
          _ <- cache.get[Call]("key").map { v =>
            logger.warn(s"Cache data for key: key, value: $v")
            // _ <- do WS to serviceC that will now make new dashboard or gatherMetrics with dashboardService: DashboardService
          }
        } yield ()
      }
    }
  }
}
