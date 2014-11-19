package tictactoe

import shared.Response

case class GameState(field: String, status: Status) extends Response

case class Player(ch:Char) extends Response

case class GameMove(x:Int, y:Int) extends Response
