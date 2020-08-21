package actors

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import com.google.inject.Inject
import metrics.Metrics
import model.Call
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.Json
import play.cache.NamedCache
import services.{DashboardService, KafkaService}
import serializers.CallSerializer._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

object KafkaConsumeMonitor {

  case object ConsumeFromKafka

}


class KafkaConsumeMonitor @Inject()(kafkaService: KafkaService,
                                    conf: Configuration,
                                    dashboardService: DashboardService,
                                    @NamedCache("CallConsumer") cache: AsyncCacheApi)(implicit val ec: ExecutionContext) extends AbstractActor {


  val expiration: FiniteDuration = conf.get[FiniteDuration]("CallConsumer.cache.expiration")

  override protected def InitialDelay: FiniteDuration = conf.get[FiniteDuration]("CallConsumer.consumer-actor.initial_delay")

  override protected def onTick(): Unit = {
    self ! ConsumeFromKafka
  }

  protected def myReceive: Receive = {
    case ConsumeFromKafka => consumeFromKafka()
  }

  override def receive: Receive = myReceive.orElse(super.receive)

  def consumeFromKafka() = {
    Metrics.applicationStartedCounter.increment()
    kafkaService.consumeFromKafka { (call: Call) => {
      logger.warn(s"Consumed Call from Kafka: \n ${Json.prettyPrint(Json.toJson(call))}")
      for {
        _ <- cache.set("key", call, expiration)
      } yield dashboardService.update()
    }
    }
  }
}
