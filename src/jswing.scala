/** 
  *  Jswing - Scala wrapper for Java Swing with GUI-building blocks.
  * 
  *  @author: Caio Rodrigues
  */
package jswing


/** Java swing dialogs (aka Message boxes) to display information or warnings. */
object Dialog {
  import javax.swing.JOptionPane
  import java.awt.Component

  /** 
       Display a message dialog
     
       - JOptionPane.WARNING_MESSAGE

       Example:

    {{{
        jswing.Dialog.showAlert("title","Message body")
    }}}
      
   */
  def showAlert(title: String, message: String, parent: Component = null){
    JOptionPane.showMessageDialog(
         parent
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
  def showInfo(title: String, message: String, parent: Component = null){
    JOptionPane.showMessageDialog(
         parent
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
  def showError(title: String, message: String, parent: Component = null){
    JOptionPane.showMessageDialog(
         parent
        ,message
        ,title
        ,JOptionPane.ERROR_MESSAGE
     )
  }

  /** Prompt the user some question. Equivalent to JOptionPane.showInputDialog */
  def prompt(title: String, message: String = "", parent: Component = null) = {
    val resp = JOptionPane.showInputDialog(parent, title, message)
    Option(resp)
  }

  /** 
    * Yes or no message dialog. If the user answer yes returns true and
    * returns false otherwise. 
    */
  def questionYesNo(title: String, message: String, parent: Component = null) = {
    val resp = JOptionPane.showConfirmDialog(
      parent,
      message,
      title,
      JOptionPane.YES_NO_OPTION
    )
    resp == 0 
  }

   /** Opens a JColorChooser color choosing dialog.  */
   def chooseColor(msg: String = "Choose a color", default: java.awt.Color = null) = {
    val col = javax.swing.JColorChooser.showDialog(null, msg, default)
    Option(col)
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

    /** Set current directory */
    def setDirectory(path: String) = {
      fch.setCurrentDirectory(new java.io.File(path))
    }

    /** Get current directory */
    def getDirectory(path: String) = {
      fch.getCurrentDirectory().getPath()
    }

   /**  Run file selection dialog and returning
    *  the selected directory or None if no directory
    *  is selected.
    */
    def run() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }

    def select() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }

    def selectRun(fn: String => Unit) = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).foreach{ file => fn(file.getPath()) }
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

    /** Set current directory */
    def setDirectory(path: String) = {
      fch.setCurrentDirectory(new java.io.File(path))
    }

    /** Get current directory */
    def getDirectory(path: String) = {
      fch.getCurrentDirectory().getPath()
    }

   /**  Run file selection dialog and returning 
    *  the selected directory or None if no directory 
    *  is selected.
    */         
    def select() = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).map(_.getPath())
    }

    def selectRun(fn: String => Unit) = {
      fch.showOpenDialog(null)
      Option(fch.getSelectedFile()).foreach{ file => fn(file.getPath()) }
    }

    def onSelect(action: Option[String] => Unit)  = {
      val listener = new java.awt.event.ActionListener{
        def actionPerformed(evt: java.awt.event.ActionEvent){
          val file = Option(fch.getSelectedFile()).map(_.getPath())
          action(file)
        }
      }
      fch.addActionListener(listener)
      () => fch.removeActionListener(listener)
    }

    def bindData(path: jswing.data.ValueModel[Option[String]]) = {
      path.get() foreach this.setDirectory
      path.onChangeRun { path.get() foreach this.setDirectory}
      this.onSelect{
        fileOpt => fileOpt match {
          case Some(file)  => path() = Some(file)
          case None        => ()
        }
      }
    }

    def run() = select()

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

  /** Open file with default desktop application.

      Example: It will open this PDF file with default system application.
      {{{
         scala> jswing.JUtils.openFile("/home/archbox/Desktop/functional-gui.pdf")
      }}}
    */
  def openFile(file: String){
    val desk = java.awt.Desktop.getDesktop()
    desk.open(new java.io.File(file))
  }

