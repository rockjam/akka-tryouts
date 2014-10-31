import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import spray.can.server.UHttp
import spray.routing.SimpleRoutingApp



object Main extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("sockets")

  import system._
  val resourcePool = actorOf(ResourcePool.props(), "resources")

  IO(UHttp) ! Http.Bind(actorOf(Router.props(), "router"), "localhost", 9000)
  IO(UHttp) ! Http.Bind(actorOf(WebSocketServer.props(), "websockets"), "localhost", 9001)

  import scala.concurrent.duration._
  scheduler.schedule(5 seconds, 5 seconds, resourcePool, ShowResources)


}
