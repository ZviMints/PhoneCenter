package controllers

import actors.KafkaConsumeMonitor.ConsumeFromKafka
import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import database.CallDao
import javax.inject.{Inject, Named, Singleton}
import model.Call
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.KafkaService
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import serializers.CallSerializer.CallFormat

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceAController @Inject()(val cc: ControllerComponents,
                                   @Named("kafka-actor") kafkaActor: ActorRef,
                                   kafkaService: KafkaService,
                                   callDao: CallDao)(implicit ec: ExecutionContext) extends AbstractController(cc) with LazyLogging with ControllerErrorHandling {


  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  // todo: remove this after we understand https://dzone.com/articles/hands-on-apache-kafka-with-scala

  def callDetails() = Action.async(parse.json) { implicit request =>
    request.body.validate[Call] match {
      case JsSuccess(call, _) => {
        for {
          _ <- callDao.insertOne(call).map(_ => logger.info(s"call inserted to database with call: $call"))
          _ <- kafkaService.writeToKafka(call).map(_ => logger.info(s"$call wrote to kafka with call: $call"))
        } yield Ok
      }.recover { case ex: Exception => handlerError(s"Failure occurred on callDetails with ex: ${ex.getMessage}") }
      case JsError(error) => Future.successful(BadRequest(Json.toJson("error" -> s"Bad rules format: ${error.mkString}")))
    }
  }

  def totalWaitingCalls() = Action.async { implicit request =>
    //todo: To understand what this routes does and do it
    logger.info(s"[totalWaitingCalls] Total message: ${request.body}")
    Future {
      Ok
    }
  }
}