  /** Open URL with default browser.

      Example:
      {{{
         scala> jswing.JUtils.openUrl("http://www.yandex.com")
      }}}
   */
  def openUrl(uri: String){
    import java.awt.Desktop
    import java.io.IOException
    import java.net.URI
    import java.net.URISyntaxException
    val u = new URI(uri)
    val desktop = Desktop.getDesktop()
    desktop.browse(u)
  }

  /** Read resource file */
  def readResourceFile(file: String): String = {
    def readBufferedReader(bf: java.io.BufferedReader) = {
      val builder = new StringBuilder()
      var line = ""
      while(line != null){
        line = bf.readLine()
        builder.append(line + "\n")
      }
      bf.close()
      builder.toString()
    }
    val txt = for {
      //s = getClass().getResourceAsStream(file)
      st    <- Option(getClass().getResourceAsStream(file))
      is    = new java.io.InputStreamReader(st)
      bf    = new java.io.BufferedReader(is)
      text  = readBufferedReader(bf)
    } yield text

    assert(!txt.isEmpty, s"Error: Resource file ${file} not found.")
    txt.get
  }


} // ------ End of Module JUtils ------ // 


/** 
    @param run        - Function that runs the event handler.
    @param dispose    - Dispose event handler, remove event listener.
    @param setEnabled - Enabled/disable event.
  */
case class EventDispose(
  /** Dispose event handler */
  dispose:    () => Unit,
  /** Enable or disable event */
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
      dispose    = () => entry.getDocument().removeDocumentListener(listener),
      setEnabled = flag => { enabled = flag }
    )
  }


  /** Subscribe to JFormattedTextField value change event. */
  def onValueChange(entry: javax.swing.JFormattedTextField)(handler: => Unit): EventDispose = {
    var enabled = true
    val listener = new java.beans.PropertyChangeListener{
      def propertyChange(evt: java.beans.PropertyChangeEvent){
        if (enabled) handler
      }
    }
    entry.addPropertyChangeListener("value", listener)

    EventDispose(
      dispose    = () => entry.removePropertyChangeListener("value", listener),
      setEnabled = flag => { enabled = flag }
    )
  }


  def onWindowExit(frame: javax.swing.JFrame) (handler: => Unit): EventDispose = {
    var enabled = true
    val listener = new java.awt.event.WindowAdapter(){
          override def windowClosing(evt: java.awt.event.WindowEvent) = {
            if (enabled) handler
          }
    }
    frame.addWindowListener(listener)

    EventDispose(
      dispose    = () => frame.removeWindowListener(listener),
      setEnabled = flag => { enabled = flag }
    )
  }

  /** Subscribes to JList selection event that is fired when user selects some item. */
  def onListSelect[A](jlist: javax.swing.JList[A]) (handler: => Unit): EventDispose = {

    var enabled = true

    val listener = new javax.swing.event.ListSelectionListener(){
      def valueChanged(args: javax.swing.event.ListSelectionEvent){
        if (enabled) handler
      }
    }

    jlist.addListSelectionListener(listener)

    EventDispose(
      dispose    = () => { jlist.removeListSelectionListener(listener) },
      setEnabled = flag => { enabled = flag }
    )
  }


  def onSpinnerChange(spinner: javax.swing.JSpinner)(handler: => Unit) = {
    var enabled = true
    val listener = new javax.swing.event.ChangeListener{
      def stateChanged(evt: javax.swing.event.ChangeEvent){
        if (enabled) handler
      }
    }
    spinner.addChangeListener(listener)
    EventDispose(
      dispose    = () => spinner.removeChangeListener(listener),
      setEnabled = (flag: Boolean) => { enabled = flag }
    )
  }
    
  /** Subscribe to component resize event.

      Example: It will display the JFrame's dimensions whenever it is resized.
      {{{
        import javax.swing._
        val frame = new JFrame("Hello world")
        frame.setSize(500, 400)
        frame.setVisible(true)
        jswing.Event.onResize(frame){
           println(s"New dimensions ${frame.getWidth()} ${frame.getHeight()}")
        }
      }}}

    */
  def onResize(comp: java.awt.Component)(handler: => Unit) = {
    var enabled = true
    val listener = new java.awt.event.ComponentAdapter(){
      override def componentResized(evt: java.awt.event.ComponentEvent){
        handler
      }
    }
    comp.addComponentListener(listener)
    EventDispose(  
      dispose  = () => comp.removeComponentListener(listener),
      setEnabled = (flag: Boolean) => { enabled = flag }
    )
  }

  /** Prints Java Swing component dimensions when it is resized.

      Example:
      {{{
        import javax.swing._
        val frame = new JFrame("Hello world")
        frame.setSize(500, 400)
        frame.setVisible(true)
        jswing.Event.printDimensions(frame)
      }}}
  */
  def printDimensions(comp: java.awt.Component) = {
    onResize(comp){
      println(s"Component dimensions w = ${comp.getWidth()} h = ${comp.getHeight()} ")
    }
  }


  /** Run some action when the JVM shutdowns using the Shutdowns Hook API.

      See: 

      - [[https://docs.oracle.com/javase/8/docs/technotes/guides/lang/hook-design.html]]


      - [[https://stackoverflow.com/questions/914666/how-to-capture-system-exit-event]]

     Example: It will print the message 'Exit application' when the
     JVM shutdowns the program ends its execution.

     {{{
         jswing.Event.onAppExit{ println("Exit application") }
     }}}
   */
  def onAppExit(action: => Unit) = {
    val thread = new java.lang.Thread(){
      override def run() = action
    }
    val runtime = java.lang.Runtime.getRuntime()
    runtime.addShutdownHook(thread)
    () => runtime.removeShutdownHook(thread)
  }

  

} // ------ End of object Event ------- // 


