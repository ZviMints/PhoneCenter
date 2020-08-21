package database

import javax.inject.{Inject, Singleton}
import model.Event
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
  private[database] val eventsCollection = for {
    collection <- mongoApi.database.map(_.collection[JSONCollection](conf.get[String]("mongodb.collections.events")))
    // For fetchByStatus
    _ <- collection.indexesManager.ensure(
      Index(Seq(
        (Event.Status, IndexType.Ascending),
        (Event.LockedTime, IndexType.Ascending)),
        name = Some("status_lockedTime"),
        background = true))
  } yield collection

  val events = Await.result(eventsCollection, Duration.Inf)
}
