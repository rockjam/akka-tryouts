import akka.actor.{Actor, Props}
import spray.can.Http

object TTTWebSocketServer {
  def props() = Props[TTTWebSocketServer]
}

class TTTWebSocketServer extends Actor {
  override def receive = {
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender
      val conn = context.actorOf(TicTacToeWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}

object SharedResourceWebSocketServer {
  def props() = Props[SharedResourceWebSocketServer]
}

class SharedResourceWebSocketServer extends Actor {
  override def receive = {
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender
      val conn = context.actorOf(SharedResourceWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}
