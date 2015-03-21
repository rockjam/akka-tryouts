import org.scalajs.dom._
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.Image

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

@JSExport
object TicTacToe {

  @JSExport
  def main(body: html.Div) = {
    //assigned once - possibly not var
    var player = "-"
    val ws = new WebSocket("ws://localhost:9001")

    ws.onopen = (e: Event) => {
      publishEvent(e, "opened")
      ws.send("join")
    }

    ws.onmessage = ordinaryOnMessage _

    ws.onclose = (evt: Event) => publishEvent(evt, "closed")

    def makeTurn(td: Node, x: Int, y: Int) = {
      ws.onmessage = waitingStatusOnMessage(td)
      ws.send(JSON.stringify(JSON.parse("{\"x\": %d, \"y\": %d}" format(x, y))))
    }

    def ordinaryOnMessage(evt: MessageEvent) = {
      publishEvent(evt, "message")
      val json = JSON.parse(evt.data.toString)
      if (!scalajs.js.isUndefined(json.field)) {
        drawField(json.field.toString)
      }
      if (!scalajs.js.isUndefined(json.ch)) {
        player = json.ch.toString
      }
    }

    def waitingStatusOnMessage (elem:Node) = (evt: MessageEvent) => {
      publishEvent(evt, "message")
      val json = JSON.parse(evt.data.toString)
      if (json.status.toString == "success" || json.status.toString == "Win" || json.status.toString == "Tie") {
        visualize(elem, player)
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
        (td: Node, i: Int, j: Int) => visualize(td, field(3*i+j).toString)
    )

    def visualize(elem: Node, sign: String, size: Int = 90) = {
      val pic = document.createElement("img").asInstanceOf[Image]
      pic.height = size
      pic.width = size

      pic.src = sign match {
        case "x" => "images/x.png"
        case "X" => "images/x1.png"
        case "o" => "images/o.png"
        case "O" => "images/o1.png"
        case _ => ""
      }

      if (elem.firstChild != null) {
        elem.replaceChild(pic, elem.firstChild)
      } else {
        elem.appendChild(pic)
      }
    }

    def publishEvent(message: Event, `type`: String) = {
      val text = message match {
        case m: MessageEvent =>
          val data = JSON.parse(m.data.toString)
          //TODO возможно место не тут
          if (!scalajs.js.isUndefined(data.ch)) {
            val sign = document.getElementById("sign")
            visualize(sign, data.ch.toString, 40)
            ""
          } else {
            data.status.toString match {
              case "success" => "Wait for your turn"
              case "failure" => data.message.toString
              case "New" => "Game began, your turn"
              case "Game" => "Your turn"
              case "Win" => "Congratulations, you win!"
              case "Lose" => "Bad luck, you lose!"
              case "Tie" => "Game over, it is tie"
              case "WrongMove" => "Wrong Move, bastard"
            }
          }
        case _ if `type` == "opened" => "Wait for your opponent"
        case _ => ""
      }
      document.getElementById("messages").innerHTML = text
    }

    bindClick()
  }

}


