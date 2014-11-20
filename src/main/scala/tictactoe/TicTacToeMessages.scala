package tictactoe

import akka.actor.ActorRef
import shared.Response
import spray.json._

case class GameState(field: String, status: Status) extends Response

case class Player(ch:Char) extends Response

case class GameMove(x:Int, y:Int) extends Response

case class ObservableState(subject:ActorRef, state:GameState)

object ObservableState extends DefaultJsonProtocol {
  implicit object ObservableStateJsonFormat extends RootJsonWriter[ObservableState] {
    def write(s:ObservableState) =
      JsObject("subject" -> JsString(s.subject.path.name),
        "state" -> JsObject("field" -> JsString(s.state.field), "status" -> JsString(s.state.status.toString)))
  }
}