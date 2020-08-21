package database

import javax.inject.{Inject, Singleton}
import model.Event
import play.api.libs.json.OFormat
import reactivemongo.play.json.collection.JSONCollection
import serializers.EventSerializer

import scala.concurrent.ExecutionContext

@Singleton
case class EventDao @Inject()(DBConfig: DBConfig)(implicit ec: ExecutionContext) extends BaseDao {
  override type Entity = Event
  override implicit val OFormat: OFormat[Event] = EventSerializer.EventFormat
  override def collection: JSONCollection = DBConfig.calls
}
