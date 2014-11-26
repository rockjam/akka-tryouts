package viruswar

import shared.{WrongMove, Game, GameMoment}

//TODO replace Vectors with List for uniform and convinience
object VirusWarGame {

  implicit def String2Field(from:String): Field = {
    val acc = from.stripMargin.replaceAll("\n", "")
    val res = augmentString(acc).foldLeft(Vector[Vector[Char]](Vector()))((acc, el) =>
      if (acc.last.length == 10)
        acc :+ Vector[Char](el)
      else
        acc.init :+ (acc.last :+ el)
    )
    if(res.length == 10 && res.forall(_.length == 10))
      res
    else
      throw new Error("wrong field")
  }


  val initialField:Field = Vector.fill(10,10)('-')
}

trait VirusWarGame {

  def makeMove(move:Move, player:Player, field:Field) =
    if (canMakeMove(move,player,field)) {
      val (x,y) = move
      val newField = field.updated(y, field(y).updated(x, player))
      (newField, score(newField))
    } else {
      (field, WrongMove)
    }

  def score(field:Field):GameMoment = Game//todo - implement

  def isFirstTurn(player:Player, field:Field) =  field.flatten.forall(_ != player)

  def validNeighborCells(move:Move) = {
    val (x,y) = move
    for {
      i <- -1 to 1
      j <- -1 to 1
      if  !(j == 0 && i == 0) && validCell(x + i, y + j)
    }  yield (x + i, y + j)
  }

  def validCell(cell:Move) = {
    val (x,y) = cell
    x >= 0 && x <= 9 && y >= 0 && y <= 9
  }

  def freeCell(move:Move, player:Player, field:Field) = {
    val (x,y) = move
    field(y)(x) match {
      case '-' => true
      case s if s == opponent(player) => true
      case _ => false
    } 
  }
  
  def opponent(player:Player) = if (player == 'x') 'o' else 'x'

  def haveLiveNeighbor(move:Move, player:Player, field:Field):Boolean = {
    def loop(xs: IndexedSeq[Move]): Boolean = xs match {
      case Vector() => false
      case (x, y) +: tail => field(y)(x) match {
        case p if p == player => true
        case p if p == player.toUpper => haveLiveNeighbor((x, y), player, field)
        case _ => loop(tail)
      }
    }
    loop(validNeighborCells(move))
  }

  //isValidMove
  def canMakeMove(move: Move, player: Player, field: Field): Boolean =
    if (isFirstTurn(player, field))
      (move, player) match {
        case ((0, 0), 'x') => true
        case ((9, 9), 'o') => true
        case _ => false
      }
    else
      validCell(move) &&
      freeCell(move, player, field) &&
      haveLiveNeighbor(move, player, field)
}