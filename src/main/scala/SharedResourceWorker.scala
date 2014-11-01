import akka.actor.{ActorRef, Props}
import spray.can.websocket.frame.{BinaryFrame, CloseFrame, TextFrame}
import spray.http.HttpRequest
import spray.json._

object SharedResourceWorker {
  def props(serverConnection: ActorRef) = Props(classOf[SharedResourceWorker], serverConnection)
}

class SharedResourceWorker(val serverConnection: ActorRef) extends WebSocketBase {
  override def pool = context actorSelection "akka://sockets/user/shared-resources-resources"

  override def ready(shared: ActorRef): Receive = {
    case TextFrame(text) if (text utf8String) startsWith "join"  =>
          import ResponseJsonProtocol._
          val failure = Failure("already acquired resource")
          send(TextFrame(failure.toJson.toString))
    case TextFrame(text) =>
          import MessageJsonProtocol._
          val message = text
            .utf8String
            .parseJson
            .convertTo[Message]
          shared ! message
    case m: Response =>
      import ResponseJsonProtocol._
      val json = m.toJson.toString
      send(TextFrame(json))
    case _: CloseFrame => context stop self

    //we dont expect these types
    case x: BinaryFrame => println("1 " + x)
    case x: HttpRequest => println("2 " + x)
    case x: AnyRef => println("3 " + x)
  }

}
