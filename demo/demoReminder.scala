import jswing.Event
import jswing.widgets.{Frame, Button}
import jswing.panel.FormBuilder
import javax.swing.{JSpinner, JTextField, JList, JButton, DefaultListModel}


val tray = new jswing.widgets.TrayIcon("", "Timer Reminder Application")
tray.addMenuItem("Close"){ System.exit(0) }
tray.onClick{ frame.setVisible(!frame.isVisible) }
tray.show()

val form = new FormBuilder()
val hourTf = new JSpinner()
//hourTf.setColumns(10)

val minTf  = new JSpinner()
//minTf.setColumns(10)

val secTf  = new JSpinner()
//secTf.setColumns(10)


form.add("Hour", hourTf)
form.nextRow()
form.add("Minutes", minTf)
form.nextRow()
form.add("Seconds", secTf)
form.nextRow()
val reminderTf = form.addTextField("Reminder", columns = 20)
form.nextRow()

val btnRun    = new Button("Run")
val btnClear  = new Button("Clear")
val btnCancel = new Button("Cancel")
val btnExit   = new Button("Exit")
form.addRowComponents(Array(btnRun, btnClear, btnCancel, btnExit))


// Get delay in Milliseconds
def getDelayMs() = {
  val h = hourTf.getValue().asInstanceOf[Int]
  val m = minTf.getValue().asInstanceOf[Int]
  val s = secTf.getValue().asInstanceOf[Int]
  s * 1000 +  m * 1000 * 60 + h * 1000 * 60 * 60 
}


def cleanGUI() = {
  hourTf.setValue(0)
  minTf.setValue(0)
  secTf.setValue(0)
  reminderTf.setText("")
}

def runReminder() = {
  val text = reminderTf.getText()
  val time = getDelayMs()
  jswing.JUtils.runDelay(time) { 
    jswing.Dialog.showInfo("Reminder", text)
    cleanGUI()
    btnRun.setEnabled(true)
    frame.setVisible(true)
  }
}


var dispose: () => Unit = null

btnClear.onClick{ cleanGUI() }

btnRun.onClick{
  if (reminderTf.getText() != ""){
    dispose = runReminder()
    btnRun.setEnabled(false)
  }
}


btnCancel.onClick{
  if (dispose != null) dispose()
  btnRun.setEnabled(true)
}

btnExit.onClick{ System.exit(0) }

val frame = new Frame(
  title       = "Reminder app.",
 // size        = (600, 500),
  pane        = form.getPanel(),
  pack        = true,
  visible     = true
  //exitOnClose = true
)


