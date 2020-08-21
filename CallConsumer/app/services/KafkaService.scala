package services

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

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

@Singleton
class KafkaService @Inject()(conf: Configuration)(implicit val ec: ExecutionContext) extends LazyLogging {

  val topic = conf.get[String]("CallConsumer.kafka.topic")

  def consumeFromKafka(): List[Call] = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9094")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group")
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(topic))
    consumer.poll(Duration.ZERO).asScala.toList.map(_.value).map(Json.parse(_).as[Call])
  }
}
