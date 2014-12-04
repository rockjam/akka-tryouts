package viruswar

import shared._

object VirusWarGameHelpers {

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

trait VirusWarGameHelpers {

  def makeMove(move:Move, player:_Player, field:Field) =
    if (canMakeMove(move, player, field)) {
      val (x,y) = move
      val cc = changedCell(move, player, field).get
      val newField = field.updated(y, field(y).updated(x, cc))
      (newField, score(player, newField))
    } else {
      (field, WrongMove)
    }

  def score(player: _Player, field: Field): GameMoment = {
    def calculateLiveCells(p:_Player) = field.foldLeft(0)((acc,v) => acc + v.count(_==p))
    field match {
      //game field is full. need to find out, who won
      case f if f.flatten.forall(_ != '-') =>
        val pScore = calculateLiveCells(player)
        val opScore = calculateLiveCells(opponent(player))
        pScore compare opScore match {
          case 1 => Win
          case -1 => Lose
          case 0 => Tie
        }
      case f if !isFirstTurn(opponent(player), f) && f.flatten.forall(_ != opponent(player)) => Win
      case _ => Game
    }
  }

  //turn into HOF maybe?
  def isFirstTurn(p:_Player, f:Field) =  f.flatten.forall(_ != p)

  def validNeighborCells(move:Move) = {
    val (x,y) = move
    for {
      i <- -1 to 1
      j <- -1 to 1
      if  !(j == 0 && i == 0) && validCell(x + i, y + j)
    } yield (x + i, y + j)
  }

  def validCell(cell:Move) = {
    val (x,y) = cell
    x >= 0 && x <= 9 && y >= 0 && y <= 9
  }

  //todo find some good name
  def changedCell(move:Move, player:_Player, field:Field) = {
    val (x,y) = move
    field(y)(x) match {
      case '-' => Some(player)
      case s if s == opponent(player) => Some(player.toUpper)
      case _ => None
    } 
  }
  
  def opponent(player:_Player) = if (player == 'x') 'o' else 'x'

  def haveLiveNeighbor(cell:Move, player:_Player, field:Field):Boolean = {
    def loop(xs: IndexedSeq[Move]): Boolean = xs match {
      case Vector() => false
      case (x, y) +: tail => field(y)(x) match {
        case p if p == player => true
        case p if p == player.toUpper => haveLiveNeighbor((x, y), player, field)
        case _ => loop(tail)
      }
    }
    loop(validNeighborCells(cell))
  }

  //isValidMove
  def canMakeMove(move: Move, player: _Player, field: Field): Boolean =
    if (isFirstTurn(player, field))
      (move, player) match {
        case ((0, 0), 'x') => true
        case ((9, 9), 'o') => true
        case _ => false
      }
    else
      validCell(move) &&
      changedCell(move, player, field).isDefined &&
      haveLiveNeighbor(move, player, field)
}