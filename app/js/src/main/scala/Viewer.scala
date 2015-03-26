import org.scalajs.dom._
import org.scalajs.dom.raw.MessageEvent
import shared.{GameOver, GameView}

import scala.scalajs.js.annotation.JSExport

@JSExport
object Viewer {

  @JSExport
  def main() = {
    val ws = new WebSocket("ws://localhost:9004/")
    val currentViews = scala.collection.mutable.Map[String, GameView]()

    ws.onmessage = (evt: MessageEvent) => {
      val view = upickle.read[GameView](evt.data.toString)
      if (!currentViews.contains(view.id)) {
        val field = Common.createField(3)
        field.setAttribute("id", view.id)
        document.getElementById("content").appendChild(field)
        currentViews.put(view.id, view)
      }
      updateField(view)
      if(view.state == GameOver) {
        setTimeout(() => {
          currentViews.remove(view.id)
          document.getElementById("content").removeChild(document.getElementById(view.id))
        }, 5000)
      }
    }

    def updateField(view: GameView) = Common.transformField (
      document.getElementById(view.id),
      (td: Node, i: Int, j: Int) => Common.visualize(td, view.field(3*i+j), 85)
    )

  }
//  @JSExport
//  def main(currentViews:Map[String, GameView]): Map[String, GameView] = {
////    val state = currentViews
//    val ws = new WebSocket("ws://localhost:9004/")
//
//    ws.onmessage = (evt: MessageEvent) => {
//      val view = upickle.read[GameView](evt.data.toString)
//      val newState = currentViews.get(view.id) map {e => } getOrElse  {main(currentViews + (view.id -> view))}
//
//
//    }
//
//
//    currentViews
//  }

}