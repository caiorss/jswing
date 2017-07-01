/** 
  * GUI Building blocks for Java Swing GUI toolkit.
  * 
  *  This package provides GUI building blocks simple 
  *  and ready to use such as GUI to display list data, 
  *  display tables and pictures.
  *  
  */
package jswing.guis

/**  Simple GUI building-block for displaying pictures. 
 */     
class PictureFrame(
      title:       String   = "Picture Box"
     ,exitOnClose: Boolean  = false
     ,visible:     Boolean  = false
     ,size:      (Int, Int) = (300, 400)
) extends javax.swing.JFrame {
  private val pbox = new jswing.widgets.PictureBox()

  init()

  def init(){
    this.add(pbox)

    if (exitOnClose)
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

    this.setTitle(title)
    val (w, h) = size
    this.setSize(w, h)
    this.setVisible(visible)

  }

  def getPictureBox() = pbox

  /** Set picture from file */
  def setImageFromFile(file: String)                = pbox.setImageFromFile(file)

  /** Set picture from file scaling to a height*/
  def setImageFromFile(file: String, height: Int)   = pbox.setImageFromFile(file, height)

  def setImage(image: java.awt.image.BufferedImage) = pbox.setImage(image)
  def setImage(image: java.awt.Image)               = pbox.setImage(image)

  def onClick(handler: => Unit){
    pbox.onClick{handler}
  }

} // End of class PictureFrame //


/** 
  *  GUI for displaying list data or array data. 
  *
  * Example: 
  * 
  * {{{
  *     scala -cp bin/jswing.jar
  * 
  *    val lview = new jswing.guis.ListView(
  *        title       = "Sample ListView",
  *        visible     = true,
  *        exitOnClose = true 
  *    )
  * 
  *    val files = new java.io.File("/").listFiles().map(_.toString)
  * 
  *    // Whenever the user click at some item, it will print this message.
  *    lview.onSelect { println("You selected item: = " + lview.getSelectedValue())}
  * 
  *    // Fill the list view executing it at the Java Swing thread.
  *    jswing.JUtils.invokeLater{ lview.addElements(files) }
  * 
  *   
  * }}} 
  * 
  */
class ListView(
      title:       String = "List View"
     ,exitOnClose: Boolean = false
     ,visible:     Boolean = false

  ) extends javax.swing.JFrame {
  private val model  = new javax.swing.DefaultListModel[String]()
  private val jlist   = new javax.swing.JList(model)
  private val scroll = new javax.swing.JScrollPane(jlist)

  // This flag avoids firing the event when an item is removed.
  // it also allows temporarily disabling events.
  //
  private var selectionEventFlag = true

  init()

  private def init(){
    this.setTitle(title)
    this.add(scroll)
    this.setSize(300, 400)

    if (exitOnClose)
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
    

    this.setVisible(visible)
  }

  def getModel() = model

  def getJList() = jlist

  def getSelectedIndex() = jlist.getSelectedIndex()

  def getSelectedValue() = jlist.getSelectedValue()

  def addElement(elem: String) = model.addElement(elem)

  def addElements(elemList: Array[String])  = {
    elemList.foreach(model.addElement)
  }

  def clear() = {
    selectionEventFlag = false
    model.clear()
    selectionEventFlag = true 
  }
  

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
    model.removeElementAt(jlist.getSelectedIndex())
    selectionEventFlag = true 
  }

  /// Event that happens when user selects an item 
  def onSelect(handler: => Unit) = {
    val listener = new javax.swing.event.ListSelectionListener(){
      def valueChanged(args: javax.swing.event.ListSelectionEvent){
        if (selectionEventFlag) handler
      }
    }

    jlist.addListSelectionListener(listener)

    // Return function that removes listener
    () => { jlist.removeListSelectionListener(listener) }

  } // End of onSelect

} // ----- End of class ListView ------- // 


/** 
  * GUI for displaying text. 
  *
  * Relevant methods: 
  *
  *  -  append(text: String): Unit 
  *  -  appendLine(text: String): Unit 
  *  -  getText(): String 
  *  -  setText(text: String): Unit 
  *  -  clear(): Unit 
  *  -  setAutoScroll(flag: Boolean): Unit 
  *  -  scrollToBottom(): Unit 
  *  -  scrollToTop(): Unit 
  * 
  * Example:
  * 
  *  {{{
  *      val tarea = new jswing.guis.TextView(visible = true, autoScroll = true, exitOnClose = true)      
  *      val text = scala.io.Source.fromFile("/etc/protocols").mkString
  *      tarea.append(text)
  *  }}}
  *   
  * 
  * 
  * @param title        TextView window title 
  * @param exitOnclose  If true, ends the program execution when user closes the window.
  * @param visible      If true, makes the TextView visible when it is created.
  * @param autoScroll   If true, the TextView auto scrolls when new text is appended. 
  * @param editable     If true, makes the text entry editable.  
  * 
  * 
  */
class TextView(
      title:       String = "Text View"
     ,exitOnClose: Boolean = false
     ,visible:     Boolean = false
     ,autoScroll:  Boolean = false
     ,editable:    Boolean = true
    ) extends javax.swing.JFrame {

  private val tarea          = new javax.swing.JTextArea()
  private var autoScrollFlag = false

  init()

  private def init(){
    val scroll = new javax.swing.JScrollPane(tarea)
    this.setTitle(title)
    this.add(scroll)
    this.setSize(300, 400)

    if (exitOnClose)
    this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

    tarea.setEditable(editable)

    this.add(scroll)
    this.setVisible(visible)    

   
    autoScrollFlag = autoScroll
  }

  def getTextArea() = tarea

  def getText() = tarea.getText()

  def setText(text: String) = {
    tarea.setText(text)
    if (this.autoScroll) this.scrollToBottom()
  }

  /** Append text */
  def append(text: String) = {
    tarea.append(text)
    if (this.autoScrollFlag) this.scrollToBottom()    
  }

  /** Append new line to text area */
  def appendLine(text: String) = {
    tarea.append("\n" + text)
    if (this.autoScrollFlag) this.scrollToBottom()
  }

  /** Clear text area content */
  def clear() = tarea.setText("")

  /** When set to true the text area auto scroll every time a new text
    * is appended. 
    */ 
  def setAutoScroll(flag: Boolean) = { autoScrollFlag = flag }

  /** Scroll to bottom of text area */ 
  def scrollToBottom() = tarea.setCaretPosition(tarea.getDocument().getLength())

  /** Scroll to top of text area */
  def scrollToTop()    = tarea.setCaretPosition(0)

  def setEditable(flag: Boolean) = tarea.setEditable(flag)

  def setLineWrap(flag: Boolean) = tarea.setLineWrap(flag)

  def setTextColor(color: java.awt.Color) = tarea.setForeground(color)

  def setBgColor(color: java.awt.Color) = tarea.setBackground(color)

}
