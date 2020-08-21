package model

import java.time.Instant

import dto.Call
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import model.Status.Ready
import reactivemongo.bson.BSONObjectID

import scala.collection.immutable


case class Event(
                  _id: BSONObjectID,
                  content: Call,
                  // Fetching:
                  status: Status,
                  lockedTime: Instant,
                  errors: List[String],
                  createdAt: Instant,
                  syncAt: Option[Instant],
               )

object Event {

  def apply(callDto: Call): Event = new Event(
    _id = BSONObjectID.generate(),
    content = callDto,
    status = Ready,
    lockedTime = Instant.now(),
    errors = Nil,
    createdAt = Instant.now(),
    syncAt = None)

  val ID = "_id"
  val Content = "content"
  
  // Fetching
  val Status = "status"
  val LockedTime = "lockedTime"
  val Errors = "errors"
  val CreatedAt = "createdAt"
  val SyncAt = "syncAt"
}

