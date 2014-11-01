import akka.actor.{ActorRef, Props}
import spray.can.websocket.frame.{BinaryFrame, CloseFrame, TextFrame}
import spray.http.HttpRequest
import spray.json._

object TicTacToeWorker {
  def props(serverConnection: ActorRef) = Props(classOf[TicTacToeWorker], serverConnection)
}

class TicTacToeWorker(val serverConnection: ActorRef) extends WebSocketBase {
  override def pool = context actorSelection "akka://sockets/user/tic-tac-toe-resources"

  override def ready(shared: ActorRef): Receive = {
    case TextFrame(text) if (text utf8String) startsWith "join"  =>
          import ResponseJsonProtocol._
          val failure = Failure("already acquired resource")
          send(TextFrame(failure.toJson.toString))
    case TextFrame(text) =>
      import GameMove._
      val message = text
        .utf8String
        .parseJson
        .convertTo[GameMove]
      shared ! message
    case m: Response =>
      import ResponseJsonProtocol._
      val json = m.toJson
      send(TextFrame(json toString))
    case CloseFrame(_, _) => context stop self

    //we don't expect these types
    case x: BinaryFrame => println("1 " + x)
    case x: HttpRequest => println("2 " + x)
    case x: AnyRef => println("3 " + x)
  }

}
