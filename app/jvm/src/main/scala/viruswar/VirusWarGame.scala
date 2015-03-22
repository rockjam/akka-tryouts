package viruswar

import shared.{VirusWarGameState, GameMove, Player}

trait VirusWarGame {
  def makeMove(m:GameMove, p:Player, f:Field) = {
    val newState = new VirusWarGameHelpers {}.makeMove((m.x, m.y), p.ch, f)
    VirusWarGameState(newState._1, newState._2)
  }
  def opponent(p:Player) = p match {
    case Player('x') => Player('o')
    case Player('o') => Player('x')
  }
}