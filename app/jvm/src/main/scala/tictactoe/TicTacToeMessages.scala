package tictactoe

import spray.json._

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