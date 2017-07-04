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
    
    // Center frame on screen 
    this.setLocationRelativeTo(null)

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
Useful table JTable model (AbstractTableModel) useful for displaying
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

