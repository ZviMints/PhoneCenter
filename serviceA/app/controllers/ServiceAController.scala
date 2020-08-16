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
                                   kafkaService: KafkaService,
                                   callDao: CallDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {


  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def callDetails() = Action.async(parse.json) { implicit request =>
    request.body.validate[CallDto] match {
      case JsSuccess(call, _) => callDao.insertOne(Call(call)).map(_ => Ok).recover {
        case ex: Exception => handlerError(s"Failure occurred on callDetails with ex: ${ex.getMessage}")
      }
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad call format: ${error.mkString}")))
    }
  }

  //todo: totalWaitingCalls()
  //todo: redesign javascript of yossi

  // 1. todo -> send and recive kafka messages
  // how to test:
  // 2. remove write and read routes from controller and and
  // 3. js yossi -> /callDetails

  def totalWaitingCalls() = Action.async { implicit request =>
    //todo: To understand what this routes does and do it
    logger.info(s"[totalWaitingCalls] Total message: ${request.body}")
    Future {
      Ok
    }
  }
  // ============================================ For DEBUG =========================================== //
  def write() = Action(parse.json) { implicit request =>
    request.body.validate[CallDto] match {
      case JsSuccess(call, _) => kafkaService.writeToKafka(Call(call)); Ok
      case JsError(error) => BadRequest(Json.toJson("error" -> s"Bad rules format: ${error.mkString}"))
    }
  }

  def read() = Action { Ok(kafkaService.consumeFromKafka().map { call => Json.stringify(Json.toJson(call)) }.toString()) }
}
