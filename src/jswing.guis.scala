/** 
  * GUI Building blocks for Java Swing GUI toolkit.
  * 
  *  This package provides GUI building blocks simple 
  *  and ready to use such as GUI to display list data, 
  *  display tables and pictures.
  *  
  */
package jswing.guis

import jswing.Widgets

/**  Simple GUI building-block for displaying pictures. 
 */     
class PictureFrame(
      title:       String   = "Picture Box"
     ,exitOnClose: Boolean  = false
     ,visible:     Boolean  = false
     ,size:      (Int, Int) = (300, 400)
) extends javax.swing.JFrame {
  private val pbox = new Widgets.PictureBox()

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

} // End of class PictureFrame //



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

  def init(){
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



