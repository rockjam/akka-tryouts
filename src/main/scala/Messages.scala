import akka.actor.ActorRef
import spray.json._

case object ShowResources

//common messages
case class Join(actor: ActorRef)

case object AcquireResource

case class ResourceAcquired(resource: ActorRef)

case class Success(status: String = "success") extends Response

case class Failure(message: String, status: String = "failure") extends Response

trait Response

//sharedResource messages
case class Message(text:String, value:Int, status: String = "result") extends Response

object MessageJsonProtocol extends DefaultJsonProtocol {
  implicit val messageFormat = jsonFormat3(Message)
}

//ticTacToe messages
case class GameState(field: String, status: Status) extends Response

//TODO не передавать player
case class GameMove(x:Int, y:Int, player:Char)

object GameMove extends DefaultJsonProtocol {
  implicit val gameMoveFormat = jsonFormat3(GameMove.apply)
}


object ResponseJsonProtocol extends DefaultJsonProtocol {

  implicit object ResponseJsonWriter extends RootJsonWriter[Response] {
    def write(input: Response) = input match {
      case m: Message => m.toJson
      case s: Success => s.toJson
      case f: Failure => f.toJson
      case g: GameState => JsObject("field" -> JsString(g.field), "status" -> JsString(g.status.toString))
    }
  }

  implicit val failureFormat = jsonFormat2(Failure)
  implicit val successFormat = jsonFormat1(Success)
  implicit val messageFormat = jsonFormat3(Message)
}
