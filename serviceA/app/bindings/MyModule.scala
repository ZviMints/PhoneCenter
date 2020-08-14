package bindings
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{KafkaConsumeMonitor, KafkaProduceMonitor}

class MyModule extends AbstractModule with AkkaGuiceSupport {
  override def configure = {
    bindActor[KafkaConsumeMonitor]("kafka-consume-actor")
    bindActor[KafkaProduceMonitor]("kafka-produce-actor")
  }
}
