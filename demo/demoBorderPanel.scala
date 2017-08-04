import jswing.{Event, Dialog}
import jswing.widgets.Frame
import jswing.panel.BorderPanel
import javax.swing._

val fdialog = new jswing.Dialog.FileChooser().withHome()

val panel = new BorderPanel()

val topPanel    = new JPanel()
val openButton  = new JButton("Open")
val clearButton = new JButton("Clear")
val exitButton  = new JButton("Exit")

topPanel.add(openButton)
topPanel.add(clearButton)
topPanel.add(exitButton)

panel.addTop(topPanel)

val tarea = new JTextArea()
panel.addCenterScroll(tarea)

val statusBar = new JLabel("Status bar")
panel.addBottom(statusBar)

val frame = new Frame(
  title       = "BorderLayout test",
  size        = (500, 400),
  // resizable   = false,
  pane        = panel,
  visible     = true,
  exitOnClose = true
)

def readFile(file: String) =
  scala.io.Source.fromFile(file).mkString


Event.onButtonClick(openButton){
  val file = fdialog.run()
  file map readFile foreach tarea.setText
  file foreach {f => statusBar.setText("Editing file: " + f)}
}

Event.onButtonClick(clearButton){
  tarea.setText("")
  statusBar.setText("Editing file:")
}

Event.onButtonClick(exitButton){
  System.exit(0)
}

