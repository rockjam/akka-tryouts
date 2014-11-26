package shared

sealed trait GameMoment
case object Tie extends GameMoment
case object Win extends GameMoment
case object Lose extends GameMoment
case object Game extends GameMoment
case object New extends GameMoment
case object WrongMove extends GameMoment