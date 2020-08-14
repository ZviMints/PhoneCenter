package serializers

import model.Call
import play.api.libs.json.{Json, OFormat}

object CallSerializer {
  implicit val InstantFormat = CommonSerializers.InstantSerializers.BSONFormat
  implicit val ObjectIdFormat = CommonSerializers.ObjectIdSerializers.BSONFormat
  implicit val CallFormat: OFormat[Call] = Json.format[Call]
}
