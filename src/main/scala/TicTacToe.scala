import akka.actor.{Actor, ActorRef, Props}

object TicTacToe {
  def props() = Props[TicTacToe]

  case class GameState(field: String, status: Status)

  case class GameMove(x: Int, y: Int, player: Char)

}

sealed trait Status
case object Tie extends Status
case object Win extends Status
case object Game extends Status
case object New extends Status
case object WrongMove extends Status

trait TicTacToeGame {

  import TicTacToe._
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
    def checkHorizontal =
      same(field(0), field(1), field(2)) ||
        same(field(3), field(4), field(5)) ||
        same(field(6), field(7), field(8))
    def checkVertical =
      same(field(0), field(3), field(6)) ||
        same(field(1), field(4), field(7)) ||
        same(field(2), field(5), field(8))
    def checkDiagonal =
      same(field(0), field(4), field(8)) ||
        same(field(2), field(3), field(6))
    field.forall(a => a != '-') match {
      case true => Tie
      case false => if (checkHorizontal || checkVertical || checkDiagonal) Win else Game
    }
  }
}

class TicTacToe extends Actor with TicTacToeGame {
  import context._
  import TicTacToe._

  def initialState = GameState("---------", New)

  def receive = notReady

  def notReady = ({
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user))
    case _:Message => sender ! Failure("resource is not ready yet")
  }: Receive) orElse wrongMessageType

  def waitingForUser(FIRST: ActorRef) = ({
    case Join(FIRST) => FIRST ! Failure("you have joined already")
    case Join(user) =>
      user ! Success()
      FIRST ! initialState
      become(working(FIRST, user, initialState) )
    case _:Message => sender ! Failure("resource is not ready yet 3")
  }: Receive) orElse wrongMessageType

  def working(OWNER: ActorRef, WAITER: ActorRef, current: GameState):Receive = ({
    case move: GameMove =>
      sender match {
        case OWNER => println("hello");
//          val newState = makeMove(current, move)
//              newState.status match {
//
//              }
//
//
//
//          match {
//            case GameState(field, "game") =>
//
//            case GameState(field, "win") =>
//
//            case GameState(field, "tie") =>
//            case GameState(field, "wrong move") =>
//          }
//          OWNER ! Success()
//          WAITER ! message
//          become(working(WAITER, OWNER, message) )
        case WAITER => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case Join(_) => sender ! Failure("cant join more users")
  }: Receive) orElse wrongMessageType

  def wrongMessageType: Receive = {
    case _ => sender ! Failure("wrong message type")
  }

}
