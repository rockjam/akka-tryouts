package viruswar

import org.scalatest.{Matchers, FlatSpec}
import VirusWarGameHelpers._
import shared.{Win, WrongMove, Game}

class VirusWarGameHelpersSpec extends FlatSpec with Matchers with VirusWarGameHelpers {

  "valid neighbor cells" should "be neighbors of this cell in range between 0 and 9 inclusive" in {
    validNeighborCells((0, 0)) should (
      have size 3
        and contain allOf((1, 0), (1, 1), (0, 1))
        and not contain(0, 0))

    validNeighborCells((1, 0)) should (
      have size 5
        and contain allOf((0, 0), (2, 0), (2, 1), (1, 1), (0, 1))
        and not contain(1, 0))

    validNeighborCells((1, 1)) should (
      have size 8
        and contain allOf((0, 0), (1, 0), (2, 0), (2, 1), (2, 2), (1, 2), (0, 2), (0, 1))
        and not contain(1, 1))

    validNeighborCells((9, 9)) should (
      have size 3
        and contain allOf((9, 8), (8, 9), (8, 8))
        and not contain(9, 9))
  }

  "valid cell" should "be cell with x and y in range 0 to 9 inclusive" in {
    validCell(0, 0) should be(true)
    validCell(3, 4) should be(true)

    validCell(-1, 0) should be(false)
    validCell(10, 0) should be(false)
  }

  "first turn" should "determine if it is first turn of player on given field" in {
    isFirstTurn('x',
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""") should be(true)

    isFirstTurn('x',
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""") should be(false)

    isFirstTurn('o',
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""") should be(true)

    isFirstTurn('x',
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(true)

    isFirstTurn('o',
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(false)

    isFirstTurn('x',
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(false)

    isFirstTurn('o',
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(false)
  }

  "live neighbors" should
    """be cells with same player symbol or
       cells captured by this player, that have live neighbors
    """ in {
    //T stands for "This cell"

    haveLiveNeighbor((4, 4), 'x',
      """x---------
        |----------
        |----------
        |----------
        |----T-----
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(false)

    haveLiveNeighbor((3, 2), 'x',
      """x---------
        |-xx-------
        |---T------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(true)

    haveLiveNeighbor((2, 3), 'x',
      """x---------
        |-xx-------
        |----------
        |--T-------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(false)

    haveLiveNeighbor((5, 5), 'o',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----T----
        |-----oo---
        |-------o--
        |--------o-
        |---------o""") should be(true)

    haveLiveNeighbor((5, 5), 'x',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----T----
        |-----oo---
        |-------o--
        |--------o-
        |---------o""") should be(true)

    haveLiveNeighbor((6, 6), 'x',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |-----oT---
        |-------o--
        |--------o-
        |---------o""") should be(true)

    haveLiveNeighbor((5, 6), 'x',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----------
        |-----X----
        |------T---
        |-------o--
        |--------o-
        |---------o""") should be(false)

  }

  "can make move" should
    """allow to make right move and
       violate to make wrong move
    """ in {
    val f0 =
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------"""

    canMakeMove((0, 0), 'x', f0) should be(true)
    canMakeMove((3, 1), 'x', f0) should be(false)
    canMakeMove((9, 9), 'x', f0) should be(false)

    val f1 =
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------"""

    canMakeMove((0, 0), 'x', f1) should be(false)
    canMakeMove((9, 9), 'o', f1) should be(true)
    canMakeMove((3, 2), 'o', f1) should be(false)

    canMakeMove((1, 1), 'x',
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""") should be(true)

    canMakeMove((5, 5), 'x',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----o----
        |-----oo---
        |-------o--
        |--------o-
        |---------o""") should be(true)

    val f2 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |-----oo---
        |-------o--
        |--------o-
        |---------o"""

    canMakeMove((5, 6), 'x', f2) should be(true)
    canMakeMove((5, 5), 'x', f2) should be(false)

    canMakeMove((4, 6), 'x',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |-----Xo---
        |-------o--
        |--------o-
        |---------o""") should be(true)

    val f3 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----xXo---
        |-------o--
        |--------o-
        |---------o"""

    canMakeMove((5, 7), 'o', f3) should be(true)
    canMakeMove((5, 5), 'o', f3) should be(false)

    canMakeMove((4, 6), 'o',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----xXo---
        |-----o-o--
        |--------o-
        |---------o""") should be(true)

    canMakeMove((4, 5), 'o',
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----OXo---
        |-----o-o--
        |--------o-
        |---------o""") should be(true)

    canMakeMove((2,7), 'x',
      """xX--------
        |-OXXXX----
        |-OOOOX----
        |-xOOXX----
        |-xOXO-----
        |-xOOXX----
        |-XOOXXX---
        |XXoOxOXX--
        |-X-oX--XX-
        |--Xo-oo--X""") should be (true)

  }

  "makeMove" should "return updated field for correct moves" in {
    val f1 =
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""": Field

    val f2 =
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""": Field

    val f3 =
      """x---------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |---------o""": Field

    makeMove((0, 0), 'x', f1) should be(f2, Game)
    makeMove((0, 0), 'x', f2) should be(f2, WrongMove)
    makeMove((9, 9), 'o', f2) should be(f3, Game)

    val f4 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----o----
        |-----oo---
        |-------o--
        |--------o-
        |---------o""": Field

    val f5 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |-----oo---
        |-------o--
        |--------o-
        |---------o""": Field

    val f6 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |-----Xo---
        |-------o--
        |--------o-
        |---------o""": Field

    val f7 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----xXo---
        |-------o--
        |--------o-
        |---------o""":Field

    makeMove((5, 5), 'x', f4) should be(f5, Game)
    makeMove((5, 6), 'x', f5) should be(f6, Game)
    makeMove((5, 6), 'x', f6) should be(f6, WrongMove)
    makeMove((4, 6), 'x', f6) should be(f7, Game)

    val f8 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----xXo---
        |-----o-o--
        |--------o-
        |---------o""":Field

    val f9 =
      """x---------
        |-xx-------
        |---x------
        |----x-----
        |----x-----
        |-----X----
        |----OXo---
        |-----o-o--
        |--------o-
        |---------o""":Field

    makeMove((5, 7), 'o', f7) should be(f8, Game)
    makeMove((5, 5), 'o', f7) should be(f7, WrongMove)
    makeMove((4, 6), 'o', f8) should be(f9, Game)

    val f10 =
      """x---------
        |-x----x---
        |--x--x----
        |--x-x-----
        |---O------
        |----O-----
        |-----o----
        |------oo--
        |--------o-
        |---------o""": Field

    val f11 =
      """x---------
        |-x----x---
        |--x--x----
        |--x-x-----
        |---O------
        |---oO-----
        |-----o----
        |------oo--
        |--------o-
        |---------o""":Field

    //this case caused to stack overflow
    makeMove((3,5), 'o',f10) should be (f11, Game)
  }

  "score" must "tell whether we should continue game, or some player won" in {
    score('x',
      """x---------
        |xO--------
        |--O-------
        |--OO------
        |---OOX----
        |---OOXX---
        |OOOOOOOXX-
        |OxOOOOXXX-
        |XXXXOOXXXX
        |XXXXXXXXXX""") should be (Win)
  }


}
