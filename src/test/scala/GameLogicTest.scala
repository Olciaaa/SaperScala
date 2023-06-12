import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GameLogicTest extends AnyFunSuite with Matchers {
  test("countNeighborBombs should return the correct number of bombs in neighboring cells") {
    val game = new GameLogic(5, 5, 5)
    game.bombLocations = Set((1, 1), (2, 2), (3, 3), (4, 4), (0, 4))

    game.countNeighborBombs(2, 2) shouldBe 2
    game.countNeighborBombs(0, 0) shouldBe 1
    game.countNeighborBombs(4, 4) shouldBe 1
    game.countNeighborBombs(0, 4) shouldBe 0
    game.countNeighborBombs(3, 0) shouldBe 0
  }

  test("generateBombLocations should generate the specified number of bomb locations") {
    val game = new GameLogic(10, 10, 20)
    game.generateBombLocations()

    game.bombLocations.size shouldBe 20

    val game1 = new GameLogic(1, 1, 0)
    game1.generateBombLocations()

    game1.bombLocations.size shouldBe 0
  }

  test("revealed should be updated correctly when cells are revealed") {
    val game = new GameLogic(3, 3, 1)
    game.bombLocations = Set((0, 0))

    game.revealed shouldBe empty

    game.addRevealCell(1, 1)
    game.revealed should contain only ((1, 1))

    game.addRevealCell(0, 0)
    game.revealed should contain only((1, 1), (0, 0))
  }
}
