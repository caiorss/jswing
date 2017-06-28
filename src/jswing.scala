package jswing

import javax.swing.JOptionPane

object Dialog {

  def plainMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.WARNING_MESSAGE
     )
  }

  def informationMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.INFORMATION_MESSAGE
     )
  }

  def errorMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.ERROR_MESSAGE
     )
  }

  def chooseFile(path: String = ".") = {
    val fch = new javax.swing.JFileChooser()
    fch.showOpenDialog(null)
    Option(fch.getSelectedFile()).map(_.getPath())
  }

  def chooseDir(path: String = ".") = {
    val fch = new javax.swing.JFileChooser()
    fch.setCurrentDirectory(new java.io.File("."))
    fch.setDialogTitle("Select a directory")
    fch.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY)
    fch.showOpenDialog(null)
    Option(fch.getSelectedFile()).map(_.getPath())
  }
  

}

object JUtils{
  def invokeLater(handler: () => Unit) = {
    javax.swing.SwingUtilities.invokeLater(
      new Runnable(){
        def run() = handler()
      }
    )
  } // End of invokeLater

}


class ListView(
      title:       String = "List View"
     ,exitOnClose: Boolean = false
     ,visible:     Boolean = false

  ) extends javax.swing.JFrame {
  private val model  = new javax.swing.DefaultListModel[String]()
  private val jlist   = new javax.swing.JList(model)
  private val scroll = new javax.swing.JScrollPane(jlist)

  // This flag avoids firing the event when an item is removed.
  // it also allows temporarily disabling events.
  //
  private var selectionEventFlag = true

  init()

  def init(){
    this.setTitle(title)
    this.add(scroll)
    this.setSize(300, 400)

    if (exitOnClose)
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
    

    this.setVisible(visible)
  }

  def getModel() = model

  def getJList() = jlist

  def getSelectedIndex() = jlist.getSelectedIndex()

  def getSelectedValue() = jlist.getSelectedValue()

  def addElement(elem: String) = model.addElement(elem)

  def addElements(elemList: Array[String])  = {
    elemList.foreach(model.addElement)
  }

  def clear() = model.clear()

  def enableSelectionEvent(flag: Boolean){
    selectionEventFlag = flag 
  }


  def removeItemAt(idx: Int){
    selectionEventFlag = false
    model.removeElementAt(idx)
    selectionEventFlag = true 
  }

  def removeAtSelected(){
    selectionEventFlag = false
    model.removeElementAt(jlist.getSelectedIndex())
    selectionEventFlag = true 
  }

  /// Event that happens when user selects an item 
  def onSelect(handler: => Unit) = {
    val listener = new javax.swing.event.ListSelectionListener(){
      def valueChanged(args: javax.swing.event.ListSelectionEvent){
        if (selectionEventFlag) handler
      }
    }

    jlist.addListSelectionListener(listener)

    // Return function that removes listener
    () => { jlist.removeListSelectionListener(listener) }

  } // End of onSelect

} // ----- End of class ListView ------- // 
