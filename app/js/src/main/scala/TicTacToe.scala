import org.scalajs.dom._
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.Image
import shared._

import scala.scalajs.js.annotation.JSExport

@JSExport
object TicTacToe {

  @JSExport
  def main(body: html.Div) = {
    //assigned once - possibly not var
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
        case GameState(field, status) => drawField(field)
        case Player(ch) =>
          player = ch
          visualize(document.getElementById("sign"), player, 30)
        case _ => ""
      }
    }

    def waitingStatusOnMessage (elem:Node) = (evt: MessageEvent) => {
      val ex = upickle.read[SharedExchange](evt.data.toString)
      publishEvent(ex)
      ex match {
        case Success(_) | GameState(_,Win) | GameState(_,Tie) => visualize(elem, player)
        case _ => ""
      }
      ws.onmessage = ordinaryOnMessage _
    }

    def transformField(f: (Node, Int, Int) => Any) = for {
      (tr, i) <- document.getElementsByTagName("tr").zipWithIndex
      (td, j) <- tr.asInstanceOf[Document].getElementsByTagName("td").zipWithIndex
    } yield f(td, i, j)

    def bindClick() = transformField(
      (td: Node, i: Int, j: Int) => td.addEventListener[MouseEvent]("click", (e: MouseEvent) => makeTurn(td, j, i))
    )

    def drawField(field: String) = transformField (
        (td: Node, i: Int, j: Int) => visualize(td, field(3*i+j))
    )

    def visualize(elem: Node, sign: Char, size: Int = 90) = {
      val pic = document.createElement("img").asInstanceOf[Image]
      pic.height = size
      pic.width = size

      pic.src = sign match {
        case 'x' => "images/x.png"
        case 'X' => "images/x1.png"
        case 'o' => "images/o.png"
        case 'O' => "images/o1.png"
        case _ => ""
      }

      if (elem.firstChild != null) {
        elem.replaceChild(pic, elem.firstChild)
      } else {
        elem.appendChild(pic)
      }
    }

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

    bindClick()
  }

}


