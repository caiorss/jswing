/** 
  *  Jswing - Scala wrapper for Java Swing with GUI-building blocks.
  * 
  *  @author: Caio Rodrigues
  */
package jswing

import javax.swing.JOptionPane



/** Java swing dialogs (aka Message boxes) to display information or warnings. */
object Dialog {

  /** 
       Display a message dialog
     
       - JOptionPane.WARNING_MESSAGE

       Example:

    {{{
        jswing.Dialog.showAlert("title","Message body")
    }}}
      
   */
  def showAlert(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,message
        ,title
        ,JOptionPane.WARNING_MESSAGE
     )
  }

  /** 
       Displays an information dialog 

       - JOptionPane.INFORMATION_MESSAGE


       Example:

    {{{
        jswing.Dialog.showInfo("title","Message body")
    }}}
    */
  def showInfo(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,message
        ,title
        ,JOptionPane.INFORMATION_MESSAGE
     )
  }

  /** 
       Displays an error dialog 

       - JOptionPane.ERROR_MESSAGE


       Example:

    {{{
        jswing.Dialog.showInfo("title","Message body")
    }}}

    */
  def showError(title: String, message: String){
    JOptionPane.showMessageDialog(
         null
        ,message
        ,title
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
      fch.setFileFilter(imgFilter)
      this
    }

    def withFilter(description: String, extList: Array[String]) = {
      val filter = makeFileFilter(description, extList)
      this.setFileFilter(filter)
      this
    }

    def withHome() = {
      val home = javax.swing.filechooser.FileSystemView
        .getFileSystemView()
        .getHomeDirectory()
      fch.setCurrentDirectory(home)
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
      fch.setDialogTitle(title)
      fch.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY)
      fch.setFileHidingEnabled(!showHidden)
    }

    def withHome() = {
      val home = javax.swing.filechooser.FileSystemView
        .getFileSystemView()
        .getHomeDirectory()
      fch.setCurrentDirectory(home)
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
  

}


/**
  * General utilities for Java Swing. It provide functions to pick
  * color, run actions on java swing thread, run actions periodically
  * or with delay and etc.
  *
  */
object JUtils{

  private val rgbRegex = """rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)""".r

  def getColorOrNull(color: String) = color match {
    case "blue"    => java.awt.Color.blue
    case "cyan"    => java.awt.Color.cyan      
    case "red"     => java.awt.Color.red
    case "green"   => java.awt.Color.green
    case "yellow"  => java.awt.Color.yellow
    case "white"   => java.awt.Color.white
    case "pink"    => java.awt.Color.pink
    case "black"   => java.awt.Color.black
    case "magenta" => java.awt.Color.magenta
    case "orange"  => java.awt.Color.orange
    case "gray"    => java.awt.Color.gray
    case rgbRegex(r, g, b) => new java.awt.Color(r.toInt, g.toInt, b.toInt)
    case  _        => null
  }

  def getColor(color: String) = Option(getColorOrNull(color))

  /** 
      Get the name of current scala script being run as $ scala /path/script.scala.
      It will return /path/script.scala 
    */
  def getScriptName() = System.getProperty("sun.java.command").split(" ")(1)

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

  /** 
     Run an action every a milliseconds period with java
     swing Timer.
      
     @param  period - Period in milliseconds (seconds x 1000)
     @param  action - Code block that will be run periodically. 
     @return - Function that when executed stops the timer.
    */
  def runEvery(period: Int)(action: => Unit) = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(ev: java.awt.event.ActionEvent) = action
    }
    val timer = new javax.swing.Timer(period, listener)
    timer.start()
    () => timer.stop()
  }


  /**
     Run an action a single-time after a milliseconds delay with java
     swing Timer.

     @param  delay - Period in milliseconds (seconds x 1000)
     @param  action - Code block that will be run after a delay.
     @return - Function that when executed stops the timer.
    */
  def runDelay(delay: Int)(action: => Unit) = {
    var timer: javax.swing.Timer = null
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(ev: java.awt.event.ActionEvent) = {
        action
        timer.stop()
      }
    }
    timer = new javax.swing.Timer(delay, listener)
    timer.start()
    () => timer.stop()
  }


  /** Get screenshot of jswing component such as JFrame */
  def getScreenShot(component: java.awt.Component) = {
    val image = new java.awt.image.BufferedImage(
      component.getWidth(),
      component.getHeight(),
      java.awt.image.BufferedImage.TYPE_INT_RGB
    )
    component.paint(image.getGraphics())
    image
  }

  /** Save a screenshot of jswing component such as JFrame to a PNG file. */  
  def saveScreenShot(component: java.awt.Component, file: String){
    val img = getScreenShot(component)
    javax.imageio.ImageIO.write(img, "png", new java.io.File(file))
  }
  


  def saveScreenShotArgs(comp: java.awt.Component, file: String = null)(args: Array[String]) = {
    args match{
      case Array("-image")
          => {
            println("Saving image to file: " + file)
            jswing.JUtils.saveScreenShot(comp, file)
          }

      case Array("-image", filen)
          => {
            println("Saving image to file: " + filen)
            jswing.JUtils.saveScreenShot(comp, filen)
          }

      case _
          => ()
    }
  }

  /** Returns screen dimensions in pixels width x height */
  def getScreenSize() = {
    val dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
    (dim.getWidth(), dim.getHeight())
  }

} // ------ End of Module JUtils ------ // 


