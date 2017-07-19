import jswing.builder.Builder
import jswing.Event

val gui      = Builder.makeFromFile("scripts/gui-borderlayout1.xml")
val frame    = Builder.getJFrame(gui, "frame1")
val btnOpen  = Builder.getJButton(gui, "btnOpen")
val btnClear = Builder.getJButton(gui, "btnClear")
val tarea    = Builder.getJTextArea(gui, "tarea")

val fchooser = new jswing.Dialog.FileChooser().withHome()

def readFile(file: String) =
  scala.io.Source.fromFile(file).mkString

Event.onButtonClick(btnClear){ tarea.setText("") }

Event.onButtonClick(btnOpen){
  fchooser.run() foreach { file => tarea.setText(readFile(file))}
  jswing.JUtils.saveScreenShotArgs(frame, "images/demoGUILayout-editor.png")(args)
}



