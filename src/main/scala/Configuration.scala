import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.border.EmptyBorder
import scala.swing.event.ButtonClicked
import scala.swing.MenuBar.NoMenuBar.{listenTo, reactions}
import scala.swing._

object Configuration extends App {
  createUI()

  def createUI(): Unit = {
    val inputFieldRow = new TextField(10)
    val inputFieldCol = new TextField(10)
    val inputFieldBomb = new TextField(10)
    val submitButton = new Button("Play!")

    val mainPanel = new FlowPanel {
      contents += new BoxPanel(Orientation.Vertical) {
        border = new EmptyBorder(0, 5, 10, 5)
        contents += new BoxPanel(Orientation.Horizontal) {
          border = new EmptyBorder(10, 0, 10, 0)
          contents += new Label("Rows quantity:")
          contents += inputFieldRow
          contents += new Label("Columns quantity:")
          contents += inputFieldCol
          contents += new Label("Cats quantity:")
          contents += inputFieldBomb
        }
        submitButton.border = new EmptyBorder(10, 20, 10, 20)
        submitButton.background = Color.ORANGE
        contents += submitButton
      }
    }

    def processInput(): Unit = {
      val rows: String = inputFieldRow.text
      val cols: String = inputFieldCol.text
      val bombs: String = inputFieldBomb.text
      if (rows.forall(_.isDigit) && cols.forall(_.isDigit) && bombs.forall(_.isDigit)
        && !rows.equals("") && !cols.equals("") && !bombs.equals("")) {
        UI.main(Array(rows, cols, bombs))

      } else {
        Dialog.showMessage(null, "Invalid arguments: required numbers", "Configuration Error", Dialog.Message.Error)
      }
    }

    listenTo(submitButton)
    reactions += {
      case ButtonClicked(`submitButton`) => processInput()
    }

    val topFrame = new MainFrame {
      title = "Cats Saper configuration"
      val iconPath = "Bomb.jpg"
      val icon = new ImageIcon(getClass.getResource(iconPath))
      iconImage = icon.getImage
      contents = new BoxPanel(Orientation.Vertical) {
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label {
            icon = new ImageIcon(getClass().getResource("Flag.jpg"))
            maximumSize = new Dimension(500, 500)
            preferredSize = new Dimension(500, 500)
          }
        }
        contents += mainPanel
      }
      pack()
      centerOnScreen()
    }
    topFrame.visible = true
  }
}

