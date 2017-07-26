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

/** JButton class extension with better initialization and Scala-friendly 
    method to add event handlers (aka Java's listeners)

    Example: 

    {{{
    import javax.swing._ 
    import jswing.widgets.Button
    import jswing.Dialog

    val frame = new JFrame("Hello world")
    frame.setSize(400, 300)
    frame.setLayout(new java.awt.FlowLayout())

    val button1 = new Button(
      "Click me!",
      // Optional parameters 
      fgColor = java.awt.Color.RED,  // Foreground color 
      bgColor = java.awt.Color.GRAY, // Background color 
      toolTip = "Please click at me"  
    )

    val button2 = new Button("Button Exit")

    frame.add(button1)
    frame.add(button2)
    frame.setVisible(true)

    button1.onClick{
      println("Button 1 clicked")
      Dialog.showAlert("Alert", "Button 1 clicked")
    }

    button2.onClick{ System.exit(0)}
    }}}


  */
class Button(
  text:    String,
  enabled: Boolean        = true,
  bgColor: java.awt.Color = null,
  fgColor: java.awt.Color = null,
  bgName:  String         = null,
  fgName:  String         = null,
  toolTip: String         = null,
  onClick: => Unit        = ()
) extends javax.swing.JButton {

  init()

  private def init(){
    this.setText(text)
    this.setEnabled(enabled)



    if (bgColor != null) this.setBackground(bgColor)
    if (fgColor != null) this.setForeground(fgColor)

    if (toolTip != null) this.setToolTipText(toolTip)

    if (bgName != null)
    jswing.JUtils.invokeLater{ this.setBackground(jswing.JUtils.getColorOrNull(bgName))}

    if (fgName != null)
    jswing.JUtils.invokeLater { this.setForeground(jswing.JUtils.getColorOrNull(fgName))}

    this.onClick{ onClick }
  }

  def onClick (handler: => Unit)  = {
    jswing.Event.onButtonClick(this){handler}
  }
}




case class ComboItem[A](label: String, value: A) {
  override def toString() = label
}


/** Modified JComboBox which each item displayed can have 
    a label and an associated value.

    Example: 

    {{{
        import javax.swing._
        import jswing.widgets.ComboBox
        import jswing.Event

        val frame = new JFrame("Combo Box Demo")
        frame.setSize(500, 300)
        frame.setLayout(new java.awt.FlowLayout())
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

        val display = new JLabel("Currency = ")
        val combo = new ComboBox[String]

        frame.add(combo)
        frame.add(display)
        frame.setVisible(true)

        combo.onSelect {
          val item = combo.getSelectedValue()
          display.setText("Currency = " + item)
        }

        combo.addItem("USD", "United States Dollar")
        combo.addItem("HKD", "Hong Kong Dollar")
        combo.addItem("AUD", "Australian Dollar")
        combo.addItem("EUR", "Euro")
        combo.addItem("JPY", "Japanese Yen")
        combo.addItem("CNY", "Chinese Yuan/Renminbi")

    }}}

*/
class ComboBox[A] extends javax.swing.JComboBox[ComboItem[A]] {
  private val model = new javax.swing.DefaultComboBoxModel[ComboItem[A]]()

  init()

  private def init(){
    this.setModel(model)
  }

  def addItem(label: String, value: A) = {
    model.addElement(ComboItem(label, value))
  }

  def getSelectedValue() = {
    val item = this.getSelectedItem()
    item.asInstanceOf[ComboItem[A]].value 
  }

  def onSelect(action: => Unit) = {
    jswing.Event.onComboBoxSelect(this){action}
  }
}


