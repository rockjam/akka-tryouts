import akka.actor.ActorSystem
import akka.io.IO
import messagePong.MessagePong
import shared.ResourcePool
import spray.can.Http
import spray.can.server.UHttp
import spray.routing.SimpleRoutingApp
import tictactoe.TicTacToe
import web.{SWebSocketListener, TWebSocketListener}


object Main extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("sockets")

  import system._
  val ticTacToeResourcePool = actorOf(ResourcePool.props(TicTacToe.props()), "tic-tac-toe-resources")
  val sharedResourceResourcePool = actorOf(ResourcePool.props(MessagePong.props()), "message-pong-resources")

  IO(UHttp) ! Http.Bind(actorOf(TWebSocketListener.props(), "t-websockets"), "localhost", 9001)
  IO(UHttp) ! Http.Bind(actorOf(SWebSocketListener.props(), "m-websockets"), "localhost", 9002)

}
