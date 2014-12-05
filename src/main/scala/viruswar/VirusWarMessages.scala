package viruswar

import shared.{Exchange, GameMoment}

case class GameState(field:Field, status:GameMoment) extends Exchange