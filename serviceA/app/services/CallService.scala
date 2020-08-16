package services

import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import database.{CallDao, DuplicationError, ManyDuplicationsError}
import javax.inject.{Inject,Singleton}
import model.Status.Sent
import model.{Call, Status}
import play.api.Configuration
import play.api.libs.json.{JsResultException, Json}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.ImplicitBSONHandlers._
import serializers.CallSerializer._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CallService @Inject()(callDao: CallDao)(implicit ec: ExecutionContext) extends LazyLogging {

  def updateStatus(callId: BSONObjectID, status: Status): Future[Option[Call]] = {
    callDao.findAndUpdate(Json.obj("$and" -> List(
      locked(Instant.now),
      Json.obj(s"${Call.ID}" -> callId))),
      Json.obj("$set" -> Json.obj(s"${Call.Status}" -> status)))
  }.recover {
    case JsResultException(errors) if errors.exists(_._2.exists(_.message.contains("code=11000"))) =>
      logger.warn("Previous call still in process")
      None
  }

  def syncUpdate(callId: BSONObjectID): Future[Option[Call]] = {
    callDao.findAndUpdate(Json.obj(s"${Call.ID}" -> callId),
      Json.obj("$set" -> Json.obj(
        s"${Call.SyncAt}" -> Instant.now(),
        s"${Call.Status}" -> Sent)))
  }

  def fetchByStatus(source: Status, lockTTL: Long): Future[Option[Call]] = {
    callDao.findAndUpdate(Json.obj("$and" -> List(
      notLocked(Instant.now), Json.obj(s"${Call.Status}" -> source))),
      Json.obj("$set" -> Json.obj(s"${Call.LockedTime}" -> Instant.now.plusMillis(lockTTL))))
  }

  def updateErrors(id: BSONObjectID, error: String): Future[Option[Call]] = {
    callDao.findAndUpdate(Json.obj(s"${Call.ID}" -> id),
      Json.obj("$push" -> Json.obj(s"${Call.Errors}" -> error)))
  }

  private def notLocked(now: Instant) = {
    Json.obj(s"${Call.LockedTime}" -> Json.obj("$lt" -> now))
  }

  private def locked(now: Instant) = Json.obj(s"${Call.LockedTime}" -> Json.obj("$gte" -> now))
}
