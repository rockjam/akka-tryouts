import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import spray.can.websocket.WebSocketServerWorker
import spray.can.websocket.frame.{BinaryFrame, CloseFrame, TextFrame}
import spray.http.HttpRequest
import spray.json._

object WebSocketWorker {
  def props(serverConnection: ActorRef) = Props(classOf[WebSocketWorker], serverConnection)
}

class WebSocketWorker(val serverConnection: ActorRef) extends Actor with WebSocketServerWorker with ActorLogging {
  val pool = context actorSelection "akka://sockets/user/resources"

  override def businessLogic = notReady

  def notReady: Receive = {
    case frame: TextFrame =>
      frame.payload.utf8String match {
        case x if x startsWith "join" =>
          pool ! AcquireResource
          context become waitingForResource
        case _ =>
          import ResponseJsonProtocol._
          send(TextFrame(Failure("resource is not ready yet 1").toJson.toString))
      }
    case _: CloseFrame => context stop self
  }

  def waitingForResource: Receive = {
    case ResourceAcquired(res) => context become ready(res)
    case _: CloseFrame => context stop self
    case _ =>
      import ResponseJsonProtocol._
      send(TextFrame(Failure("resource is not ready yet 2").toJson.toString))
  }

  def ready(shared: ActorRef): Receive = {
    case frame: TextFrame =>
      frame.payload.utf8String match {
        case x if x.startsWith("join") =>
          import ResponseJsonProtocol._
          val failure = Failure("already acquired resource")
          send(TextFrame(failure.toJson.toString))
        case x =>
          import MessageJsonProtocol._
          val message = x
            .parseJson
            .convertTo[Message]
          shared ! message
      }

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
