package bindings
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{KafkaProduceMonitor}

class MyModule extends AbstractModule with AkkaGuiceSupport {
  override def configure = {
    bindActor[KafkaProduceMonitor]("kafka-produce-actor")
  }
}
