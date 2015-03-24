package web

import akka.actor.{Actor, Props}
import shared.GameView
import viewer.Viewer
import spray.can.Http
import tictactoe.TicTacToeWorker
import cursors.CursorsWorker
import viruswar.VirusWarWorker

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
      val conn = context.actorOf(Viewer props serverConnection)
      context.system.eventStream.subscribe(conn, classOf[GameView])
      serverConnection ! Http.Register(conn)
  }
}

object VWWebSocketListener {
  def props() = Props[VWWebSocketListener]
}

class VWWebSocketListener extends Actor {
  override def receive = {
    case _: Http.Connected =>
      val serverConnection = sender
      val conn = context.actorOf(VirusWarWorker props serverConnection)
      serverConnection ! Http.Register(conn)
  }
}
