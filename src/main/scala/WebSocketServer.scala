import akka.actor.{Actor, Props}
import spray.can.Http

object WebSocketServer {
  def props() = Props[WebSocketServer]
}

class WebSocketServer extends Actor {
  override def receive = {
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender
      val conn = context.actorOf(WebSocketWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}
