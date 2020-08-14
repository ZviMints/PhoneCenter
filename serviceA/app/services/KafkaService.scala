package services


import java.time.Duration

import javax.inject.{Inject, Singleton}
import model.Call
import org.apache.kafka.clients.producer._
import play.api.libs.json.Json
import serializers.CallSerializer._
import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties
import com.typesafe.scalalogging.LazyLogging
import play.api.Configuration

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class KafkaService @Inject()(conf: Configuration)(implicit val ec: ExecutionContext) extends LazyLogging {

  val topic = conf.get[String]("serviceA.kafka.topic")

  def writeToKafka(call: Call) = {
    Future {
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9094")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      val producer = new KafkaProducer[String, String](props)
      val value = Json.stringify(Json.toJson(call))
      val record = new ProducerRecord[String, String](topic, "key", value)
      producer.send(record)
      producer.close()
    }
  }

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
