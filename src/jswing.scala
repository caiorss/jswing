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

  class DirChooser(
        current: String     = "."
       ,title: String       = "Select a directory"
       ,showHidden: Boolean = true
  ){
    private val fch = new javax.swing.JFileChooser()

    init()

    def init(){
      fch.setCurrentDirectory(new java.io.File(current))
      fch.setDialogTitle("Select a directory")
      fch.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY)
      fch.setFileHidingEnabled(!showHidden)
    }

    def run() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }
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

// Custom widgets 
object Widgets {

    class PictureBox extends javax.swing.JLabel {
      //init()

      private def scaleImage(image: java.awt.image.BufferedImage, height: Int) = {
        val w = image.getWidth().toFloat
        val h = image.getHeight().toFloat
        val width = (height.toFloat / h * w).toInt
        image.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT)
      }


      def setImageFromFile(file: String) {
        val image  = javax.imageio.ImageIO.read(new java.io.File(file))
        val ico = new javax.swing.ImageIcon(image)
        this.setIcon(ico)
      }

      def setImageFromFile(file: String, height: Int) {
        val image0  = javax.imageio.ImageIO.read(new java.io.File(file))
        val image   = scaleImage(image0, height)
        val ico = new javax.swing.ImageIcon(image)
        this.setIcon(ico)
      }

      def setImage(image: java.awt.image.BufferedImage){
        val ico = new javax.swing.ImageIcon(image)
        this.setIcon(ico)    
      }

      def setImage(image: java.awt.Image){
        val ico = new javax.swing.ImageIcon(image)
        this.setIcon(ico)    
      }

      def getImageSize() = {
        val ico = this.getIcon()
        val x = ico.getIconWidth()
        val y = ico.getIconHeight()
        (x, y)
      }

    } // --- End of Class Picturebox --- //


}


