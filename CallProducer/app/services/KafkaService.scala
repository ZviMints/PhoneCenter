package services


import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import dto.Call
import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.producer._
import play.api.Configuration
import play.api.libs.json.Json
import serializers.CallSerializer.CallFormat

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaService @Inject()(conf: Configuration)(implicit val ec: ExecutionContext) extends LazyLogging {

  // pre-start kafka: https://kafka.apache.org/quickstart
  // kafka-play: https://dzone.com/articles/hands-on-apache-kafka-with-scala

  val topic = conf.get[String]("CallProducer.kafka.topic")

  def writeToKafka(call: Call) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val value = Json.stringify(Json.toJson(call))
    val record = new ProducerRecord[String, String](topic, "key", value)
    producer.send(record)
    producer.close()
  }
}
