package controllers

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import database.CallDao
import dto.CallDto
import javax.inject.{Inject, Named, Singleton}
import model.Call
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.KafkaService
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import serializers.CallDtoSerializer._

import scala.concurrent.{ExecutionContext, Future}
import serializers.CallSerializer._
@Singleton
class ServiceAController @Inject()(val cc: ControllerComponents,
                                   callDao: CallDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {


  def index(): Action[AnyContent] = Action {
    Ok(views.html.main("Welcome"))
  }

  def send() = Action.async(parse.json) { implicit request =>
    logger.warn(s"[ServiceAController] - Got /send request with request.body = ${request.body}")
    request.body.validate[CallDto] match {
      case JsSuccess(call, _) => callDao.insertOne(Call(call)).map(_ => Ok).recover {
        case ex: Exception => handlerError(s"Failure occurred on callDetails with ex: ${ex.getMessage}")
      }
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad call format: ${error.mkString}")))
    }
  }
}
