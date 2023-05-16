import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.border.EmptyBorder
import scala.swing._
import scala.swing.event._
import java.awt.Font


object Main extends App {
  val rows = Integer.parseInt(args(0))
  val cols = Integer.parseInt(args(1))
  val bombCount = Integer.parseInt(args(2))
  var bombsLeft = bombCount
  var score = 0

  val fields: Array[Array[Button]] = Array.ofDim[Button](rows, cols)
  var bombLocations: Set[(Int, Int)] = Set.empty
  var revealed: Set[(Int, Int)] = Set.empty

  val bombsLeftLabel: Label = new Label("Cats left: " + bombsLeft.toString)
  bombsLeftLabel.font = new Font(bombsLeftLabel.font.getName, Font.BOLD, 20)
  val scoreLabel: Label = new Label("Score: " + score.toString)
  scoreLabel.font = new Font(scoreLabel.font.getName, Font.PLAIN, 20)

  generateBombLocations()
  createUI()

  def createUI(): Unit = {
    val mainPanel = new GridPanel(rows, cols) {
      for (row <- 0 until rows; col <- 0 until cols) {
        val button = new Button()
        button.background = Color.decode("#FDE3A8")
        button.foreground = Color.decode("#000061")
        button.font = new Font(button.font.getName, Font.BOLD, 20)
        button.preferredSize = new Dimension(50, 50)
        listenTo(button.mouse.clicks)
        reactions += {
          case evt@MouseClicked(`button`, _, _, _, _) =>
            val which = evt.peer.getButton
            if (which == 1) {
              handleButtonClick(row, col)
            } else if (which == 3) {
              handleButtonClickRight(row, col)
            }
        }
        contents += button
        fields(row)(col) = button
      }
    }


    val topFrame = new MainFrame {
      title = "CAT BOOM!"
      val iconPath = "Bomb.jpg"
      val icon = new ImageIcon(getClass.getResource(iconPath))
      iconImage = icon.getImage

      contents = new BoxPanel(Orientation.Vertical) {
        contents += new BoxPanel(Orientation.Horizontal) {
          border = new EmptyBorder(10, 0, 10, 0)
          bombsLeftLabel.border = new EmptyBorder(0, 0, 0, 10)
          contents += bombsLeftLabel
          contents += scoreLabel
        }
        contents += mainPanel
      }
      pack()
      centerOnScreen()
    }
    topFrame.visible = true
  }

  def handleButtonClickRight(row: Int, col: Int): Unit = {
    if (fields(row)(col).text.equals("X")) {
      fields(row)(col).text = ""
      bombsLeft += 1
    } else if (bombsLeft > 0) {
      fields(row)(col).text = "X"
      bombsLeft -= 1
    }

    bombsLeftLabel.text = "Cats left: " + bombsLeft.toString
  }

  def handleButtonClick(row: Int, col: Int): Unit = {
    if (bombLocations.contains((row, col))) {
      revealBombs()
      showGameOverMessage()
    } else {
      revealCell(row, col)
      if (revealed.size == rows * cols - bombCount) {
        showVictoryMessage()
      }
    }
  }

  def revealCell(row: Int, col: Int): Unit = {
    if (!revealed.contains((row, col))) {
      revealed += ((row, col))
      fields(row)(col).text = countNeighborBombs(row, col).toString
      fields(row)(col).enabled = false
      if (countNeighborBombs(row, col) == 0) {
        for (i <- -1 to 1; j <- -1 to 1) {
          val newRow = row + i
          val newCol = col + j
          if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
            revealCell(newRow, newCol)
          }
        }
      }
      score += Integer.parseInt(fields(row)(col).text)
      scoreLabel.text = "Score: " + score.toString
    }
  }

  def revealBombs(): Unit = {
    for ((row, col) <- bombLocations) {
      fields(row)(col).text = "*"
      fields(row)(col).enabled = false
    }
  }

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

  def showGameOverMessage(): Unit = {
    Dialog.showMessage(null, "Game Over! You hit a cat.", "Game Over", Dialog.Message.Error)
    resetGame()
  }

  def resetGame(): Unit = {
    bombLocations = Set.empty
    revealed = Set.empty
    generateBombLocations()

    for {
      row <- 0 until rows
      col <- 0 until cols
    } {
      fields(row)(col).text = ""
      fields(row)(col).enabled = true
    }
  }

  def showVictoryMessage(): Unit = {
    Dialog.showMessage(null, "Congratulations! You won the game.", "Victory", Dialog.Message.Info)
  }

}

