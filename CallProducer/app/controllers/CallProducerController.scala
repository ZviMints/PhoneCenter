package controllers

import com.typesafe.scalalogging.LazyLogging
import database.EventDao
import dto.Call
import javax.inject.{Inject, Singleton}
import model.Event
import play.api.libs.json.{Format, JsError, JsSuccess, Json, OFormat}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import serializers.CallSerializer._
import services.KafkaService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CallProducerController @Inject()(val cc: ControllerComponents,
                                       kafkaService: KafkaService,
                                       eventDao: EventDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {

  // sbt runProd -Dhttp.port=8080
  // datadog-agent start

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

  def totalWaitingCalls() = Action.async(parse.json) { implicit request =>
    logger.warn(s"[CallProducerController] - got /totalWaitingCalls request with body = ${request.body}")
    request.body.validate[MonitorRequest] match {
      case JsSuccess(monitor, _) => kafkaService.writeNumberToKafka(monitor.totalCalls)
        Future.successful(Ok)
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad MonitorRequest format: ${error.mkString}")))
    }
  }

  // ================================ Private Case Class ==================================== //
  case class MonitorRequest(totalCalls: Int)
  implicit val MonitorRequestFormat: OFormat[MonitorRequest] = Json.format[MonitorRequest]

}
