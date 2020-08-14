package serializers

import dto.CallDto
import model.Call
import play.api.libs.json.{Json, OFormat}

object CallDtoSerializer {
  implicit val CallDtoFormat: OFormat[CallDto] = Json.format[CallDto]
}
