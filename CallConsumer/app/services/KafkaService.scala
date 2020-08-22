package services

import java.lang.Integer
import java.time.Duration
import java.util
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import model.Call
import org.apache.kafka.clients.consumer.KafkaConsumer
import play.api.Configuration
import play.api.libs.json.Json
import serializers.CallSerializer.CallFormat

import scala.Int
import scala.collection.JavaConverters._
import scala.collection.immutable.Range.Int
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaService @Inject()(conf: Configuration)(implicit val ec: ExecutionContext) extends LazyLogging {

  val callsTopic = conf.get[String]("CallConsumer.kafka.callsTopic")
  val monitorTopic = conf.get[String]("CallConsumer.kafka.monitorTopic")

  def consumeFromKafka[T, R](callOperation: Call => Future[T], monitorOperation: Int => R) = {
    logger.warn("Consuming from Kafka...")
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group-id-unique")
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(callsTopic, monitorTopic))
    try {
      while (true) {
        val records = consumer.poll(1000).asScala.toList
        for (data <- records) {
          val value = data.value()
          data.topic match {
            case callsTopic => callOperation(Json.parse(value).as[Call])
            case monitorTopic => monitorOperation(value.toInt)
          }
        }
      }
    } finally consumer.close()
  }
}
