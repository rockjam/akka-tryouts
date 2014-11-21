package tictactoe

import shared.Response
import spray.json._

case class GameState(field: String, status: Status) extends Response

case class Player(ch:Char) extends Response

case class GameMove(x:Int, y:Int) extends Response

case class ObservableState(id:String, field:String, state:ViewState, message:String)

sealed trait ViewState extends DefaultJsonProtocol
case object InProgress extends ViewState
case object GameOver extends ViewState



object ObservableState extends DefaultJsonProtocol {
  implicit object ObservableStateJsonFormat extends RootJsonWriter[ObservableState] {
    def write(s:ObservableState) =
      JsObject("id" -> JsString(s.id), "field" -> JsString(s.field), "state" -> JsString(s.state.toString), "message" -> JsString(s.message))
  }
}