/** Demonstration of Jswing jswing.guis.FormBuilder

  */

import jswing.widgets.{Frame, FormBuilder}
import javax.swing.JButton

val builder = new FormBuilder()

builder.addRowLabel("Task data")

builder.addTextField("Title", w = 2, columns = 20)
builder.nextRow()

builder.addComboBox("Status", Array("Pending", "Completed", "Failed"))
builder.nextRow()

builder.addCheckBox("Activate remainder", true)
builder.nextRow()
builder.addCheckBox("Repeat")
builder.nextRow()
builder.addComboBox("Priority", Array("High", "Medium", "Low"))
builder.nextRow()

builder.addTextField("Who?")
builder.addTextField("When?")
builder.nextRow()

val buttonSave  = new JButton("Save")
val buttonClean = new JButton("Clean")
val buttonClose = new JButton("Close")

builder.addRowComponents(1, Array(buttonSave, buttonClean, buttonClose))

val frame = new Frame(
  title       = "FormBuilder demonstration",
  size        = (400, 500),
  exitOnClose = true 
)

builder.addToFrame(frame)
frame.pack()
frame.setVisible(true)
