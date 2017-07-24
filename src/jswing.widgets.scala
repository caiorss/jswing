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


/** Modified JPanel with FlowLayout, but with better initialization.

    Example:

    {{{
    import javax.swing._
    import jswing.widgets.FlowPanel

    val panel = new FlowPanel(
      new JButton("Click me"),
      new JComboBox(),
      new JButton("Button 2")
    )

    val frame = new JFrame()
    frame.setSize(400, 300)
    frame.setContentPane(panel)
    frame.setVisible(true)
    }}}

*/
class FlowPanel(contents: java.awt.Component*) extends javax.swing.JPanel{
  init()

  private def init(){
    for (item <- contents) { this.add(item) }
  }
}



/** Modified JPanel with BorderPanel */
class BorderPanel extends javax.swing.JPanel {
  init()

  private def init(){
    this.setLayout(new java.awt.BorderLayout())    
  }

  def addTop(comp: java.awt.Component){
    this.add(comp,  java.awt.BorderLayout.NORTH)
  }

  def addBottom(comp: java.awt.Component){
    this.add(comp,  java.awt.BorderLayout.SOUTH)
  }

  def addRight(comp: java.awt.Component){
    this.add(comp,  java.awt.BorderLayout.EAST)
  }

  def addLeft(comp: java.awt.Component){
    this.add(comp,  java.awt.BorderLayout.WEST)
  }

  def addCenter(comp: java.awt.Component){
    this.add(comp,  java.awt.BorderLayout.CENTER)
  }
  
  def addStatusBar() = {
    val label = new javax.swing.JLabel()
    this.addBottom(label)
    label
  }

  def addCenterScrollPane(comp: java.awt.Component){
    val scroll = new javax.swing.JScrollPane(comp)
    this.addCenter(scroll)    
  }

}



/** 
    Class to build forms like JGoodies. It leverages the GridbagLayout
    hiding implementation details and providing an easy and high level
    interface to the application developer.

  */
class FormBuilder{
  private val panel = new javax.swing.JPanel()
  private val c = new java.awt.GridBagConstraints()
  private var x: Int = 0
  private var y: Int = 0
  private var xMax: Int = -1

  init()

  private def init(){  
    panel.setLayout(new java.awt.GridBagLayout())
    c.fill = java.awt.GridBagConstraints.HORIZONTAL
    c.insets = new java.awt.Insets(10, 10, 10, 10)
  }
 
  def getPanel() = panel

  def getC() = c

  /** Add the JPanel created to a JFrame */
  def addToFrame(frame: javax.swing.JFrame){
    frame.setContentPane(panel)
  }

  /** Advance to next line. */
  def nextRow() = {
    x = 0
    y = y + 1
  }

  def nextCol() = {
    x = x + 1
    xMax = xMax max x 
  }

  /** Add a new JComponent widget advancing one column to the right */
  def add(item:  javax.swing.JComponent, w: Int = 1, h: Int = 1){
    c.gridx = x 
    c.gridy = y
    c.gridwidth = w
    c.gridheight = h
    x = x + 1
    xMax = x max xMax
    panel.add(item, c)
  }

  /** 
     Add a label and a new JComponent widget advancing two columns to
     the right 
    */
  def add(label: String, item:  javax.swing.JComponent){
    val lbl = new javax.swing.JLabel(label)

    c.gridx = x
    c.gridy = y 
    panel.add(lbl, c)

    c.gridx = x + 1
    c.gridy = y
    x = x + 2
    xMax = x max xMax
    panel.add(item, c)
  }


  def addButton(label: String) = {
    val item = new javax.swing.JButton(label)
    this.add(item)
    item 
  }

  /** 
      Add new label with a separator and advances one line. 
    */
  def addRowLabel(label: String, w: Int = 1) = {
    val lbl = new javax.swing.JLabel(label)
    c.fill = java.awt.GridBagConstraints.BOTH
    val sep = new javax.swing.JSeparator()
    this.add(lbl)
    this.add(sep, w = w)
    this.nextRow()
    lbl
  }

  /** Add a text field with a label at the left side. */ 
  def addTextField(label: String, w: Int = 1, columns: Int = 10) = {
    val tf = new javax.swing.JTextField(columns)
    val wo = c.gridwidth
    c.gridwidth = w 
    this.add(label, tf)
    c.gridwidth = wo
    tf 
  }

  /** Add a text field with a label at the left side advancing a new line. */
  def addTextFieldRow(label: String, columns: Int = 10) = {
    val tf = new javax.swing.JTextField(columns)
    this.add(label, tf)
    this.nextRow()
    tf 
  }

  /** Add a formatted text field with a label */
  def addFTextField[A](label: String, value: A = null, columns: Int = 10) = {
    val tf = new javax.swing.JFormattedTextField()
    tf.setColumns(columns)
    if (value != null) { tf.setValue(value) }
    this.add(label, tf)
    tf 
  }

  /** Add a formatted text field with a label advancing a new line. */  
  def addFTextFieldRow[A](label: String, value: A = null, columns: Int = 10) = {
    val tf = new javax.swing.JFormattedTextField()
    tf.setColumns(columns)
    if (value != null) { tf.setValue(value) }
    this.add(label, tf)
    this.nextRow()
    tf 
  }   

  /** Add a checkbox with a label */
  def addCheckBox(label: String, value: Boolean = false) = {
    val item = new javax.swing.JCheckBox()
    item.setSelected(value)
    this.add(label, item)
    item
  }

  /** Add new ComboBox item */
  def addComboBox(label: String, values: Array[String] = Array()) = {
    val item = new javax.swing.JComboBox(values)
    this.add(label, item)
    item
  }

  /** Add a scroll pane */
  def addScrollPane(item: javax.swing.JComponent){
    val sc = new javax.swing.JScrollPane(item)
    val fill = c.fill 
    c.fill = java.awt.GridBagConstraints.BOTH
    c.weightx = 1.0
    c.weighty = 1.0
    this.nextRow()
    this.add(sc, w = 4)
    c.fill = fill
  }

  def addPanel() = {
    val panel = new javax.swing.JPanel()
    this.add(panel)
    this.nextRow()
    panel
  }

  def addRowComponents(    
    items: Array[javax.swing.JComponent] = Array(),
    ipadx: Int = 0
  ) = {

    val ipx = c.ipadx
    c.ipadx = ipadx

    val panel = new javax.swing.JPanel()
    items foreach panel.add
    this.add(panel)

    c.ipadx = ipx

    this.nextRow()    
    panel
  }

} 
