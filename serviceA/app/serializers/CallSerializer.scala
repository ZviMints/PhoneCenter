package serializers

import model.{Call, Status}
import play.api.libs.json.{Format, Json, OFormat}
import enumeratum.EnumFormats

object CallSerializer {
  implicit val InstantFormat = CommonSerializers.InstantSerializers.BSONFormat
  implicit val ObjectIdFormat = CommonSerializers.ObjectIdSerializers.BSONFormat
  implicit val StatusFormat = EnumFormats.formats(Status)
  implicit val CallFormat: OFormat[Call] = Json.format[Call]
}
