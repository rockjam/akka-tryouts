import akka.actor.ActorRef
import spray.json._

trait Response

case class Join(actor: ActorRef)

case object AcquireResource

case class ResourceAcquired(resource: ActorRef)

case class Message(text:String, value:Int, status: String = "result") extends Response

object MessageJsonProtocol extends DefaultJsonProtocol {
  implicit val messageFormat = jsonFormat3(Message)
}

case class Success(status: String = "success") extends Response

case class Failure(message: String, status: String = "failure") extends Response


object ResponseJsonProtocol extends DefaultJsonProtocol {

  implicit object ResponseJsonWriter extends RootJsonWriter[Response] {
    def write(input: Response) = input match {
      case m: Message => m.toJson
      case s: Success => s.toJson
      case f: Failure => f.toJson
    }
  }

  implicit val failureFormat = jsonFormat2(Failure)
  implicit val successFormat = jsonFormat1(Success)
  implicit val messageFormat = jsonFormat3(Message)
}
