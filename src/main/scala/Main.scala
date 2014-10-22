import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import spray.can.server.UHttp
import spray.routing.SimpleRoutingApp



object Main extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("sockets")

  val server = system.actorOf(WebSocketServer.props(), "websockets")
  val resourcePool = system.actorOf(ResourcePool.props(), "resources")

  IO(UHttp) ! Http.Bind(server, "localhost", 9000)

}
