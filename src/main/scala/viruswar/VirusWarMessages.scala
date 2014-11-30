package viruswar

import shared.{GameMoment, Exchange}

case class GameMove(move:Move) extends Exchange

case class _Player(player:Player) extends Exchange

case class GameState(field:Field, state:GameMoment)