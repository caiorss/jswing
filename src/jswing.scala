/** 
  *  Jswing - Scala wrapper for Java Swing with GUI-building blocks.
  * 
  *  @author: Caio Rodrigues
  */
package jswing

import javax.swing.JOptionPane



/** Java swing dialogs (aka Message boxes) to display information or warnings. */
object Dialog {

  def plainMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.WARNING_MESSAGE
     )
  }

  /** Displays an information dialog */
  def informationMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.INFORMATION_MESSAGE
     )
  }

  /** Displays an error dialog */
  def errorMsg(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,title
        ,message
        ,JOptionPane.ERROR_MESSAGE
     )
  }

  def chooseFile(path: String = ".", showHidden: Boolean = true) = {
    val fch = new javax.swing.JFileChooser()
    fch.showOpenDialog(null)
    fch.setFileHidingEnabled(showHidden)
    Option(fch.getSelectedFile()).map(_.getPath())
  }

  def chooseDir(path: String = ".", showHidden: Boolean = true) = {
    val fch = new javax.swing.JFileChooser()
    fch.setCurrentDirectory(new java.io.File("."))
    fch.setDialogTitle("Select a directory")
    fch.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY)
    fch.setFileHidingEnabled(false)
    fch.showOpenDialog(null)   
    Option(fch.getSelectedFile()).map(_.getPath())
  }


  def makeFileFilter(description: String, extList: Array[String]) = {
    new javax.swing.filechooser.FileFilter{
      def accept(f: java.io.File) = {
        f.isDirectory() || extList.exists(e => f.getName().endsWith(e))
      }
      def getDescription() = description
    }
  }


  class FileChooser(
        current:    String   = "."
       ,title:      String   = "Select a directory"
       ,showHidden: Boolean  = true
  ){
    private val fch = new javax.swing.JFileChooser()

    init()

    private def init(){
      fch.setCurrentDirectory(new java.io.File(current))
      fch.setDialogTitle(title)
      fch.setFileHidingEnabled(!showHidden)
    }

    def setFileFilter(filter: javax.swing.filechooser.FileFilter){
      fch.setFileFilter(filter)
    }

    def addFileFilter(filter: javax.swing.filechooser.FileFilter){
      fch.addChoosableFileFilter(filter)
    }

    def withImageFilter() = {
      val imgFilter = makeFileFilter(
        "Image Files",
        Array(".png", ".tiff", ".tif", ".jpeg", ".jpg", ".bmp")
      )
      this.setFileFilter(imgFilter)
      this
    }

    def withFilter(description: String, extList: Array[String]) = {
      val filter = makeFileFilter(description, extList)
      this.setFileFilter(filter)
      this
    }

   /**  Run file selection dialog and returning
    *  the selected directory or None if no directory
    *  is selected.
    */
    def run() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }

  } // End of class DirChooser


  /**
  *  Dialog to select directories.
  *
  * Example: 
  * 
  * {{{
  *  scala> val chooser = new jswing.Dialog.DirChooser()
  * 
  *  scala> chooser.run()
  *  res0: Option[String] = Some(/home/archbox/Projects)
  * 
  *  scala> chooser.run()
  *  res1: Option[String] = Some(/home/archbox/Projects)
  *  }}}
  *    
  *   
  *  @param current     Current directory 
  *  @param title       Dialog title
  *  @param showHidden  Flag that if set to true show hidden files.
  * 
  */    
  class DirChooser(
        current:    String   = "."
       ,title:      String   = "Select a directory"
       ,showHidden: Boolean  = true
  ){
    private val fch = new javax.swing.JFileChooser()

    init()

    private def init(){
      fch.setCurrentDirectory(new java.io.File(current))
      fch.setDialogTitle("Select a directory")
      fch.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY)
      fch.setFileHidingEnabled(!showHidden)
    }

   /**  Run file selection dialog and returning 
    *  the selected directory or None if no directory 
    *  is selected.
    */         
    def run() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }
  }
  

}

object JUtils{

  def getColor(color: String) = java.awt.Color.getColor(color)

  def invokeLater(handler: => Unit) = {
    javax.swing.SwingUtilities.invokeLater(
      new Runnable(){
        def run() = handler
      }
    )
  } // End of invokeLater

  def invokeAndWait(handler: => Unit) = {
    javax.swing.SwingUtilities.invokeAndWait(
      new Runnable(){
        def run() = handler
      }
    )
  }

} // ------ End of Module JUtils ------ // 

