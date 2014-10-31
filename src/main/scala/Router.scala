import akka.actor.Props
import spray.routing.HttpServiceActor

object Router {
  def props() = Props[Router]
}

class Router extends HttpServiceActor {

  override def receive = runRoute {
    path("ticTacToe") {
      getFromResource("ticTacToe.html")
    } ~
    path("some") {
      getFromResource("some.html")
    }
  }

}
