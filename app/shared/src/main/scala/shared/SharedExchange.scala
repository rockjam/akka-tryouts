package shared

sealed trait SharedExchange
sealed trait GameRepr

case object Start extends SharedExchange

case class Coordinates(x:Int, y:Int) extends SharedExchange

case class VirusWarGameState(field:Vector[Vector[Char]], status:GameMoment) extends SharedExchange with GameRepr

case class GameMove(x:Int, y:Int) extends SharedExchange

case class Player(ch:Char) extends SharedExchange

case class GameState(field: String, status: GameMoment) extends SharedExchange with GameRepr

case class Success(status: String = "success") extends SharedExchange

case class Failure(message: String, status: String = "failure") extends SharedExchange

case object Opened extends SharedExchange

case object Closed extends SharedExchange


sealed trait GameMoment extends SharedExchange

case object Tie extends GameMoment
case object Win extends GameMoment
case object Lose extends GameMoment
case object Game extends GameMoment
case object New extends GameMoment
case object WrongMove extends GameMoment

case object YourTurn extends GameMoment

case class GameView(id:String, gameState:GameRepr, viewState:ViewState, message:String)

sealed trait ViewState
case object InProgress extends ViewState
case object GameOver extends ViewState
