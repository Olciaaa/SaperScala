class GameLogic(rows: Int, cols: Int, bombCount: Int) {
  var bombsLeft: Int = bombCount
  var bombLocations: Set[(Int, Int)] = Set.empty
  var revealed: Set[(Int, Int)] = Set.empty

  generateBombLocations()

  def countNeighborBombs(row: Int, col: Int): Int = {
    val neighborPositions = for {
      i <- -1 to 1
      j <- -1 to 1
      if !(i == 0 && j == 0)
      newRow = row + i
      newCol = col + j
      if newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
    } yield (newRow, newCol)
    neighborPositions.count(pos => bombLocations.contains(pos))
  }

  def generateBombLocations(): Unit = {
    val allPositions = for {
      row <- 0 until rows
      col <- 0 until cols
    } yield (row, col)
    bombLocations = scala.util.Random.shuffle(allPositions).take(bombCount).toSet
  }

  def addRevealCell(row: Int, col: Int): Unit = {
    revealed += ((row, col))
  }
}

