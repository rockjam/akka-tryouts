import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom._

case class Point(x:Double, y:Double)

@JSExport
object Cursors {

  @JSExport
  def main(canvas: html.Canvas) = {

    var ownCoord = Point(0,0)
    var alienCoord = Point(0,0)

    canvas.width = 480
    canvas.height = 320
    val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val ws = new WebSocket(url = "ws://localhost:9003")
    ws.onopen = (e: Event) => {
      println("opened")
      ws.send("join")
    }

    ws.onmessage = (evt: MessageEvent) => {
      if(evt.data != null) {
        val coord = JSON.parse(evt.data.toString)
        console.log(coord)
        alienCoord = Point(coord.x.asInstanceOf[Double], coord.y.asInstanceOf[Double])
        context.fillStyle = "#ccc"
        context.fillRect(0, 0, canvas.width, canvas.height)
        context.fillStyle = "#000"
        context.fillRect(alienCoord.x, alienCoord.y, 10, 10)
        context.fillRect(ownCoord.x, ownCoord.y, 10, 10)
      }
    }

    ws.onclose = (evt: CloseEvent) => println("close")

    canvas.onmousemove = playerMove _

    def playerMove(e: MouseEvent) = {
      context.fillStyle = "#ccc"
      context.fillRect(0, 0, canvas.width, canvas.height)
      context.fillStyle = "#000"
      ownCoord = Point(e.clientX, e.clientY)
      context.fillRect(ownCoord.x, ownCoord.y, 10, 10)
      context.fillRect(alienCoord.x, alienCoord.y, 10, 10)
      val json = JSON.parse("{ \"x\":%s, \"y\":%s }" format (ownCoord.x, ownCoord.y))
      ws.send(JSON.stringify(json))
      println(e)
    }


  }

}