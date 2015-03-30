package shared

trait StaticRoute extends spray.routing.Directives {
  implicit def actorRefFactory: akka.actor.ActorRefFactory

  def staticRoutes = {
   pathPrefix("css") {
      get {
        getFromResourceDirectory("css")
      }
    } ~ pathPrefix("images") {
      get {
        getFromResourceDirectory("images")
      }
    } ~ getFromResourceDirectory(".")
  }
}
