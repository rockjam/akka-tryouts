package shared

import cursors.Coordinates
import messagePong.Message
import spray.json._
import tictactoe.{GameMove, GameState, Player}

object ResponseJsonProtocol extends DefaultJsonProtocol {

  implicit object ResponseJsonWriter extends RootJsonWriter[Response] {
    def write(input: Response) = input match {
      case m: Message => m.toJson
      case s: Success => s.toJson
      case f: Failure => f.toJson
      case r: Player =>  r.toJson
      case g: GameMove => g.toJson
      case g: GameState => JsObject("field" -> JsString(g.field), "status" -> JsString(g.status.toString))
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