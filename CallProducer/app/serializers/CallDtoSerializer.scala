package serializers

import dto.Call
import model.Event
import play.api.libs.json.{Json, OFormat}

object CallDtoSerializer {
  implicit val CallFormat: OFormat[Call] = Json.format[Call]
}
