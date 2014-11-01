import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import spray.can.server.UHttp
import spray.routing.SimpleRoutingApp



object Main extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("sockets")

  import system._
  val ticTacToeResourcePool = actorOf(ResourcePool.props(TicTacToe.props()), "tic-tac-toe-resources")
  val sharedResourceResourcePool = actorOf(ResourcePool.props(SharedResource.props()), "shared-resources-resources")

  IO(UHttp) ! Http.Bind(actorOf(Router.props(), "router"), "localhost", 9000)
  IO(UHttp) ! Http.Bind(actorOf(TTTWebSocketServer.props(), "ttt-websockets"), "localhost", 9001)
  IO(UHttp) ! Http.Bind(actorOf(SharedResourceWebSocketServer.props(), "shared-resource-websockets"), "localhost", 9002)

  import scala.concurrent.duration._
  scheduler.schedule(5 seconds, 5 seconds, ticTacToeResourcePool, ShowResources)


}
