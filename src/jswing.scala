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









      }




