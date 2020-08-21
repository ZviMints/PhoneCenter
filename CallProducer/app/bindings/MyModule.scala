package bindings
import actors.KafkaProduceMonitor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class MyModule extends AbstractModule with AkkaGuiceSupport {
  override def configure = {
    bindActor[KafkaProduceMonitor]("kafka-produce-actor")
  }
}
