package serializers

import java.time.Instant

import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.BSONFormats


object CommonSerializers {

  object InstantSerializers {

    val BSONFormat: OFormat[Instant] = new OFormat[Instant] {
      override def reads(bson: JsValue): JsResult[Instant] = (bson \ "$date").validate[Long].map(Instant.ofEpochMilli)
      override def writes(o: Instant): JsObject = Json.obj("$date" -> o.toEpochMilli)
    }

    val JSONFormat: Format[Instant] = new Format[Instant] {
      override def reads(json: JsValue): JsResult[Instant] = json.validate[Instant]
      override def writes(o: Instant): JsValue = JsString(o.toString)
    }
  }

  object ObjectIdSerializers {

    val BSONFormat: Format[BSONObjectID] = BSONFormats.jsonFormat[BSONObjectID] //{$oid: ""}

    val JSONFormat: Format[BSONObjectID] = new Format[BSONObjectID] {
      override def writes(o: BSONObjectID): JsValue = JsString(o.stringify)
      override def reads(json: JsValue): JsResult[BSONObjectID] = json match {
        case JsString(x) =>
          BSONObjectID.parse(x) match {
            case scala.util.Success(value) => JsSuccess(value)
            case scala.util.Failure(exception) =>
              JsError(s"Expected BSONObjectID as JsString: $exception")
          }
        case _ => JsError(s"Expected BSONObjectID as JsString")
      }
    }
  }

}
