import org.scalajs.dom._
import org.scalajs.dom.raw.MessageEvent
import shared.{GameOver, GameState, GameView, VirusWarGameState}

import scala.scalajs.js.annotation.JSExport

@JSExport
object Viewer {

  @JSExport
  def main(body: html.Div) = {
    val ws = new WebSocket("ws://localhost:9004/")

    def setState(currentViews: Set[String]): Unit = {
      ws.onmessage = (evt: MessageEvent) => {
        val view = upickle.read[GameView](evt.data.toString)
        //add game field if it is not present
        if (!currentViews(view.id)) {
          val (field, cls) = view.gameState match {
            case _:GameState => (Common.createField(3), "tic-tac-toe")
            case _:VirusWarGameState => (Common.createField(10), "virus-war")
          }
          field.setAttribute("id", view.id)
          field.setAttribute("class", cls)
          body.appendChild(field)//replace with passsing in main
          setState(currentViews + view.id)
        }
        updateField(view)
        if (view.viewState == GameOver) {
          setTimeout(() => {
            body.removeChild(document.getElementById(view.id))
            setState(currentViews - view.id)
          }, 5000)
        }
      }

      def updateField(view: GameView) = {
        val updater = (cell: Node, cellIndex: Int, rowIndex: Int) => {
          val (sign, size) = view.gameState match {
            case GameState(field, _) => (field(3 * cellIndex + rowIndex), 80)
            case VirusWarGameState(field, _) => (field(rowIndex)(cellIndex), 30)
          }
          Common.visualize(cell, sign, size)
        }
        Common.transformField (document.getElementById(view.id), updater)
      }

    }

    setState(Set.empty)
  }

}