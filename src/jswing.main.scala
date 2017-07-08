package jswing.main


private class GUIBuilder(xmlBuilder: String => Unit) {
  private val entry  = new javax.swing.JTextArea()
  private val scroll = new javax.swing.JScrollPane(entry)
  private val frame = new javax.swing.JFrame("Jswing XML Builder")
  private val dialog = new jswing.Dialog.FileChooser()


  init()


  def readFile(file: String) = scala.io.Source.fromFile(file).mkString

  private def init(){
    val panel = new javax.swing.JPanel()
    val buttonRun = new javax.swing.JButton("Build GUI")
    val buttonOpen = new javax.swing.JButton("Open XML")

    panel.add(buttonRun)
    panel.add(buttonOpen)

    frame.setLayout(new java.awt.BorderLayout())
    frame.add(scroll, java.awt.BorderLayout.CENTER)
    frame.add(panel,  java.awt.BorderLayout.SOUTH)
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
    frame.setSize(600, 400)

    jswing.Event.onButtonClick(buttonRun){
      xmlBuilder(entry.getText())
    }

    jswing.Event.onButtonClick(buttonOpen){
      dialog.run() map(this.readFile) foreach entry.setText
    }
  }

  def setVisible(flag: Boolean) = frame.setVisible(flag)
}


object Main{

  def main(args: Array[String]){
    // makeFromFile(args(0))
    val gui = new GUIBuilder(jswing.layout.Builder.makeFromString)

    args match {      
      case Array("-layout-file", file) => jswing.layout.Builder.makeFromFile(file)
      case Array("-layout-gui")        => gui.setVisible(true)
      case _                           => println("Error: invalid option.")
    }  
          
  }

}
