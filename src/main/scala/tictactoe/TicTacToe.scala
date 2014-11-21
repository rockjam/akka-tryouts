package tictactoe

import akka.actor.{Actor, ActorRef, Props}
import shared._

object TicTacToe {
  def props() = Props[TicTacToe]
}

class TicTacToe extends Actor with TicTacToeGame {
  import context._

  val initialState = GameState("---------", New)

  def receive = notReady

  def notReady = ({
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user))
    case _:GameMove => sender ! Failure("resource is not ready yet")
  }: Receive) orElse wrongMessageType

  def waitingForUser(FIRST: ActorRef) = ({
    case Join(FIRST) => FIRST ! Failure("you have joined already")
    case Join(user) =>
      val o = Player('o')
      user ! o
      user ! Success()

      val x = Player('x')
      FIRST ! x
      FIRST ! initialState
      context.system.eventStream.publish(GameView(self.path.name, initialState.field, InProgress, "Game bagan"))

      become(working(FIRST, x, user, o, initialState) )
    case _:GameMove => sender ! Failure("resource is not ready yet 3")
  }: Receive) orElse wrongMessageType

  def working(OWNER: ActorRef, ownerSign: Player, WAITER: ActorRef, waiterSign: Player, current: GameState): Receive = ({
    case move: GameMove =>
      sender match {
        case OWNER =>
          val newState = makeMove(current, move, ownerSign)
          if (newState.status != WrongMove) {
            context.system.eventStream.publish(GameView(
              self.path.name,
              newState.field,
              newState.status match {
                case Tie | Win | Lose => GameOver
                case Game | New => InProgress
              },
              "Message"))
          }
          newState.status match {
            case Game =>
              OWNER ! Success()
              WAITER ! newState
              become(working(WAITER, waiterSign, OWNER, ownerSign, newState))
            case Tie =>
              OWNER ! newState
              WAITER ! newState
              become(gameOver(newState))
            case Win =>
              OWNER ! newState
              WAITER ! newState.copy(status = Lose)
              become(gameOver(newState))
            case WrongMove =>
              OWNER ! newState
            case _ => throw new Error("inconsistent state")
          }
        case WAITER => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case Join(_) => sender ! Failure("cant join more users")
  }: Receive) orElse wrongMessageType

  def gameOver(finalState: GameState) = ({
    case Join(_) => sender ! Failure("cant join, game over")
    case _:GameMove => sender ! Failure("cant make more moves, game over")
  }:Receive) orElse wrongMessageType

  def wrongMessageType: Receive = {
    case _ => sender ! Failure("wrong message type")
  }

}
