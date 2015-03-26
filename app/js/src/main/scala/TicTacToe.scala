import org.scalajs.dom._
import shared._

import scala.scalajs.js.annotation.JSExport

@JSExport
object TicTacToe {

  @JSExport
  def main(body: html.Div) = {
    var player = '-'
    val ws = new WebSocket("ws://localhost:9001")

    ws.onopen = (e: Event) => {
      publishEvent(Opened)
      ws.send(upickle.write(Start))
    }

    ws.onmessage = ordinaryOnMessage _

    ws.onclose = (evt: Event) => publishEvent(Closed)

    def makeTurn(td: Node, x: Int, y: Int) = {
      ws.onmessage = waitingStatusOnMessage(td)
      ws.send(upickle.write(GameMove(x,y)))
    }

    def ordinaryOnMessage(evt: MessageEvent) = {
      val ex = upickle.read[SharedExchange](evt.data.toString)
      publishEvent(ex)
      ex match {
        case GameState(field, _) => drawField(field)
        case Player(ch) =>
          player = ch
          Common.visualize(document.getElementById("sign"), player, 30)
        case _ => ""
      }
    }

    def waitingStatusOnMessage (elem:Node) = (evt: MessageEvent) => {
      val ex = upickle.read[SharedExchange](evt.data.toString)
      publishEvent(ex)
      ex match {
        case Success(_) | GameState(_,Win) | GameState(_,Tie) => Common.visualize(elem, player, 90)
        case _ => ""
      }
      ws.onmessage = ordinaryOnMessage _
    }

    def bindClick() = Common.transformField(
      (td: Node, i: Int, j: Int) => td.addEventListener[MouseEvent]("click", (e: MouseEvent) => makeTurn(td, j, i))
    )

    def drawField(field: String) = Common.transformField (
      (td: Node, i: Int, j: Int) => Common.visualize(td, field(3*i+j), 90)
    )

    def publishEvent(message: SharedExchange) =
      document.getElementById("messages").innerHTML = message match {
        case Success(_) => "Wait for your turn"
        case Failure(m, _) => m
        case GameState(_, status) => status match {
          case New => "Game began, your turn"
          case Game => "Your turn"
          case Win => "Congratulations, you win!"
          case Lose => "Bad luck, you lose!"
          case Tie => "Game over, it is tie"
          case WrongMove => "Wrong Move, bastard"
        }

        case Opened => "Wait for your turn"
        case Closed => "Closed, reload page please"

        case _ => ""
      }

    body.appendChild(Common.createField(3))
    bindClick()
  }

}


