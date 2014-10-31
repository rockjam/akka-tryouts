import akka.actor.{Actor, ActorRef, Props}

object TicTacToe {
  def props() = Props[TicTacToe]
}

class TicTacToe extends Actor with TicTacToeGame {
  import context._

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
        case OWNER =>
          val newState = makeMove(current, move)
          newState.status match {
            case Game =>
              OWNER ! Success()
              WAITER ! newState
              become(working(WAITER, OWNER, newState))
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
