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


private class GUIRunExamples(path: String) {
  val frame  = new javax.swing.JFrame("Jswing Examples")
  val model  = new javax.swing.DefaultListModel[String]()
  val jlist  = new javax.swing.JList(model)

  val tarea = new javax.swing.JTextArea()

  init()

  def readFile(file: String) = scala.io.Source.fromFile(file).mkString

  def getFiles(path: String, extension: String) = {
    new java.io.File(path)
      .listFiles()
      .map(_.toString)
      .filter(_.endsWith(extension))
  }

  def runProcess(args: String*) = {
    Runtime.getRuntime().exec(args toArray)
  }

  def init(){

    val top = new javax.swing.JPanel()
    val buttonRun = new javax.swing.JButton("Run")
    val buttonUpdate = new javax.swing.JButton("Update")

    top.setLayout(new java.awt.FlowLayout())
    top.add(buttonRun)
    top.add(buttonUpdate)

    frame.setLayout(new java.awt.BorderLayout())
    frame.add(top, java.awt.BorderLayout.NORTH)
    frame.add(new javax.swing.JScrollPane(jlist), java.awt.BorderLayout.WEST)
    frame.add(new javax.swing.JScrollPane(tarea), java.awt.BorderLayout.CENTER)

    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
    frame.setSize(795, 614)

    tarea.setEditable(false)

    jswing.Event.onListSelect(jlist){
      Option(jlist.getSelectedValue()) foreach {
        file => val content = this.readFile(file)
        tarea.setText(content)
      }
    }

    jswing.Event.onButtonClick(buttonRun){
      val script = jlist.getSelectedValue()
      this.runProcess("scala", "-cp", "bin/jswing.jar", "-save", script)
    }

    jswing.Event.onButtonClick(buttonUpdate){ this.update() }

    this.update()
    jlist.setSelectedIndex(0)
  }


  def update() = jswing.JUtils.invokeLater {
    model.clear()
    getFiles(path, ".scala") foreach model.addElement
  }

  def show() = frame.setVisible(true)

  def hide() = frame.setVisible(false)

  def setVisible(flag: Boolean) = frame.setVisible(flag)
}



object Main{

  def main(args: Array[String]){
    // makeFromFile(args(0))



    args match {      
      case Array("-layout-file", file)
          => jswing.builder.Builder.makeFromFileShow(file)


      case Array("-layout-gui")
          => {
            val gui = new GUIBuilder(jswing.builder.Builder.makeFromString)
            gui.setVisible(true)
          }

      case Array("-examples")
          => {
            val gui = new GUIRunExamples("./scripts")
            gui.show()
          }

      case _
          => println("Error: invalid option.")
    }  
          
  }

}
