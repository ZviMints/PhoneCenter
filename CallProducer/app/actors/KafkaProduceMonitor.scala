package actors

import actors.KafkaProduceMonitor.{CheckInProgress, CheckInReady}
import com.google.inject.Inject
import model.Event
import model.Status.{InProgress, Ready}
import play.api.Configuration
import services.{EventService, KafkaService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object KafkaProduceMonitor {
  case object CheckInReady
  case object CheckInProgress
}

class KafkaProduceMonitor @Inject()(eventService: EventService,
                                    kafkaService: KafkaService,
                                    conf: Configuration)(implicit val ec: ExecutionContext) extends MonitorActor {


  lazy val lockTTLReady = conf.get[FiniteDuration]("CallProducer.lockTTL.ready").toMillis
  lazy val lockTTLInProgress = conf.get[FiniteDuration]("CallProducer.lockTTL.inProgress").toMillis
  override protected def InitialDelay: FiniteDuration = conf.get[FiniteDuration]("CallProducer.producer-actor.initial_delay")
  override protected def TickInterval: FiniteDuration = conf.get[FiniteDuration]("CallProducer.producer-actor.tick_interval")

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
    eventService.fetchByStatus(Ready, lockTTLReady).foreach {
      case Some(event) =>
        logger.warn("[KafkaProduceMonitor] - fetched Ready Event")
        eventService.updateStatus(event._id, InProgress).map(_.get)
        self ! CheckInProgress
      case None => logger.debug(s"Not found event for sync or there is already other process inProgress, next check in $TickInterval")
    }
  }

  private[actors] def errorHandler(event: Event, ex: Exception, action: String) = {
    val msg = s"Failure occurred on $action for eventId: ${event._id}"
    logger.error(msg, ex)
    eventService.updateErrors(event._id, msg + s" ${ex.getMessage}")
    throw ex
  }


  def onCheckInProgress() = {
    eventService.fetchByStatus(InProgress, lockTTLInProgress).foreach {
      case Some(event) => {
        logger.warn("[KafkaProduceMonitor] - fetched InProgress Event")
        kafkaService.writeCallToKafka(event.content)
        logger.warn("Call Successfully sent to Kafka!")
        eventService.syncUpdate(event._id).map(_.get)
        }.recover { case ex: Exception => errorHandler(event, ex, "IN-PROGRESS-GENERAL-ERROR")}
      case None => logger.debug(s"Not found event for sync or there is already other process inProgress, next check in $TickInterval")
    }
  }

}
