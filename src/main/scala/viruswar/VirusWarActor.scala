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

  def notReady:Receive = {
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user))
    case _:GameMove => sender ! Failure("resource is not ready yet")
  }

  def waitingForUser(first: ActorRef):Receive = {
    case Join(`first`) => first ! Failure("you have joined already")
    case Join(second) =>
      //todo more elegant solution
      val (fstPlayer, sndPlayer) = randomize(_Player('x'), _Player('o'))
      val (current, waiting) = randomize(first, second)

      current ! fstPlayer
      current ! GameState(initialField, New)

      waiting ! sndPlayer
      waiting ! Success()

      become(game(current, waiting, initialField, fstPlayer) )
    case _:GameMove => sender ! Failure("resource is not ready yet 3")
  }

  def game(owner:ActorRef, waiter:ActorRef, current:Field, currentPlayer:_Player):Receive = {
    case move: GameMove =>
      sender match {
        case `owner` =>
          val newState = makeMove(move.move, currentPlayer.player, current)
          newState._2 match {
            case Game =>
              owner ! Success()
              waiter ! newState
              become(game(waiter, owner, newState._1, _Player(opponent(currentPlayer.player))))
            case Tie =>
              owner ! newState
              waiter ! newState
              become(gameOver(GameState(newState._1, newState._2)))
            case Win =>
              owner ! newState
              waiter ! newState.copy(_2 = Lose)
              become(gameOver(GameState(newState._1, newState._2)))
            case WrongMove =>
              owner ! newState
            case _ => throw new Error("inconsistent state")
          }
        case `waiter` => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case Join(_) => sender ! Failure("cant join more users")
  }

  def gameOver(finalState: GameState):Receive = {
    case Join(_) => sender ! Failure("cant join, game over")
    case _:GameMove => sender ! Failure("cant make more moves, game over")
  }

}

trait RandomSwap {
  val rand = new Random()
  def randomize[T](x:(T,T)):(T,T) = if(rand.nextBoolean()) x.swap else x
}