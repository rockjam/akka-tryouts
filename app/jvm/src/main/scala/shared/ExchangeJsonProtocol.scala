package shared

import spray.json._

object ExchangeJsonProtocol extends DefaultJsonProtocol {

  implicit object ExchangeJsonWriter extends RootJsonWriter[SharedExchange] {
    def write(input: SharedExchange) = input match {
      case m: Message => m.toJson
      case s: Success => s.toJson

      case f: Failure => f.toJson
      case r: Player =>  r.toJson
      case g: GameMove => g.toJson
      case tgs: GameState => JsObject("field" -> JsString(tgs.field), "status" -> JsString(tgs.status.toString))
      case vgs: VirusWarGameState => JsObject(
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