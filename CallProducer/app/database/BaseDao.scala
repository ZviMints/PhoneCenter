package database

import javax.inject.Inject
import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.commands.{DefaultWriteResult, MultiBulkWriteResult, WriteResult}
import reactivemongo.api.{Cursor, WriteConcern}
import reactivemongo.play.json.ImplicitBSONHandlers
import reactivemongo.play.json.JSONSerializationPack.{Document, _}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}


abstract class BaseDao @Inject()(implicit ec: ExecutionContext) {

  type Entity
  val ob = ImplicitBSONHandlers
  implicit val OFormat: OFormat[Entity]

  def collection: JSONCollection


  def insertOne(entity: Entity) = {
    collection.insert.one(entity).map {
      case DefaultWriteResult(true, 1, _, _, _, _) => entity
      case err => throw GeneralDBError(s"$entity was not inserted, something went wrong: $err")
    }
      .recover {
        case WriteResult.Code(11000) => throw DuplicationError(s"$entity exist on DB")
        case err => throw GeneralDBError(err.getMessage)
      }
  }

  def insertMany(entities: List[Entity]): Future[MultiBulkWriteResult] = {
    collection.insert.many(entities)
      .map({
        case re @ MultiBulkWriteResult(true, n, _, _, _, _, _, _, _) if n == entities.length => re
        case re @ MultiBulkWriteResult(false, n, _, _, errors, _, _, errmsg, _) if (errors.count(_.code == 11000) + n) == entities.length =>
          throw ManyDuplicationsError(s"Part or all of the entities: $entities exist on DB: $errmsg")
        case error => throw GeneralDBError(s"InsertMany failed, something went wrong: $error")
      })
  }

  def find(selector: Document, limit: Int = 100): Future[List[Entity]] = {
    val cursor = collection.find(selector, None)(ob.JsObjectDocumentWriter, ob.JsObjectDocumentWriter).sort(Json.obj("_id" -> -1)).cursor[Entity]()
    cursor.collect[List](limit, Cursor.ContOnError[List[Entity]]())
  }

  def findAndUpdate[T](selector: Document, update: T, fetchNewObject: Boolean = true, upsert: Boolean = false)(implicit writer: Writer[T]) = {
    collection.findAndUpdate(selector = selector, update = update, fetchNewObject = fetchNewObject, upsert = upsert, None, None, false, WriteConcern.Acknowledged, None, None, Nil)(ob.JsObjectDocumentWriter, writer, ec).map{ x =>
      x.lastError.foreach(_.err.foreach({ e =>
        val msg = s"Failure on findAndUpdate: $e , selector: ${Json.prettyPrint(selector)}"
        println(msg)
      }))
      x.result[Entity]
    }
  }

  def findOne(filter: Document) = collection.find(filter, None)(ob.JsObjectDocumentWriter, ob.JsObjectDocumentWriter).one[Entity]


}