/** Collection of functions to manipulate images. */
object ImageUtils{

  import java.awt.image.BufferedImage

  /** Scale a BufferedImage with a zoom factor increment in percent. */
  def scaleZoom(image: BufferedImage, zoom: Double) = {
     val z = 1.0 + zoom / 100.0
     val wm = (z * image.getWidth().toDouble).toInt
     val hm = (z * image.getHeight().toDouble).toInt     
     image.getScaledInstance(wm, hm, java.awt.Image.SCALE_DEFAULT)  
  }

  /** Scale a BufferedImage to the container size with a zoom factor in percent. */  
  def scaleFitZoom(image: BufferedImage, width: Int, height: Int, zoom: Double = 0.0) = {
    val wi = image.getWidth().toDouble
    val hi = image.getHeight().toDouble
    val z = 1.0 + zoom / 100.0
    val k = (width.toDouble / wi * z) min (height.toDouble / hi * z)
    // New image dimensions
    val wm = (k * wi).toInt
    val hm = (k * hi).toInt
    //println(s" scaleFitZoom2 wm = ${wm} hm = ${hm} / wi = ${wi} hi = ${hi} / d = ${(width, height)} ")
    image.getScaledInstance(wm, hm, java.awt.Image.SCALE_DEFAULT)
  }

  /** Scale a BufferedImage to fit the container size if it is larger than the container. */  
  def scaleFitZoomIfLarger(image: BufferedImage, width: Int, height: Int, zoom: Double = 0.0) = {
    val wi = image.getWidth()
    val hi = image.getHeight()

    //println(s"scaleFitZoomIfLarger wi = ${wi} hi = ${hi} / w = ${width} h = ${width}")

    if (wi > width || hi > height)
      scaleFitZoom(image, width, height, zoom)
    else
      scaleZoom(image, zoom)
  }

  def scaleContainer(image: BufferedImage, cont: java.awt.Container, zoom: Double = 0.0) = {
    scaleFitZoom(image, cont.getWidth(), cont.getHeight(), zoom)
  }

  def scaleContainerIfLarger(image: BufferedImage, cont: java.awt.Container, zoom: Double = 0.0) = {
    scaleFitZoomIfLarger(image, cont.getWidth(), cont.getHeight(), zoom)
  }

  /**  Read a BufferedImage from an image file.*/
  def readFile(file: String) = {
    javax.imageio.ImageIO.read(new java.io.File(file))
  }

  /** Read image from resource file */
  def readResourceImage(file: String) = {
    val img = for {
      file   <-  Option(getClass().getResource(file))
      image  = javax.imageio.ImageIO.read(file)
    } yield image
    assert(!img.isEmpty, s"Error: resource image file ${file} not found.")
    img.get
  }

  }


}
