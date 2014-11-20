package web

import akka.actor.{Actor, Props}
import messagePong.MessagePongWorker
import observer.Subscriber
import spray.can.Http
import tictactoe.{ObservableState, TicTacToeWorker}
import cursors.CursorsWorker

object TWebSocketListener {
  def props() = Props[TWebSocketListener]
}

class TWebSocketListener extends Actor {
  override def receive = {
    case _: Http.Connected =>
      val serverConnection = sender
      val conn = context.actorOf(TicTacToeWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}

object SWebSocketListener {
  def props() = Props[SWebSocketListener]
}

class SWebSocketListener extends Actor {
  override def receive = {
    case _: Http.Connected =>
      val serverConnection = sender
      val conn = context.actorOf(MessagePongWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}

object CWebSocketListener {
  def props() = Props[CWebSocketListener]
}

class CWebSocketListener extends Actor {
  override def receive = {
    case _: Http.Connected =>
      val serverConnection = sender
      val conn = context.actorOf(CursorsWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}

object VWebSocketListener {
  def props() = Props[VWebSocketListener]
}

class VWebSocketListener extends Actor {
  override def receive = {
    case _: Http.Connected =>
      val serverConnection = sender
      val conn = context.actorOf(Subscriber props serverConnection)
      context.system.eventStream.subscribe(conn, classOf[ObservableState])
      serverConnection ! Http.Register(conn)
  }
}
