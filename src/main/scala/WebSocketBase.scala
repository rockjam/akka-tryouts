import akka.actor.{ActorSelection, Actor, ActorRef}
import spray.can.websocket.WebSocketServerWorker
import spray.can.websocket.frame.{CloseFrame, TextFrame}
import spray.json._

trait WebSocketBase extends Actor with WebSocketServerWorker {
  def serverConnection: ActorRef
  def pool: ActorSelection

  override def businessLogic = notReady

  def notReady: Receive = {
    case TextFrame(text) if (text utf8String) startsWith "join"  =>
      pool ! AcquireResource
      context become waitingForResource
    case TextFrame(_) =>
      import ResponseJsonProtocol._
      send(TextFrame(Failure("resource is not ready yet 1").toJson.toString))
    case CloseFrame(_, _) => context stop self
  }

  def waitingForResource: Receive = {
    case ResourceAcquired(res) => context become ready(res)
    case CloseFrame(_, _) => context stop self
    case _ =>
      import ResponseJsonProtocol._
      send(TextFrame(Failure("resource is not ready yet 2").toJson.toString))
  }

  def ready(sharedResource: ActorRef): Receive

}
