import jswing.widgets.ListBox
import jswing.panel.BorderPanel
import jswing.{Event, Dialog}
import javax.swing._

val dirch = new Dialog.DirChooser().withHome()

val frame = new JFrame("Listbox test")
frame.setSize(500, 400)
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

val buttonOpen = new JButton("Open")
val buttonClear = new JButton("Clear")

val display = new JLabel("")
val lbox = new ListBox[String]()

val panel = new BorderPanel()
panel.addTopItems(buttonOpen, buttonClear)
panel.addCenterScroll(lbox)
panel.addBottomItems(display)


def showFiles(path: String){
  lbox.clear()
  display.setText("")
  jswing.JUtils.invokeLater{
    val files = new java.io.File(path).listFiles()
    files foreach {f => lbox.addItem(f.getName(), f.getAbsolutePath())}
  }
}

Event.onButtonClick(buttonOpen){
  dirch.run() foreach showFiles
}

Event.onButtonClick(buttonClear){
  lbox.clear()
  display.setText("")
}

lbox.onSelect{
  println("Selected label = " + lbox.getSelectedItemLabel())
  println("Selected value = " + lbox.getSelectedItemValue())
  lbox.getSelectedItemValue foreach display.setText
}


frame.setContentPane(panel)
frame.setVisible(true)


