package actors

import actors.MonitorActor.Tick
import akka.actor.{Actor, Cancellable}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.FiniteDuration

object MonitorActor {
  case object Tick
}

trait MonitorActor extends Actor with LazyLogging {

  import context.dispatcher

  protected def InitialDelay: FiniteDuration

  protected def TickInterval: FiniteDuration

  private var cancelTick: Option[Cancellable] = None

  override def preStart(): Unit = cancelTick = Some(context.system.scheduler.schedule(InitialDelay, TickInterval, self, Tick))

  override def postStop(): Unit = cancelTick.foreach(_.cancel())

  protected def onTick(): Unit

  override def receive: Receive = defaultReceive

  protected def defaultReceive: Receive = {
    case Tick => onTick()
    case x => logger.warn(s"MonitorActor: Got an unknown message $x")
  }
}

