package viruswar

import akka.actor.{ActorRef, Props, Actor}
import shared._

import scala.util.Random

object VirusWarActor {
  def props() = Props[VirusWarActor]
}

class VirusWarActor extends Actor with VirusWarGame with RandomSwap {
  import context._
  import VirusWarGameHelpers._

  def receive = notReady

  val maxTurns = 3

  def notReady:Receive = {
    case Join(user) =>
      user ! Success()//should we use Success at all?
      become(waitingForUser(user))
    case _:GameMove => sender ! Failure("resource is not ready yet")
  }

  def waitingForUser(first: ActorRef):Receive = {
    case Join(`first`) => first ! Failure("you have joined already")
    case Join(second) =>
      //todo more elegant solution
      val (fstPlayer, sndPlayer) = randomize(Player('x'), Player('o'))
      val (owner, waiter) = randomize(first, second)

      val initialState = VirusWarGameState(initialField, New)
      owner ! fstPlayer
      owner ! initialState

      waiter ! sndPlayer
      waiter ! Success()
      system.eventStream.publish(GameView(self.path.toString, initialState, InProgress, "Game began"))

      become(game(owner, waiter, initialField, fstPlayer, maxTurns) )
    case _:GameMove => sender ! Failure("resource is not ready yet 3")
  }

  def game(owner:ActorRef, waiter:ActorRef, current:Field, currentPlayer:Player, turnsLeft:Int):Receive = {
    case move: GameMove =>
      sender match {
        case `owner` =>
          val newState = makeMove(move, currentPlayer, current)
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
              if (turnsLeft == 1) {
                owner ! newState
                waiter ! newState.copy(status = YourTurn)
                become(game(waiter, owner, newState.field, opponent(currentPlayer), maxTurns))
              } else {
                owner ! newState.copy(status = YourTurn)
                waiter ! newState
                // can possibly send status = Game when you should wait, and status = YourTurn
                // when you should make turn. Or it is possible to pass turns with message.
                become(game(owner, waiter, newState.field, currentPlayer, turnsLeft - 1))
              }
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
  }

  def gameOver(finalState: VirusWarGameState):Receive = {
    case Join(_) => sender ! Failure("cant join, game over")
    case _:GameMove => sender ! Failure("cant make more moves, game over")
  }
}

trait RandomSwap {
  val r = new Random()
  def randomize[T](x:T, y:T):(T,T) = if (r.nextBoolean()) (x,y) else (y,x)
}