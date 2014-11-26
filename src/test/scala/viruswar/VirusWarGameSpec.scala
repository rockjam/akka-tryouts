package viruswar

import org.scalatest.{Matchers, FlatSpec}
import VirusWarGame._

class VirusWarGameSpec extends FlatSpec with Matchers with VirusWarGame {

  def emptyField = (1 to 100).foldLeft("")((a,f) => a + "-")

  "valid neighbor cells" should "be neighbors of this cell in range between 0 and 9 inclusive" in {
    validNeighborCells((0, 0)) should (
      have size 3
      and contain allOf((1, 0), (1, 1), (0, 1))
      and not contain(0, 0))

    validNeighborCells((1,0)) should (
      have size 5
      and contain allOf((0,0), (2,0), (2,1), (1,1), (0,1))
      and not contain (1,0))

    validNeighborCells((1,1)) should (
      have size 8
      and contain allOf((0,0), (1,0), (2,0), (2,1), (2,2), (1,2), (0,2), (0,1))
      and not contain (1,1))

    validNeighborCells((9,9)) should (
      have size 3
      and contain allOf((9,8), (8,9), (8,8))
      and not contain (9,9))
  }

  "valid cell" should "be cell with x and y in range 0 to 9 inclusive" in {
    validCell(0,0) should be(true)
    validCell(3,4) should be(true)

    validCell(-1,0) should be(false)
    validCell(10,0) should be(false)
  }

  "first turn" should "determine first turn of player on given field" in {
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
      |----------""") should be (true)

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
      |----------""") should be (false)

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
      |----------""") should be (true)

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
      |---------o""") should be (true)

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
      |---------o""") should be (false)

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
      |---------o""") should be (false)

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
      |---------o""") should be (false)
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
        |---------o""") should be (false)

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

  "can make move" should "allow to make right move" in {
    canMakeMove((0,0), 'x',
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

    canMakeMove((3,1), 'x',
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""") should be(false)

    canMakeMove((9,9), 'x',
      """----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------
        |----------""") should be(false)

    canMakeMove((0,0), 'x',
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

  }

}
