import org.scalajs.dom._
import org.scalajs.dom.raw.MessageEvent
import shared.{GameOver, GameView}

import scala.scalajs.js.annotation.JSExport

@JSExport
object Viewer {

  @JSExport
  def main() = {
    val ws = new WebSocket("ws://localhost:9004/")

    def inner(currentViews: Set[String] = Set()): Unit = {
      ws.onmessage = (evt: MessageEvent) => {
        val view = upickle.read[GameView](evt.data.toString)
        if (!currentViews.contains(view.id)) {
          val field = Common.createField(3)
          field.setAttribute("id", view.id)
          document.getElementById("content").appendChild(field)
          inner(currentViews + view.id)
        }
        updateField(view)
        if (view.state == GameOver) {
          setTimeout(() => {
            document.getElementById("content").removeChild(document.getElementById(view.id))
            inner(currentViews - view.id)
          }, 5000)
        }
      }

      def updateField(view: GameView) = Common.transformField(
        document.getElementById(view.id),
        (td: Node, i: Int, j: Int) => Common.visualize(td, view.field(3 * i + j), 85)
      )
    }

    inner()

  }

}