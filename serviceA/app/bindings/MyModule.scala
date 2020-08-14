package bindings
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{KafkaConsumeMonitor}

class MyModule extends AbstractModule with AkkaGuiceSupport {
  override def configure = {
    bindActor[KafkaConsumeMonitor]("kafka-actor")
  }
}
