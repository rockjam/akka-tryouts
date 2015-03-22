package shared

import akka.actor.{Actor, ActorRef, ActorSelection}
import akka.io.Tcp._
import spray.can.websocket.WebSocketServerWorker
import spray.can.websocket.frame.{BinaryFrame, CloseFrame, TextFrame}
import spray.http.HttpRequest
import spray.json._

trait WebSocketBase extends Actor with WebSocketServerWorker {
  def serverConnection: ActorRef
  def pool: ActorSelection
  def businessLogicNoUpgrade: Receive

  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

  override def businessLogic = notReady

  def convertRequestFromClient[T >: SharedExchange](text: String): T

  def notReady: Receive = {
    case TextFrame(text) if upickle.read[SharedExchange](text.utf8String) == Start  =>
      pool ! AcquireResource
      context become waitingForResource
    case TextFrame(_) =>
      import ExchangeJsonProtocol._
      send(TextFrame(Failure("resource is not ready yet 1").toJson.toString))
    case CloseFrame(_, _) => context stop self
  }

  def waitingForResource: Receive = {
    case ResourceAcquired(res) => context become ready(res)
    case CloseFrame(_, _) => context stop self
    case _ =>
      import ExchangeJsonProtocol._
      send(TextFrame(Failure("resource is not ready yet 2").toJson.toString))
  }

  def ready(shared: ActorRef): Receive = {
    case TextFrame(text) if (text utf8String) startsWith "join" =>
      import ExchangeJsonProtocol._
      val failure = Failure("already acquired resource")
      send(TextFrame(failure.toJson.toString))
    case TextFrame(text) =>
      shared ! convertRequestFromClient(text utf8String)
    case m: SharedExchange =>
      send(TextFrame(upickle.write[SharedExchange](m)))
    case CloseFrame(_, _) | PeerClosed | Closed =>
      context stop shared
      context stop self

    //we don't expect these types
    case x: BinaryFrame => println("1 " + x)
    case x: HttpRequest => println("2 " + x)
    case x: AnyRef => println("3 " + x)
  }


}
