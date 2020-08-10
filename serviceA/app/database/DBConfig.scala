package database

import javax.inject.{Inject, Singleton}
import model.Call
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

@Singleton
class DBConfig @Inject()(conf: Configuration,
                         val mongoApi: ReactiveMongoApi)
                        (implicit val ec: ExecutionContext) {
  private[database] val callsCollection = for {
    collection <- mongoApi.database.map(_.collection[JSONCollection](conf.get[String]("mongodb.collections.calls")))
    _ <- collection.indexesManager.ensure(
      Index(
        Seq(
          (Call.Phone, IndexType.Ascending)),
        name = Some("call_unique_index"),
        background = true,
        unique = true)
    )
  } yield collection

  val calls = Await.result(callsCollection, Duration.Inf)
}
