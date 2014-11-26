package tictactoe

import shared.{GameMoment, Exchange}
import spray.json._

//TODO add WinGameState(field, winCells, status)
case class GameState(field: String, status: GameMoment) extends Exchange

case class Player(ch:Char) extends Exchange

case class GameMove(x:Int, y:Int) extends Exchange

case class GameView(id:String, field:String, state:ViewState, message:String)

sealed trait ViewState extends DefaultJsonProtocol
case object InProgress extends ViewState
case object GameOver extends ViewState

object GameView extends DefaultJsonProtocol {
  implicit object ObservableStateJsonFormat extends RootJsonWriter[GameView] {
    def write(s:GameView) =
      JsObject("id" -> JsString(s.id), "field" -> JsString(s.field), "state" -> JsString(s.state.toString), "message" -> JsString(s.message))
  }
}