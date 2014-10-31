trait TicTacToeGame {
  def makeMove(state: GameState, move: GameMove) = {
    def calcIndex = move.y * 3 + move.x
    def canMakeMove = {
      def isFree = state.field(calcIndex) == '-'
      move.x < 3 &&
        move.x >= 0 &&
        move.y < 3 &&
        move.y >= 0 &&
        isFree
    }
    if (canMakeMove) {
      val newField = state.field.updated(calcIndex, move.player)
      GameState(newField, score(newField))
    } else
      GameState(state.field, WrongMove)
  }
  def score(field: String) = {
    def same (a: Char, b: Char, c: Char) = a != '-' && a == b && b == c
    def horizontalWin =
      same(field(0), field(1), field(2)) ||
        same(field(3), field(4), field(5)) ||
        same(field(6), field(7), field(8))
    def verticalWin =
      same(field(0), field(3), field(6)) ||
        same(field(1), field(4), field(7)) ||
        same(field(2), field(5), field(8))
    def diagonalWin =
      same(field(0), field(4), field(8)) ||
        same(field(2), field(3), field(6))
    field.forall(a => a != '-') match {
      case true => Tie
      case false => if (horizontalWin || verticalWin || diagonalWin) Win else Game
    }
  }
}

sealed trait Status
case object Tie extends Status
case object Win extends Status
case object Lose extends Status
case object Game extends Status
case object New extends Status
case object WrongMove extends Status
