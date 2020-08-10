package database

import javax.inject.{Inject, Singleton}
import model.Call
import play.api.libs.json.{Json, OFormat}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

@Singleton
case class CallDao @Inject()(DBConfig: DBConfig)(implicit ec: ExecutionContext) extends BaseDao {
  override type Entity = Call
  override implicit val OFormat: OFormat[Call] = Json.format[Call]
  override def collection: JSONCollection = DBConfig.calls
}
