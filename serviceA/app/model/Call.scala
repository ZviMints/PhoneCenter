package model

import java.time.Instant

import dto.CallDto
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import model.Status.Ready
import reactivemongo.bson.BSONObjectID

import scala.collection.immutable


case class Call(
                 _id: BSONObjectID,
                 startTime: String,
                 city: String,
                 topic: String,
                 language: String,
                 gender: String,
                 age: String,
                 // Fetching:
                 status: Status,
                 lockedTime: Instant,
                 errors: List[String],
                 createdAt: Instant,
                 syncAt: Option[Instant],
               )

object Call {

  def apply(callDto: CallDto): Call = new Call(
    _id = BSONObjectID.generate(),
    startTime = callDto.startTime,
    city = callDto.city,
    topic = callDto.topic,
    language = callDto.language,
    gender = callDto.gender,
    age = callDto.age,
    status = Ready,
    lockedTime = Instant.now(),
    errors = Nil,
    createdAt = Instant.now(),
    syncAt = None)

  val ID = "_id"
  val StartTime = "startTime"
  val City = "city"
  val Topic = "topic"
  val Language = "language"
  val Gender = "gender"
  val Age = "age"

  // Fetching
  val Status = "status"
  val LockedTime = "lockedTime"
  val Errors = "errors"
  val CreatedAt = "createdAt"
  val SyncAt = "syncAt"
}

