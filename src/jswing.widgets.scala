package jswing.widgets

object WidgetUtils { 

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

}


class Button(
  text:    String,
  enabled: Boolean        = true,
  bgColor: java.awt.Color = null,
  fgColor: java.awt.Color = null,
  bgName:  String         = null,
  fgName:  String         = null,
  onClick: => Unit        = ()
) extends javax.swing.JButton {

  init()

  private def init(){
    this.setText(text)
    this.setEnabled(enabled)



    if (bgColor != null) this.setBackground(bgColor)
    if (fgColor != null) this.setForeground(fgColor)

    if (bgName != null)
    jswing.JUtils.invokeLater{ this.setBackground(jswing.JUtils.getColorOrNull(bgName))}

    if (fgName != null)
    jswing.JUtils.invokeLater { this.setForeground(jswing.JUtils.getColorOrNull(fgName))}

    this.onClick{ onClick }
  }


  /** Subscribes to button click event */
  def onClick (handler: => Unit)  = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(evt: java.awt.event.ActionEvent) = {
        handler
      }
    }
    this.addActionListener(listener)
    // Returns function that when executed disposes the event handler
    () => this.removeActionListener(listener)
  }
}


/**
  * Enhanced JFrame class that provides better initialization.
  *
  * Example:
  *
  * It creates a window with title 'Hello World', not resizable, initially visible and ends
  * the application when the user close the window. It also prints a message when the window
  * is closed.
  *
  * {{{
  *     val frame = new jswing.widgets.Frame(
  *       title       = "Hello World",
  *       resizable   = false,
  *       visible     = true,
  *       exitOnClose = true
  *      )
  *
  *     frame.onWindowClose{ println("I was closed") }
  * }}}
  *
  *
  */
class Frame(
  title:     String     = "",
  size:      (Int, Int) = (300, 400),
  visible:   Boolean    = false,
  resizable: Boolean    = true,
  enabled:   Boolean    = true,
  exitOnClose: Boolean  = false

) extends javax.swing.JFrame {

  init()

  private def init(){
    this.setSize(size._1, size._2)
    this.setTitle(title)
    this.setResizable(resizable)
    this.setEnabled(enabled)
    this.setVisible(visible)

    if (exitOnClose)
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  }

  /** Subscribe to window close event notifications */
  def onWindowClose(handler: => Unit){
    val listener = new java.awt.event.WindowAdapter(){
      override def windowClosing(evt: java.awt.event.WindowEvent){
        handler
      }
    }

    this.addWindowListener(listener)

    // Return lambda function that removes the listener
    // or the event handler when executed.
    () => this.removeWindowListener(listener)
  }

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



/**
  * Enhanced Java swing checkbox.
  *
  */
 class CheckBox(
   label: String,
   value: Boolean = false
 ) extends javax.swing.JCheckBox {

   init()

   private def init(){
     this.setText(label)
     this.setSelected(value)
   }

   /** Synonym to method is selected. Returns the checkbox status. */
   def getValue() = {
     this.isSelected()
   }

   /** Subscribes to checkbox click event notifications. */
   def onClick(handler: => Unit){
     val listener = new java.awt.event.ActionListener(){
       def actionPerformed(evt: java.awt.event.ActionEvent) = {
         handler
       }
     }
     this.addActionListener(listener)
     // Returns function that when executed disposes the event handler
     () => this.removeActionListener(listener)
   }

   def onClick(handler: CheckBox => Unit){
     val listener = new java.awt.event.ActionListener(){
       def actionPerformed(evt: java.awt.event.ActionEvent) = {
         handler(evt.getSource().asInstanceOf[CheckBox])
       }
     }
     this.addActionListener(listener)
     // Returns function that when executed disposes the event handler
     () => this.removeActionListener(listener)
   }
 }




/**
  * Widget to show pictures and with facilitate to
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


