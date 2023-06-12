import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.border.EmptyBorder
import scala.swing._
import scala.swing.event._
import java.awt.Font

object UI extends App {
  val rows = Integer.parseInt(args(0))
  val cols = Integer.parseInt(args(1))
  val bombCount = Integer.parseInt(args(2))

  private val fields: Array[Array[Button]] = Array.ofDim[Button](rows, cols)
  private var bombsLeft = bombCount
  private var score = 0

  private val bombsLeftLabel: Label = new Label("Cats left: " + bombsLeft.toString)
  private val scoreLabel: Label = new Label("Score: " + score.toString)
  bombsLeftLabel.font = new Font(bombsLeftLabel.font.getName, Font.BOLD, 20)
  scoreLabel.font = new Font(scoreLabel.font.getName, Font.PLAIN, 20)

  private val gameLogic = new GameLogic(rows, cols, bombCount)

  createUI()

  def createUI(): Unit = {
    val mainPanel = new GridPanel(rows, cols) {
      for (row <- 0 until rows; col <- 0 until cols) {
        val button = new Button()
        button.background = Color.decode("#FDE3A8")
        button.foreground = Color.decode("#000061")
        button.font = new Font(button.font.getName, Font.BOLD, 20)
        button.preferredSize = new Dimension(50, 50)
        button.listenTo(button.mouse.clicks)
        button.reactions += {
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
    if (fields(row)(col).text != "X") {
      if (gameLogic.bombLocations.contains((row, col))) {
        revealBombs()
        resetGame()
        showGameOverMessage()
      } else {
        revealCell(row, col)
        if (gameLogic.revealed.size == rows * cols - bombCount) {
          showVictoryMessage()
        }
      }
    }
  }

  def revealCell(row: Int, col: Int): Unit = {
    if (!gameLogic.revealed.contains((row, col))) {
      gameLogic.addRevealCell(row, col)
      val countedBombs = gameLogic.countNeighborBombs(row, col)

      if (fields(row)(col).text != "X") {
        fields(row)(col).text = countedBombs.toString
        fields(row)(col).enabled = false
      }

      if (countedBombs == 0) {
        for (i <- -1 to 1; j <- -1 to 1) {
          val newRow = row + i
          val newCol = col + j
          if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
            revealCell(newRow, newCol)
          }
        }
      }

      if (fields(row)(col).text != "X") {
        score += Integer.parseInt(fields(row)(col).text)
        scoreLabel.text = "Score: " + score.toString
      }
    }
  }

  def revealBombs(): Unit = {
    for ((row, col) <- gameLogic.bombLocations) {
      fields(row)(col).text = "*"
      fields(row)(col).enabled = false
    }
  }

  def resetGame(): Unit = {
    gameLogic.bombLocations = Set.empty
    gameLogic.revealed = Set.empty
    gameLogic.generateBombLocations()

    for {
      row <- 0 until rows
      col <- 0 until cols
    } {
      fields(row)(col).text = ""
      fields(row)(col).enabled = true
    }

    bombsLeft = bombCount
    bombsLeftLabel.text = "Cats left: " + bombsLeft.toString
    score = 0
    scoreLabel.text = "Score: " + score.toString
  }

  def showVictoryMessage(): Unit = {
    Dialog.showMessage(null, "Congratulations! You won the game.", "Victory", Dialog.Message.Info)
  }

  def showGameOverMessage(): Unit = {
    Dialog.showMessage(null, "Game Over! You hit a cat.", "Game Over", Dialog.Message.Error)
  }
}


