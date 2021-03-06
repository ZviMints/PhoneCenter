package serializers

import dto.Call
import play.api.libs.json.{Json, OFormat}

object CallSerializer {
  implicit val CallFormat: OFormat[Call] = Json.format[Call]
}
