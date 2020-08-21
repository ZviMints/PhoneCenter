package services

import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import database.{EventDao, DuplicationError, ManyDuplicationsError}
import javax.inject.{Inject,Singleton}
import model.Status.Sent
import model.{Event, Status}
import play.api.Configuration
import play.api.libs.json.{JsResultException, Json}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.ImplicitBSONHandlers._
import serializers.EventSerializer._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventService @Inject()(eventDao: EventDao)(implicit ec: ExecutionContext) extends LazyLogging {

  def updateStatus(eventId: BSONObjectID, status: Status): Future[Option[Event]] = {
    eventDao.findAndUpdate(Json.obj("$and" -> List(
      locked(Instant.now),
      Json.obj(s"${Event.ID}" -> eventId))),
      Json.obj("$set" -> Json.obj(s"${Event.Status}" -> status)))
  }.recover {
    case JsResultException(errors) if errors.exists(_._2.exists(_.message.contains("code=11000"))) =>
      logger.warn("Previous event still in process")
      None
  }

  def syncUpdate(eventId: BSONObjectID): Future[Option[Event]] = {
    eventDao.findAndUpdate(Json.obj(s"${Event.ID}" -> eventId),
      Json.obj("$set" -> Json.obj(
        s"${Event.SyncAt}" -> Instant.now(),
        s"${Event.Status}" -> Sent)))
  }

  def fetchByStatus(source: Status, lockTTL: Long): Future[Option[Event]] = {
    eventDao.findAndUpdate(Json.obj("$and" -> List(
      notLocked(Instant.now), Json.obj(s"${Event.Status}" -> source))),
      Json.obj("$set" -> Json.obj(s"${Event.LockedTime}" -> Instant.now.plusMillis(lockTTL))))
  }

  def updateErrors(id: BSONObjectID, error: String): Future[Option[Event]] = {
    eventDao.findAndUpdate(Json.obj(s"${Event.ID}" -> id),
      Json.obj("$push" -> Json.obj(s"${Event.Errors}" -> error)))
  }

  private def notLocked(now: Instant) = {
    Json.obj(s"${Event.LockedTime}" -> Json.obj("$lt" -> now))
  }

  private def locked(now: Instant) = Json.obj(s"${Event.LockedTime}" -> Json.obj("$gte" -> now))
}
