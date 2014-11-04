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

  IO(UHttp) ! Http.Bind(actorOf(TWebSocketListener.props(), "s-websockets"), "localhost", 9001)
  IO(UHttp) ! Http.Bind(actorOf(SWebSocketListener.props(), "t-websockets"), "localhost", 9002)

}
