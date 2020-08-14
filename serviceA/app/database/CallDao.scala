package database

import javax.inject.{Inject, Singleton}
import model.Call
import play.api.libs.json.OFormat
import reactivemongo.play.json.collection.JSONCollection
import serializers.CallSerializer

import scala.concurrent.ExecutionContext

@Singleton
case class CallDao @Inject()(DBConfig: DBConfig)(implicit ec: ExecutionContext) extends BaseDao {
  override type Entity = Call
  override implicit val OFormat: OFormat[Call] = CallSerializer.CallFormat
  override def collection: JSONCollection = DBConfig.calls
}
