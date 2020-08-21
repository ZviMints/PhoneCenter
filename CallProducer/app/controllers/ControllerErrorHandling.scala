package controllers

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import play.api.mvc.Results.InternalServerError

trait ControllerErrorHandling extends LazyLogging {

  def handlerError(err: String) = {
    logger.error(err)
    InternalServerError(Json.toJson("error" -> err))
  }
}
