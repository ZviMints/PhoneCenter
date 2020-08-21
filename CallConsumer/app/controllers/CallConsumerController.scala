package controllers

import javax.inject._
import play.api.cache.AsyncCacheApi
import play.api.mvc._
import play.cache.NamedCache

import scala.concurrent.ExecutionContext


@Singleton
class CallConsumerController @Inject()(@NamedCache("CallConsumer") cache: AsyncCacheApi, val controllerComponents: ControllerComponents)(implicit val ec: ExecutionContext) extends BaseController {

  // sbt runProd
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def resetCache() = Action.async {
    cache.removeAll().map(_ => Ok)
  }
}
