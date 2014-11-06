package web

import akka.actor.{Actor, Props}
import messagePong.MessagePongWorker
import spray.can.Http
import tictactoe.TicTacToeWorker

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
