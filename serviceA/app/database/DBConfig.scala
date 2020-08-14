package database

import javax.inject.{Inject, Singleton}
import model.Call
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

@Singleton
class DBConfig @Inject()(conf: Configuration,
                         val mongoApi: ReactiveMongoApi)
                        (implicit val ec: ExecutionContext) {
  private[database] val callsCollection = for {
    collection <- mongoApi.database.map(_.collection[JSONCollection](conf.get[String]("mongodb.collections.calls")))
  } yield collection

  val calls = Await.result(callsCollection, Duration.Inf)
}
