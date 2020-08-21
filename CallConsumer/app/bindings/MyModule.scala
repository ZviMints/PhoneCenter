package bindings

import actors.KafkaConsumeMonitor
import com.google.inject.AbstractModule
import kamon.Kamon
import play.api.libs.concurrent.AkkaGuiceSupport

class MyModule extends AbstractModule with AkkaGuiceSupport {
  override def configure = {
    bindActor[KafkaConsumeMonitor]("kafka-consume-actor")
    Kamon.init()
  }
}
