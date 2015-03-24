import org.scalajs.dom._
import shared.{Coordinates, SharedExchange, Start}

import scala.scalajs.js.annotation.JSExport

case class Point(x:Double, y:Double)

@JSExport
object Cursors {

  @JSExport
  def main(canvas: html.Canvas) = {

    var ownCoord = Coordinates(0,0)
    var alienCoord = Coordinates(0,0)

    canvas.width = 480
    canvas.height = 320

    val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val ws = new WebSocket(url = "ws://localhost:9003")

    ws.onopen = (e: Event) => ws.send(upickle.write(Start))

    ws.onmessage = (evt: MessageEvent) => {
      upickle.read[SharedExchange](evt.data.toString) match {
        case coord:Coordinates =>
          alienCoord = coord
          context.fillStyle = "#ccc"
          context.fillRect(0, 0, canvas.width, canvas.height)
          context.fillStyle = "#000"
          context.fillRect(coord.x, coord.y, 10, 10)
          context.fillRect(ownCoord.x, ownCoord.y, 10, 10)
        case data@_ => console.log(s"we are here with $data")
      }
    }

    ws.onclose = (evt: CloseEvent) => println("close")

    canvas.onmousemove = playerMove _

    def playerMove(e: MouseEvent) = {
      context.fillStyle = "#ccc"
      context.fillRect(0, 0, canvas.width, canvas.height)
      context.fillStyle = "#000"
      ownCoord = Coordinates(e.clientX.toInt, e.clientY.toInt)
      context.fillRect(ownCoord.x, ownCoord.y, 10, 10)
      context.fillRect(alienCoord.x, alienCoord.y, 10, 10)
      ws.send(upickle.write(ownCoord))
    }
  }

}