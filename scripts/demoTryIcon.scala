import jswing.Event
import jswing.widgets.{Frame, TrayIcon}

val frame = new Frame(
  title = "Try Icon test",
  size  = (500, 400),
  layout = new java.awt.FlowLayout()
)

val display = new javax.swing.JLabel("Hello world")
frame.add(display)

val trayIcon = new TrayIcon("")

trayIcon.onClick{
  if (frame.isVisible)
  {
    frame.setVisible(false)   
  }
  else
  {
    frame.setVisible(true)
  }
}


trayIcon.addMenuItem("Item 1"){
  display.setText("Item 1 test")
  trayIcon.showInfo("Information", "Download finished. Ok.")
}

trayIcon.addMenuItem("Item 2"){
  display.setText("Item 2 test")
  trayIcon.showError("Error", "Error: Connection failure")
}

trayIcon.addMenuItem("Item 3"){
  display.setText("Item 3 test")
  trayIcon.showInfo("Info test", "Error: Item 3 test.")
}


trayIcon.addMenuItem("Item 4"){
  display.setText("Item 4 test")
}

trayIcon.addMenuItem("Close"){ System.exit(0)}


frame.setVisible(true)
trayIcon.show()



