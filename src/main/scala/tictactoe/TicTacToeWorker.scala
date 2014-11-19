package tictactoe

import akka.actor.{ActorRef, Props}
import shared._
import spray.routing.HttpServiceActor
import spray.json._

object TicTacToeWorker {
  def props(serverConnection: ActorRef) = Props(classOf[TicTacToeWorker], serverConnection)
}

class TicTacToeWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase {
  override def pool = context actorSelection "akka://sockets/user/tic-tac-toe-resources"

  override def businessLogicNoUpgrade = runRoute {
    pathPrefix("js") {
      get {
        getFromResourceDirectory("js")
      }
    } ~
    pathPrefix("css") {
      get {
        getFromResourceDirectory("css")
      }
    } ~
    pathPrefix("images") {
      get {
        getFromResourceDirectory("images")
      }
    } ~
    path("ticTacToe") {
      getFromResource("ticTacToe.html")
    }
  }

  override def convertRequest[T >: Response](text: String): T = {
    import shared.ResponseJsonProtocol._
    text.parseJson.convertTo[GameMove]
  }

}
