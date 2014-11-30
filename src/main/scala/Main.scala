import akka.actor.ActorSystem
import akka.io.IO
import cursors.Cursors
import messagePong.MessagePong
import shared.ResourcePool
import spray.can.Http
import spray.can.server.UHttp
import tictactoe.TicTacToeActor
import web._

object Main extends App {
  implicit val system = ActorSystem("sockets")

  import system._
  actorOf(ResourcePool.props(TicTacToeActor.props()), "tic-tac-toe-resources")
  actorOf(ResourcePool.props(MessagePong.props()), "message-pong-resources")
  actorOf(ResourcePool.props(Cursors.props()), "cursors-resources")

  IO(UHttp) ! Http.Bind(actorOf(TWebSocketListener.props(), "t-websockets"), "localhost", 9001)
  IO(UHttp) ! Http.Bind(actorOf(SWebSocketListener.props(), "m-websockets"), "localhost", 9002)
  IO(UHttp) ! Http.Bind(actorOf(CWebSocketListener.props(), "c-websockets"), "localhost", 9003)
  IO(UHttp) ! Http.Bind(actorOf(VWebSocketListener.props(), "v-websockets"), "localhost", 9004)

}
