package jswing.panel


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

/** Flow panel with items aligned to the left. */
class FlowPanelLeft(contents: java.awt.Component*) extends javax.swing.JPanel{
  init()
  private def init(){
    this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT))
    for (item <- contents) { this.add(item) }
  }
}

/** Flow panel with items aligned to the right. */
class FlowPanelRight(contents: java.awt.Component*) extends javax.swing.JPanel{
  init()
  private def init(){
    this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT))
    for (item <- contents) { this.add(item) }
  }
}



/** Modified JPanel with layout set to BorderLayout.

    Example: 

    {{{
    import jswing.widgets.{Frame, Button, BorderPanel, FlowPanel}

    val button1 = new Button(
      text = "button1",
      bgColor = java.awt.Color.BLUE,
      onClick = { println("You clicked at button 1") }
    )

    val button2 = new Button(
      text    = "button2",
      bgColor = java.awt.Color.BLUE,
      onClick = { println("You clicked at button 2") }
    )

    val button3 = new Button("Click me")
    button3.onClick{println("You clicked at button3")}

    val textArea = new javax.swing.JTextArea()

    val frame = new Frame(
      title       = "Test Border Panel",
      size        = (692, 441),
      exitOnClose = true,
      visible     = true,
      pane        = new BorderPanel(
        top          = new FlowPanel(button1, button2),
        bottom       = button3,
        centerScroll = textArea
      )
    )
    }}}

 */
class BorderPanel(
  top:    java.awt.Component = null,
  bottom: java.awt.Component = null,
  left:   java.awt.Component = null,
  right:  java.awt.Component = null,
  center: java.awt.Component = null,
  centerScroll:  java.awt.Component = null,
  leftScroll :   java.awt.Component = null,
  rightScroll:   java.awt.Component = null
) extends javax.swing.JPanel {
  private var itemT: java.awt.Component = null
  private var itemB: java.awt.Component = null
  private var itemL: java.awt.Component = null
  private var itemR: java.awt.Component = null
  private var itemC: java.awt.Component = null

  init()

  private def init(){
    this.setLayout(new java.awt.BorderLayout())

    if (top    != null) this.addTop(top)
    if (bottom != null) this.addBottom(bottom)
    if (left   != null) this.addCenter(left)
    if (right  != null) this.addRight(right)
    if (center != null) this.addCenter(center)    
    if (centerScroll != null) this.addCenterScroll(centerScroll)
    if (rightScroll  != null) this.addRightScroll(rightScroll)
    if (leftScroll   != null) this.addLeftScroll(leftScroll)    

  }

  def getTop()    = itemT
  def getBottom() = itemB
  def getLeft()   = itemL
  def getRight()  = itemR
  def getCenter() = itemC

  def addTop(comp: java.awt.Component){
    itemT = comp 
    this.add(comp,  java.awt.BorderLayout.NORTH)
  }

  def addTopItems(items: java.awt.Component*){
    val panel = new javax.swing.JPanel()
    itemT = panel
    for (i <- items) { panel.add(i) }
    this.add(panel,  java.awt.BorderLayout.NORTH)
  }

  def addBottom(comp: java.awt.Component){
    itemB = comp 
    this.add(comp,  java.awt.BorderLayout.SOUTH)
  }

  def addBottomItems(items: java.awt.Component*){
    val panel = new javax.swing.JPanel()
    itemB = panel
    for (i <- items) { panel.add(i) }
    this.add(panel,  java.awt.BorderLayout.SOUTH)
  }


  def addRight(comp: java.awt.Component){
    itemR = comp 
    this.add(comp,  java.awt.BorderLayout.EAST)
  }

  def addLeft(comp: java.awt.Component){
    itemL = comp 
    this.add(comp,  java.awt.BorderLayout.WEST)
  }

  def addCenter(comp: java.awt.Component){
    itemC = comp 
    this.add(comp,  java.awt.BorderLayout.CENTER)
  }
  
  /** Add item inside a scroll panel in the left side */
  def addLeftScroll(comp: java.awt.Component){
    val scroll = new javax.swing.JScrollPane(comp)
    this.addLeft(scroll)
  }

  /** Add item inside a scroll panel in the right side */
  def addRightScroll(comp: java.awt.Component){
    val scroll = new javax.swing.JScrollPane(comp)
    this.addRight(scroll)
  }


  /** Add item inside a scroll panel in the center. */
  def addCenterScroll(comp: java.awt.Component){
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

