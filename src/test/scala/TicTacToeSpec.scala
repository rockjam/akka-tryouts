import org.scalatest.FlatSpec
import tictactoe.{Game, Win, Tie, TicTacToeGame}

class TicTacToeSpec extends FlatSpec with TicTacToeGame {

  def field(st: String) = st.stripMargin.replaceAll( """\n""", "")

  "Score on empty field" should "return game" in {
    assertResult(Game) {
      score(field(
        """---
          |---
          |---"""))
    }
  }

  "Score on non empty field" should "return game" in {
    assertResult(Game) {
      score(field(
        """x--
          |o--
          |---"""))
    }

    assertResult(Game) {
      score(field(
        """xox
          |o--
          |---"""))
    }

    assertResult(Game) {
      score(field(
        """-ox
          |o--
          |-o-"""))
    }

  }

  "Score on full field" should "return tie" in {
    assertResult(Tie) {
      score(field(
        """xox
          |oxo
          |oxo"""))
    }

    assertResult(Tie) {
      score(field(
        """xxo
          |oxx
          |xoo"""))
    }

  }

  "Score on winning field" should "return Win" in {
    assertResult(Win) {
      score(field(
        """xxx
          |o-o
          |-xo"""))
    }

    assertResult(Win) {
      score(field(
        """x--
          |-x-
          |oox"""))
    }

  }

  "Score on full winning field" should "be Win" in  {
    assertResult(Win) {
      score(field(
      """xox
        |oox
        |oxx"""
      ))
    }
  }

}
