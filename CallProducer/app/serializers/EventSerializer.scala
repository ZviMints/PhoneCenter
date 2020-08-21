package serializers

import enumeratum.EnumFormats
import model.{Event, Status}
import play.api.libs.json.{Json, OFormat}

object EventSerializer {
  implicit val InstantFormat = CommonSerializers.InstantSerializers.BSONFormat
  implicit val ObjectIdFormat = CommonSerializers.ObjectIdSerializers.BSONFormat
  implicit val StatusFormat = EnumFormats.formats(Status)
  implicit val EventFormat: OFormat[Event] = Json.format[Event]
}
