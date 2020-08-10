package controllers

import com.vatbox.deadsea.common.Metrics
import database.CallDao
import javax.inject.{Inject, Singleton}
import model.Call
import play.api.Configuration
import play.api.cache.{AsyncCacheApi, NamedCache}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import play.api.cache.AsyncCacheApi
import play.api.cache.redis.Done
import play.cache.NamedCache

import scala.concurrent.ExecutionContext
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class HomeController @Inject()(val cc: ControllerComponents,
                               callDao: CallDao,
                               @NamedCache("serviceA") cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit request =>
    callDao.insertOne(Call("0523348262"))
      .map(_ =>  Ok(views.html.index()))
      .recover { case _ => Accepted }
  }

  def cache(): Action[AnyContent] = Action.async {
    for {
      calls <- callDao.find(Json.obj(s"${Call.Phone}" -> "0523348262"))
      _ <- cache.set("key", calls)
      value <- cache.get[List[Call]]("key")
    } yield Ok(s"value from key in the cache is equal to $value")
}

  def increaseKamon() = Action {
    Metrics.successCounter.increment()
    Ok
  }
}
