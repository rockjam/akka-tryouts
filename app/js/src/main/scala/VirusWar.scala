import org.scalajs.dom._
import shared._

import scala.scalajs.js.annotation.JSExport

@JSExport
object VirusWar {

  @JSExport
  def main(body: html.Div) = {
    var player = '-'
    val ws = new WebSocket("ws://localhost:9005")

    ws.onopen = (e: Event) => {
      publishEvent(Opened)
      ws.send(upickle.write(Start))
    }

    ws.onmessage = ordinaryOnMessage _

    ws.onclose = (evt: Event) => publishEvent(Closed)

    def ordinaryOnMessage(evt: MessageEvent) = {
      val ex = upickle.read[SharedExchange](evt.data.toString)
      publishEvent(ex)
      ex match {
        case VirusWarGameState(field, _) => drawField(field)
        case Player(ch) =>
          player = ch
          Common.visualize(document.getElementById("sign"), player, 40)
        case _ => ""
      }
    }

    def bindClick() = Common.transformField (
      (td: Node, i: Int, j: Int) => td.addEventListener[MouseEvent]("click", (e: MouseEvent) => makeTurn(j, i))
    )

    def drawField(field:Vector[Vector[Char]]) = Common.transformField (
      (td: Node, i: Int, j: Int) => Common.visualize(td, field(i)(j), 30)
    )

    def makeTurn(x: Int, y: Int) = ws.send(upickle.write(GameMove(x,y)))

    def publishEvent(message: SharedExchange) =
      document.getElementById("messages").innerHTML = message match {
        case Success(_) => "Wait for your turn"
        case Failure(m, _) => m
        case VirusWarGameState(_, status) => status match {
          case Game => "Wait for your turn"
          case New => "Game began, your turn"
          case YourTurn => "Your turn"
          case Win => "Congratulations, you win!"
          case Lose => "Bad luck, you lose!"
          case Tie => "Game over, it is tie"
          case WrongMove => "Wrong Move, bastard"
        }

        case Opened => "Wait for your turn"
        case Closed => "Closed, reload page please"

        case _ => ""
      }

    body.appendChild(Common.createField(10))
    bindClick()
  }

}
