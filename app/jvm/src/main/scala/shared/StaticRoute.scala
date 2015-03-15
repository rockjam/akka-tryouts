package shared

import spray.util._

trait StaticRoute extends spray.routing.Directives {
  implicit def actorRefFactory: akka.actor.ActorRefFactory

  def staticRoutes = {
    pathPrefix("js") {
      get {
        getFromResource("scala-ws-fastopt.js")
      }
    } ~ pathPrefix("css") {
      get {
        getFromResourceDirectory("css")
      }
    } ~ pathPrefix("images") {
      get {
        getFromResourceDirectory("images")
      }
    }
  }
}
