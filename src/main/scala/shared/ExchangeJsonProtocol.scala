package shared

import cursors.Coordinates
import messagePong.Message
import spray.json._

object ExchangeJsonProtocol extends DefaultJsonProtocol {

  implicit object ExchangeJsonWriter extends RootJsonWriter[Exchange] {
    def write(input: Exchange) = input match {
      case m: Message => m.toJson
      case s: Success => s.toJson
      case f: Failure => f.toJson
      case r: Player =>  r.toJson
      case g: GameMove => g.toJson
      case tgs: tictactoe.GameState => JsObject("field" -> JsString(tgs.field), "status" -> JsString(tgs.status.toString))
      case vgs: viruswar.GameState => JsObject(
        "field" -> JsArray(vgs.field.map(e => JsArray(e.map(x => JsString(x.toString))))),
        "status" -> JsString(vgs.status.toString))
      case c: Coordinates => c.toJson
    }
  }

  implicit val failureFormat = jsonFormat2(Failure)
  implicit val successFormat = jsonFormat1(Success)
  implicit val messageFormat = jsonFormat3(Message)
  implicit val playerFormat = jsonFormat1(Player)
  implicit val gameMoveFormat = jsonFormat2(GameMove)
  implicit val coordinatesFormat = jsonFormat2(Coordinates)
}