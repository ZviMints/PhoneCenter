package controllers

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import database.EventDao
import dto.Call
import javax.inject.{Inject, Named, Singleton}
import model.Event
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.KafkaService
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import serializers.CallDtoSerializer._

import scala.concurrent.{ExecutionContext, Future}
import serializers.EventSerializer._
@Singleton
class CallProducerController @Inject()(val cc: ControllerComponents,
                                       callDao: EventDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {


  def index(): Action[AnyContent] = Action {
    Ok(views.html.main("Welcome"))
  }

  def send() = Action.async(parse.json) { implicit request =>
    logger.warn(s"[CallProducerController] - Got /send request with request.body = ${request.body}")
    request.body.validate[Call] match {
      case JsSuccess(call, _) => callDao.insertOne(Event(call)).map(_ => Ok).recover {
        case ex: Exception => handlerError(s"Failure occurred on callDetails with ex: ${ex.getMessage}")
      }
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad call format: ${error.mkString}")))
    }
  }
}
