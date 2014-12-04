package tictactoe

import akka.actor.{ActorRef, Props}
import shared._
import spray.routing.HttpServiceActor
import spray.json._

object TicTacToeWorker {
  def props(serverConnection: ActorRef) = Props(classOf[TicTacToeWorker], serverConnection)
}

class TicTacToeWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase with StaticRoute {
  override def pool = context actorSelection "akka://sockets/user/tic-tac-toe-resources"

  override def businessLogicNoUpgrade = runRoute {
    staticRoutes ~
    path("ticTacToe") {
      getFromResource("ticTacToe.html")
    }
  }

  override def convertRequest[T >: Exchange](text: String): T = {
    import shared.ExchangeJsonProtocol._
    text.parseJson.convertTo[GameMove]
  }

}
