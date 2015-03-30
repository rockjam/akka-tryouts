package tictactoe

import akka.actor.{ActorRef, Props}
import shared._
import spray.routing.HttpServiceActor

object TicTacToeWorker {
  def props(serverConnection: ActorRef) = Props(classOf[TicTacToeWorker], serverConnection)
}

class TicTacToeWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase with StaticRoute {
  override def pool = context actorSelection "akka://sockets/user/tic-tac-toe-resources"

  override def businessLogicNoUpgrade = runRoute {
    path("ticTacToe") {
      getFromResource("ticTacToe.html")
    } ~ staticRoutes
  }

}