case class EventDispose(
  run:        () => Unit,
  dispose:    () => Unit,
  setEnabled: Boolean => Unit
)


/** Provides functions to manipulate Java Swing event handlers */
object Event{

  /** Function that when executed disposes the removes the event handler */
  type Dispose = () => Unit

  /** Create an action listener from a function that actionCommand (type String)  as parameter. */
  def makeActionListener(handler: String => Unit) = {
    new java.awt.event.ActionListener(){
      def actionPerformed(event: java.awt.event.ActionEvent){
        handler(event.getActionCommand())
      }
    }
  }


  def makeActionListener(handler: () => Unit) = {
    new java.awt.event.ActionListener(){
      def actionPerformed(event: java.awt.event.ActionEvent){
        handler()
      }
    }
  }



  /** Add listener to button */
  def addButtonListener(comp: javax.swing.JButton)(handler: String => Unit) = {
    val listener = makeActionListener(handler)
    comp.addActionListener(listener)
      () => comp.removeActionListener(listener)
  }


  /** Add listener to combo box */
  def onComboBoxSelect [A](comp: javax.swing.JComboBox[A])(handler: => Unit) = {
    var enabled = true

    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(event: java.awt.event.ActionEvent){
        if (enabled) handler
      }
    }

    comp.addActionListener(listener)

    EventDispose(
      run        = () => handler,
      dispose    = () => comp.removeActionListener(listener),
      setEnabled = flag => { enabled = flag }
    )
  }


  /** Subscribes to button click event */
  def onButtonClick(button: javax.swing.JButton) (handler: => Unit) : EventDispose = {
    var enabled = true
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        if (enabled) handler
      }
    }
    button.addActionListener(listener)
    // Returns function that when executed disposes the event handler 
    EventDispose(
      run        = () => handler,
      dispose    = () => button.removeActionListener(listener),
      setEnabled = flag => { enabled = flag }
    )
  }


  /** Subscribes to button click event */
  def onButtonClickAction(button: javax.swing.JButton) (handler: String => Unit) : Dispose = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        handler(evt.getActionCommand())        
      }
    }

    button.addActionListener(listener)
    // Returns function that when executed disposes the event handler 
    () => button.removeActionListener(listener)        
  }

    /** Subscribes to button click event */
  def onMenuClickAction(menu: java.awt.MenuItem) (handler: String => Unit) : Dispose = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        handler(evt.getActionCommand())
      }
    }

    menu.addActionListener(listener)
    // Returns function that when executed disposes the event handler
    () => menu.removeActionListener(listener)
  }


  /** Subscribes to checkbox click event notifications */
  def onCheckboxClick(chbox: javax.swing.JCheckBox) (handler: => Unit) = {
    var enabled = true
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        if (enabled) handler
      }
    }
    chbox.addActionListener(listener)

    EventDispose(
      run        = () => handler,
      dispose    = () => chbox.removeActionListener(listener),
      setEnabled = flag => { enabled = flag }
    )
  }


  /** Event fired when text is changed or user type something in a text field. */ 
  def onTextChange(entry: javax.swing.JTextField)(handler: => Unit) : EventDispose = {
    var enabled = true
    val listener = new javax.swing.event.DocumentListener(){
      def changedUpdate(arg: javax.swing.event.DocumentEvent) = if (enabled) handler
      def insertUpdate (arg: javax.swing.event.DocumentEvent) = if (enabled) handler
      def removeUpdate (arg: javax.swing.event.DocumentEvent) = if (enabled) handler
    }

    entry.getDocument().addDocumentListener(listener)

    EventDispose(
      run        = () => handler,
      dispose    = () => entry.getDocument().removeDocumentListener(listener),
      setEnabled = flag => { enabled = flag }
    )
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