/** 
    JList extension that allows label and value items. This class also provides 
    type safe and null-safe methods to get selected label and selected items.

    Relevant methods: 

    - def addItem(label: String, value: A): Unit 

    - def addItems(elemList: Seq[(String, A)]): Unit

    - def clear(): Unit

    - def getSelectedItem(): Option[ComboItem[A]]

    - def getSelectedItemLabel(): Option[String]

    - def getSelectedItemValue(): Option[A]

    - def onSelect(handler: ⇒ Unit): EventDispose

    - def removeItemAt(idx: Int): Unit

    - def removeSelectedItem(): Unit


    Example: 

    {{{
    import jswing.widgets.{BorderPanel, ListBox}
    import jswing.{Event, Dialog}
    import javax.swing._

    val dirch = new Dialog.DirChooser().withHome()

    val frame = new JFrame("Listbox test")
    frame.setSize(500, 400)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val buttonOpen = new JButton("Open")
    val buttonClear = new JButton("Clear")

    val display = new JLabel("")
    val lbox = new ListBox[String]()

    val panel = new BorderPanel()
    panel.addTopItems(buttonOpen, buttonClear)
    panel.addCenterScrollPane(lbox)
    panel.addBottomItems(display)

    def showFiles(path: String){
      lbox.clear()
      display.setText("")
      jswing.JUtils.invokeLater{
        val files = new java.io.File(path).listFiles()
        files foreach {f => lbox.addItem(f.getName(), f.getAbsolutePath())}
      }
    }

    Event.onButtonClick(buttonOpen){
      dirch.run() foreach showFiles
    }

    Event.onButtonClick(buttonClear){
      lbox.clear()
      display.setText("")
    }

    lbox.onSelect{
      println("Selected label = " + lbox.getSelectedItemLabel())
      println("Selected value = " + lbox.getSelectedItemValue())
      lbox.getSelectedItemValue foreach display.setText
    }


    frame.setContentPane(panel)
    frame.setVisible(true)
    }}}

  */
class ListBox[A] extends javax.swing.JList[ComboItem[A]] {
  private val model  = new javax.swing.DefaultListModel[ComboItem[A]]()

  // This flag avoids firing the event when an item is removed.
  // it also allows temporarily disabling events.
  //
  private var selectionEventFlag = true

  init()

  private def init(){
    this.setModel(model)
  }

  def addItem(label: String, value: A) =
    model.addElement(ComboItem(label, value))

  def addItems(elemList: Seq[(String, A)])  = {
    for ((label, value) <- elemList) 
      model.addElement(ComboItem(label, value))    
  }

  def getSelectedItem() = {
    Option(this.getSelectedValue())
  }

  def getSelectedItemValue() = {
    Option(this.getSelectedValue()) map (_.value)
  }

