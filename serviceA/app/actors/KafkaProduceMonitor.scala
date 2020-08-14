package actors

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import actors.KafkaProduceMonitor.{CheckInProgress, CheckInReady}
import com.google.inject.Inject
import database.CallDao
import model.Status.{InProgress, Ready}
import model.{Call}
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.cache.NamedCache
import services.{CallService, KafkaService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object KafkaProduceMonitor {
  case object CheckInReady
  case object CheckInProgress
}
class KafkaProduceMonitor @Inject()(callService: CallService,
                                    kafkaService: KafkaService,
                                    conf: Configuration,
                                    @NamedCache("serviceA") cache: AsyncCacheApi)(implicit val ec: ExecutionContext) extends MonitorActor {


  val expiration: FiniteDuration = conf.get[FiniteDuration]("serviceA.cache.expiration")
  lazy val lockTTLReady = conf.get[FiniteDuration]("serviceA.lockTTL.ready").toMillis
  lazy val lockTTLInProgress = conf.get[FiniteDuration]("serviceA.lockTTL.inProgress").toMillis

  override protected def InitialDelay: FiniteDuration = conf.get[FiniteDuration]("serviceA.producer-actor.initial_delay")

  override protected def TickInterval: FiniteDuration = conf.get[FiniteDuration]("serviceA.producer-actor.tick_interval")

  override protected def onTick(): Unit = {
    self ! CheckInReady
    self ! CheckInProgress
  }

  protected def myReceive: Receive = {
    case CheckInReady => onCheckInReady()
    case CheckInProgress => onCheckInProgress()
  }

  override def receive: Receive = myReceive.orElse(super.receive)

  def onCheckInReady() = {
    callService.fetchByStatus(Ready, lockTTLReady).foreach {
      case Some(call) =>
        logger.warn("[KafkaProduceMonitor] - fetched Ready Call in status Ready")
        callService.updateStatus(call._id, InProgress).map(_.get)
        self ! CheckInProgress
      case None => logger.debug(s"Not found call for sync or there is already other process inProgress, next check in $TickInterval")
    }
  }

  private[actors] def errorHandler(call: Call, ex: Exception, action: String) = {
    val msg = s"Failure occurred on $action for callId: ${call._id}"
    logger.error(msg, ex)
    callService.updateErrors(call._id, msg + s" ${ex.getMessage}")
    throw ex
  }


  def onCheckInProgress() = {
    callService.fetchByStatus(InProgress, lockTTLInProgress).foreach {
      case Some(call) => {
        logger.warn("[KafkaProduceMonitor] - fetched some call in status InProgress")
        kafkaService.writeToKafka(call)
        logger.warn("Call successfully sent to Kafka!")
        callService.syncUpdate(call._id).map(_.get)
        }.recover { case ex: Exception => errorHandler(call, ex, "IN-PROGRESS-GENERAL-ERROR")}
      case None => logger.debug(s"Not found call for sync or there is already other process inProgress, next check in $TickInterval")
    }
  }

}
