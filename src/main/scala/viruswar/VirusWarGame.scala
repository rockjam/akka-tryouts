package viruswar

trait VirusWarGame {
  private val helper = new VirusWarGameHelpers {}
  def makeMove(move:Move, player:Player, field:Field) = helper.makeMove(move, player, field)
  def opponent(player:Player) = helper.opponent(player)
}
//trait VirusWarGameCore {
//  def makeMove(move:Move, player:Player, field:Field)
//}
//
//class VirusWarGame extends VirusWarGameCore with VirusWarGameHelpers {
//  override def makeMove(move:Move, player:Player, field:Field) =
//    if (canMakeMove(move, player, field)) {
//      val (x,y) = move
//      val cc = changedCell(move, player, field).get
//      val newField = field.updated(y, field(y).updated(x, cc))
//      (newField, score(player, newField))
//    } else {
//      (field, WrongMove)
//    }
//}