  def getSelectedItemLabel() = {
    Option(this.getSelectedValue()) map (_.label)
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

  def removeSelectedItem(){
    selectionEventFlag = false
    model.removeElementAt(this.getSelectedIndex())
    selectionEventFlag = true
  }

  /// Event that happens when user selects an item
  def onSelect(handler: => Unit) = 
    jswing.Event.onListSelect(this){ if (selectionEventFlag) handler }

  def onSelectItem(handler: A => Unit) = {

    var enabled = true
    val jlist = this

    val listener = new javax.swing.event.ListSelectionListener(){
      def valueChanged(args: javax.swing.event.ListSelectionEvent){
        if (enabled) {
          jlist.getSelectedItemValue() foreach handler
        }
      }
    }

    jlist.addListSelectionListener(listener)

    jswing.EventDispose(
      dispose    = () => { jlist.removeListSelectionListener(listener) },
      setEnabled = flag => { enabled = flag }
    )
    
  }

} // ----- End of ListBox class ------- //



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
  title:     String                 = "",
  size:      (Int, Int)             = (300, 400),
  visible:   Boolean                = false,
  resizable: Boolean                = true,
  enabled:   Boolean                = true,
  layout:    java.awt.LayoutManager = null,
  pack:      Boolean                = false,
  pane:      java.awt.Container     = null,
  exitOnClose: Boolean              = false

) extends javax.swing.JFrame {

  init()

  private def init(){
    this.setSize(size._1, size._2)
    this.setTitle(title)
    this.setResizable(resizable)
    this.setEnabled(enabled)
    this.setLayout(layout)
    // Center frame on screen 
    this.setLocationRelativeTo(null)
   
    if (exitOnClose)
      this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

    if (pane != null) this.setContentPane(pane)

    if (pack) this.pack()

    this.setVisible(visible)
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

   /** Clear current image */
   def clear() = {
     this.setIcon(null)
     this.setBackground(java.awt.Color.WHITE)
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



/** 
Extension of JTable widget with better initialization  

Example: 

  {{{
import jswing.widgets.Table
import javax.swing.{JFrame, JScrollPane}
import javax.swing.table.DefaultTableModel

val model = new DefaultTableModel()

Array("Name", "Price", "Quantity") foreach model.addColumn

model.addRow(Array("Sugar", 1.50, 100).asInstanceOf[Array[Object]])
model.addRow(Array("Milk",  2.00, 200).asInstanceOf[Array[Object]])
model.addRow(Array("Wine",  3.25, 300).asInstanceOf[Array[Object]])

val table = new Table(
  model         = model,
  editable      = false,
  showGrid      = false,
  cellSelection = false,
  focusable     = false,
  bgColor       = java.awt.Color.green,
  headerColor   = java.awt.Color.blue
)

val frame = new JFrame("Inventory list")
frame.setSize(300, 400)
frame.add(new JScrollPane(table))
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
frame.setVisible(true)

  }}}


 @param  model         - Table modle 
 @param  editable      - (Default true) If this flag is set makes the cells editable.
 @param  cellSelection - (Default true) If true the cells can be selected.
 @param  focusable     - (Default true) If true the celss can be focused.
 @param  showGrid      - (Default true) If true shows the table grid.
 @param  toolTip       - (Default null) Sets the tooltip text. 
 @param  bgcolor       - (Default null) Sets the table background color. 
 @param  fgColor       - (Default null) Sets the table foreground color.
 @param  headercolor   - (Default null) Sets the header background color. 

*/
class Table(
  model:         javax.swing.table.AbstractTableModel = null,
  editable:      Boolean        = true,
  focusable:     Boolean        = true,
  cellSelection: Boolean        = true,
  showGrid:      Boolean        = true,
  toolTip:       String         = null,
  bgColor:       java.awt.Color = null,
  fgColor:       java.awt.Color = null,
  headerColor:   java.awt.Color = null
  ) extends javax.swing.JTable {
  private var cellEditable = editable

  init()

  private def init(){

    this.setFocusable(focusable)
    this.setCellSelectionEnabled(cellSelection)
    this.setShowGrid(showGrid)

    if (model != null) this.setModel(model)
    if (toolTip != null) this.setToolTipText(toolTip)
    if (bgColor != null) this.setBackground(bgColor)
    if (fgColor != null) this.setForeground(fgColor)
    if (headerColor != null) this.getTableHeader().setBackground(headerColor)
    
  }

  def setHeaderColor(color: java.awt.Color){
     this.getTableHeader().setBackground(color) 
  }

  def setCellEditable(flag: Boolean)    = { cellEditable = flag }
  def setBgColor(color: java.awt.Color) = { this.setBackground(color) }
  def setFgColor(color: java.awt.Color) = { this.setForeground(color) }

  override def isCellEditable(arg0: Int, arg1: Int) = cellEditable

} // -------- End of class Table ---------- // 




/** 
Useful JTable model (AbstractTableModel) for displaying
Scala Case classes or algebraic data types.
 
Important methods: 
 
 - .addItem(A): Unit

 - .addItems(Seq[A]): Unit

 - .clear(): Unit 

Example:   

    {{{ 
import jswing.widgets.MTableModel
import javax.swing.{JFrame, JPanel, JTable, JScrollPane}


case class InventoryItem(name: String, price: Double, number: Int)

val products = Array(
  InventoryItem("Sugar", 1.20, 100),
  InventoryItem("Milk",  2.50, 200),
  InventoryItem("Coca Cola", 4.00, 300),
  InventoryItem("Beans", 5.0, 600)
)


def itemToCol(item: InventoryItem, col: Int) = col match {
  case 0 => item.name.asInstanceOf[Object]
  case 1 => item.price.asInstanceOf[Object]
  case 2 => item.number.asInstanceOf[Object]
  case _ => error("Error: Column number out of range.")
}

val tableModel = new MTableModel[InventoryItem](
  columns   = Array("Name", "Price", "Quantity"),
  columnsFn = itemToCol
)

tableModel.addItems(products)

val table = new JTable(tableModel)
val frame = new JFrame("Inventory list")
frame.setSize(300, 400)
frame.add(new JScrollPane(table))
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
frame.setVisible(true)

     }}}
 
     It will display a table like this: 

  {{{

| Name      | Price | Quantity |
|-----------+-------+----------|
| Sugar     |   1.2 |      100 |
| Milk      |   2.5 |      200 |
| Coca Cola |   4.0 |      300 |
| Beans     |   5.0 |      600 |

  }}}
 
     @param columns    - Array containing the column names.
     @param columnsFn  - Function that maps the algebraic data type into the column.
  *  
 */
class MTableModel[A](
  columns:   Array[String],
  columnsFn: (A, Int) => Object,
  items:     Seq[A]   = Seq()
)extends javax.swing.table.AbstractTableModel {
  val data = scala.collection.mutable.ListBuffer[A]()

  init()

  private def init(){
    this.addItems(items)
  }

  // Required by  AbstractTableModel  
  def getRowCount() = data.length

  // Required by  AbstractTableModel
  def getColumnCount = columns.length

  // Required by  AbstractTableModel
  def getValueAt(row: Int, col: Int): Object =  {
    columnsFn(data(row), col)
  }
  
  override def getColumnName(col: Int) = {
    columns(col)
  }

  /** Add row to the table model. */
  def addItem(item: A) = {
    data.append(item)
    //this.fireTableChanged()
  }

  /** Add rows to the table model. */
  def addItems(items: Seq[A]) {
    //for (i < - items) { data.append(i) }
    items foreach (i => data.append(i) )
    this.fireTableDataChanged()
  }

  /** Removes all items. */
  def clear() = {
    data.clear()
    this.fireTableDataChanged()
  }

  override def isCellEditable(row: Int, col: Int) = false
}



class TrayIcon(
  file: String,
  toolTip: String = "Tray Icon app",
  offset: Int     = 0
) {
  private val tray = java.awt.SystemTray.getSystemTray()
  private val toolkit = java.awt.Toolkit.getDefaultToolkit()
  private val image  = toolkit.getImage(file)
  private val popuMenu = new java.awt.PopupMenu()
  private val icon   = new java.awt.TrayIcon(image)
  private val frame = new javax.swing.JFrame("")
  private var offsetY = offset

  init()

  private def init(){

    icon.setToolTip(toolTip)
    icon.setImageAutoSize(true)

    frame.setUndecorated(true)
    frame.setResizable(false)
    frame.setVisible(true)  
    frame.setVisible(true)
    frame.add(popuMenu)


    icon.addMouseListener(new java.awt.event.MouseAdapter(){
      override def mouseClicked(evt: java.awt.event.MouseEvent){
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3){
          popuMenu.show(frame, evt.getXOnScreen(), evt.getYOnScreen() - offsetY)          
        }
      }
    })

  }

  //def setOffset(offset: Int) = { offsetY = offset } 

  def onClick(handler: => Unit) = {
    val listener = new java.awt.event.ActionListener(){
      def actionPerformed(event: java.awt.event.ActionEvent){
        handler 
      }
    }
    icon.addActionListener(listener)
  }

  def addMenuItem(label: String)(action: => Unit){
    val menuItem = new java.awt.MenuItem(label)
    val listener = jswing.Event.makeActionListener(() => action)
    menuItem.addActionListener(listener)
    popuMenu.add(menuItem)
    offsetY = offsetY + 20
  }

  def setVisible(flag: Boolean){
    if(flag)
      tray.add(icon)
    else
      tray.remove(icon)
  }

  def show() = tray.add(icon)

  def hide() = tray.remove(icon)

  def showInfo(title: String, message: String){
    icon.displayMessage(title, message,  java.awt.TrayIcon.MessageType.INFO)
  }

  def showError(title: String, message: String){
    icon.displayMessage(title, message,  java.awt.TrayIcon.MessageType.ERROR)
  }

  def showWarning(title: String, message: String){
    icon.displayMessage(title, message,  java.awt.TrayIcon.MessageType.WARNING)
  }

}