/** Provides functions to manipulate Java Swing event handlers */
object Event{

  /** Function that when executed disposes the removes the event handler */
  type Dispose = () => Unit

  /** Subscribes to button click event */
  def onButtonClick(button: javax.swing.JButton) (handler: => Unit) : Dispose = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        handler
      }
    }
    button.addActionListener(listener)
    // Returns function that when executed disposes the event handler 
    () => button.removeActionListener(listener)
    }

  /** Event fired when text is changed or user type something in a text field. */ 
  def onTextChange(entry: javax.swing.JTextField)(handler: => Unit) : Dispose = {
    val listener = new javax.swing.event.DocumentListener(){
      def changedUpdate(arg: javax.swing.event.DocumentEvent) = handler
      def insertUpdate (arg: javax.swing.event.DocumentEvent) = handler
      def removeUpdate (arg: javax.swing.event.DocumentEvent) = handler
    }
    entry.getDocument().addDocumentListener(listener)       
    () => entry.getDocument().removeDocumentListener(listener)
  }


  /** Subscribe to JFormattedTextField value change event. */
  def onValueChange(entry: javax.swing.JFormattedTextField)(handler: => Unit): Dispose = {
    val listener = new java.beans.PropertyChangeListener{
      def propertyChange(evt: java.beans.PropertyChangeEvent){
        handler     
      }
    }
    entry.addPropertyChangeListener("value", listener)
    () => entry.removePropertyChangeListener("value", listener)
  }


  def onWindowExit(frame: javax.swing.JFrame) (handler: => Unit): Dispose = {
      val listener = new java.awt.event.WindowAdapter(){
          override def windowClosing(evt: java.awt.event.WindowEvent) = {
            handler
          }
      }
      frame.addWindowListener(listener)
      () => frame.removeWindowListener(listener)
  }

  /** Subscribes to JList selection event that is fired when user selects some item. */
  def onListSelect[A](jlist: javax.swing.JList[A]) (handler: => Unit): Dispose = {
    val listener = new javax.swing.event.ListSelectionListener(){
      def valueChanged(args: javax.swing.event.ListSelectionEvent){
        handler
      }
    }

    jlist.addListSelectionListener(listener)

    // Return function that removes listener
    () => { jlist.removeListSelectionListener(listener) }
  }


} // ------ End of object Event ------- // 


// Custom widgets 
object Widgets {


    def makeDateFormat(format: String = "yyyy-mm-dd") = new java.text.SimpleDateFormat(format)

    def makeNumberFormat(precision: Int = 4) = {
      val fmt = java.text.NumberFormat.getNumberInstance()
      fmt.setMinimumFractionDigits(precision)
      fmt
    }

    def makeFormattedEntry[A](init: A, format: java.text.Format = null, columns: Int = 10) = {
      val entry = new javax.swing.JFormattedTextField(format)
      entry.setValue(init)
      entry.setColumns(columns)
      entry 
    }

    def makeNumberEntry[A](init: A, precision: Int = 3, columns: Int = 10) = {
      val fmt = java.text.NumberFormat.getNumberInstance()
      fmt.setMinimumFractionDigits(precision)    
      val entry = new javax.swing.JFormattedTextField(fmt)
      entry.setValue(init)
      entry.setColumns(columns)
      entry 
    }

    def makeFloatEntry[A](init: Double, precision: Int = 3, columns: Int = 10) = {
      val entry = new javax.swing.JFormattedTextField()
      entry.setValue(init)
      entry.setColumns(columns)
      entry 
    }

    def makeFloatDisplay(init: Double, precision: Int = 3, columns: Int = 10) = {
      val entry = new javax.swing.JFormattedTextField()
      entry.setValue(init)
      entry.setColumns(columns)
      entry.setEditable(false)
      entry 
    }



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


   /** Widget to show pictures and with facilitate to 
     * manipulate and display image.
     * 
     */  
    class PictureBox extends javax.swing.JLabel {
      //init()

      private def scaleImage(image: java.awt.image.BufferedImage, height: Int) = {
        val w = image.getWidth().toFloat
        val h = image.getHeight().toFloat
        val width = (height.toFloat / h * w).toInt
        image.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT)
      }

    /** Set image from file. 
      *
      */
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


      def onClick(handler: => Unit) = {
        this.addMouseListener( new java.awt.event.MouseAdapter {
          override def mouseClicked(arg: java.awt.event.MouseEvent){
            handler
          }
        }
        )
      }


    } // --- End of Class Picturebox --- //


}


