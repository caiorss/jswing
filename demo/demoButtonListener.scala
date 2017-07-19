import jswing.Event
import jswing.Dialog
import jswing.widgets.Frame
import javax.swing.JButton

val frame = new Frame(
  title       ="JButton Event Listener",
  size        =(300, 400),
  layout      = new java.awt.FlowLayout(),
  exitOnClose = true 
)

val button1 = new JButton("Button 1")
button1.setActionCommand("bt1")

val button2 = new JButton("Button 2")
button2.setActionCommand("bt2")

val button3 = new JButton("Button 3")

val listener = Event.makeActionListener{ cmd =>
  cmd match {
    case "bt1"
        => Dialog.showAlert("Alert", "You clicked on button 1")

    case "bt2"
        => Dialog.showAlert("Alert", "You clicked on button 2")

    case "Button 3"
        => Dialog.showAlert("Alert", "You clicked on button 3")
  }
}


button1.addActionListener(listener)
button2.addActionListener(listener)
button3.addActionListener(listener)

frame.add(button1)
frame.add(button2)
frame.add(button3)
frame.setVisible(true)

jswing.JUtils.saveScreenShotArgs(frame, "images/demoButtonListener.png")(args)



