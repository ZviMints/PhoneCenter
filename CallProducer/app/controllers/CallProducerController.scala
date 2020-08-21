package controllers

import com.typesafe.scalalogging.LazyLogging
import database.EventDao
import dto.Call
import javax.inject.{Inject, Singleton}
import model.Event
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import serializers.CallSerializer._

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class CallProducerController @Inject()(val cc: ControllerComponents,
                                       eventDao: EventDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {

  // sbt runProd -Dhttp.port=8080
  def index(): Action[AnyContent] = Action {
    Ok(views.html.main("CallProducer"))
  }

  def send() = Action.async(parse.json) { implicit request =>
    logger.warn(s"[CallProducerController] - got /send request with request.body = ${request.body}")
    request.body.validate[Call] match {
      case JsSuccess(call, _) => eventDao.insertOne(Event(call)).map(_ => Ok).recover {
        case ex: Exception => handlerError(s"Failure occurred on callDetails with ex: ${ex.getMessage}")
      }
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad call format: ${error.mkString}")))
    }
  }
}
