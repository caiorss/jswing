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



    class ListBox extends javax.swing.JList {
      private val model  = new javax.swing.DefaultListModel[String]()
      private val jlist  = new javax.swing.JList(model)
      private val scroll = new javax.swing.JScrollPane(jlist)

      // This flag avoids firing the event when an item is removed.
      // it also allows temporarily disabling events.
      //
      private var selectionEventFlag = true

      init()

      def init(){
      }

      // def getModel() = model
      def addTo(wdg: javax.swing.JComponent) = scroll.add(this)

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
        model.removeElementAt(this.getSelectedIndex())
        selectionEventFlag = true
      }

      /// Event that happens when user selects an item
      def onSelect(handler: => Unit) = {
        val listener = new javax.swing.event.ListSelectionListener(){
          def valueChanged(args: javax.swing.event.ListSelectionEvent){
            if (selectionEventFlag) handler
          }
        }

        this.addListSelectionListener(listener)

        // Return function that removes listener
        () => { this.removeListSelectionListener(listener) }

      } // End of onSelect

    } // ----- End of class ListView ------- //


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


