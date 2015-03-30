package tictactoe

import akka.actor.{Actor, ActorRef, Props}
import shared._

object TicTacToeActor {
  def props() = Props[TicTacToeActor]
}

class TicTacToeActor extends Actor with TicTacToeGame {
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
      system.eventStream.publish(GameView(self.path.toString, initialState, InProgress, "Game began"))

      become(working(FIRST, x, user, o, initialState) )
    case _:GameMove => sender ! Failure("resource is not ready yet 3")
  }: Receive) orElse wrongMessageType

  def working(owner: ActorRef, ownerSign: Player, waiter: ActorRef, waiterSign: Player, current: GameState): Receive = ({
    case move: GameMove =>
      sender match {
        case `owner` =>
          val newState = makeMove(current, move, ownerSign)
          if (newState.status != WrongMove) {
            system.eventStream.publish(GameView(
              self.path.toString,
              newState,
              newState.status match {
                case Tie | Win | Lose => GameOver
                case Game | New => InProgress
                case _ => throw new Error("inconsistent state")
              },
              "Message"))
          }
          newState.status match {
            case Game =>
              owner ! Success()
              waiter ! newState
              become(working(waiter, waiterSign, owner, ownerSign, newState))
            case Tie =>
              owner ! newState
              waiter ! newState
              become(gameOver(newState))
            case Win =>
              owner ! newState
              waiter ! newState.copy(status = Lose)
              become(gameOver(newState))
            case WrongMove =>
              owner ! newState
            case _ => throw new Error("inconsistent state")
          }
        case `waiter` => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case Join(_) => sender ! Failure("cant join more users")
  }: Receive) orElse wrongMessageType

  def gameOver(finalState: GameState) = ({
    case Join(_) => sender ! Failure("cant join, game over")
    case _:GameMove => sender ! Failure("cant make more moves, game over")
  }:Receive) orElse wrongMessageType

  def wrongMessageType: PartialFunction[Any, Unit] = {
    case _ => sender ! Failure("wrong message type")
  }

}
