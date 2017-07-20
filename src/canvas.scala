import javax.swing._
import scala.collection.mutable.ListBuffer

type G2D = java.awt.Graphics2D

type DrawCmd = java.awt.Graphics2D => Unit 

type G = java.awt.Graphics

type PointInt = (Int, Int)


object DrawUtils {


  def coordToScreen(origin: PointInt, point: PointInt) = { 
    (point._1 + origin._1, -1 * point._2 + origin._2)
  }

  def withColor(color: java.awt.Color) = (action: G2D => Unit) => 
    (g: G2D) => { 
      val col = g.getColor()
      g.setColor(color)
      action(g)
      g.setColor(col)
    }

}

abstract sealed class OriginType
case class OriginXY(x: Int, y: Int)  extends OriginType
case       object OriginBL           extends OriginType
case       object OriginC            extends OriginType


class DrawParams(comp: java.awt.Component, offs: Int = 0){
  private var offset = offs

  // Origin of chart - Relative to the bottom left of drawing screen  
  private var origin: OriginType = OriginBL


  def getOrigin() = origin

  def setOrigin(o: OriginType) = {
    origin = o
  }

  // Get coordinate of bottom left screen 
  def getCoordBottomLeft() = {
    val w   = comp.getWidth()
    val h   = comp.getSize().height 
    (offset, h -  offset)
  }

  def getOriginToCenter() = {
    val x = comp.getWidth()  / 2 
    val y = comp.getHeight() / 2  
    (x, y)
  }

  // def setGraphics(graphics: java.awt.Graphics) = { g = graphics}
  def setOffset(offs: Int){
    offset = offs
  }

  def getSize() = {
    val w = comp.getSize().width  - 2 * offset
    val h = comp.getSize().height - 2 * offset
    (w, h)
  }

  def getHeight() = comp.getHeight()

  def getWidth()  = comp.getWidth()


  /** 
       Converts coordinate centered at some origin (x - positive from left to right, y - positive upward) 
       to screen's coordinates with origin at top left and  (x - positive from left to right and y 
       positive downward.

    */
  def coordOriginToScreen(p: PointInt): PointInt = origin match { 
    case OriginXY(xo, yo)
        => DrawUtils.coordToScreen((xo, yo), p)

    case OriginBL
        => {
          val xo = offset
          val yo = comp.getHeight() - offset
          DrawUtils.coordToScreen((xo, yo), p)
        }
    case OriginC
        => {
          val xo = comp.getWidth()  / 2
          val yo = comp.getHeight() / 2
          DrawUtils.coordToScreen((xo, yo), p)
        }
  }
  
  /** Converts coordinate with origin at bottom left to screen's coordinate */
  def coordBottomToScreen(p: PointInt): PointInt =  {
    val w   = comp.getSize().width
    val h   = comp.getSize().height 
    (p._1 + offset, -1 * p._2 + h - offset)
  }  
  

  def drawLine(pMin: PointInt,pMax: PointInt) = (g: G2D) => {
    val (xmin, ymin) = pMin
    val (xmax, ymax) = pMax
    val (xsMin, ysMin) = this.coordOriginToScreen(xmin, ymin)
    val (xsMax, ysMax) = this.coordOriginToScreen(xmax, ymax)
    g.drawLine(xsMin, ysMin, xsMax, ysMax)
  }

  def drawLine2(xmin: Int, ymin: Int, xmax: Int, ymax: Int) = (g: G2D) => {
    val (xsMin, ysMin) = this.coordOriginToScreen(xmin, ymin)
    val (xsMax, ysMax) = this.coordOriginToScreen(xmax, ymax)
    g.drawLine(xsMin, ysMin, xsMax, ysMax)
  }

  def drawHLine(y: Int) = (g: G2D) => {    
    drawLine2(0, y, comp.getWidth() - 2 * offset, y)(g)
  }

  def drawVLine(x: Int) = (g: G2D) => {
    drawLine2(x, 0, x, comp.getHeight() - 2 * offset)(g)
  }

  /** Draw horizontal line at screen's center from screen's left border to the right border. 
      Note: this command is independent from the current screen coordinate. 
   */
  def drawHLineCenter(g: G2D){
    val y = comp.getHeight() / 2 
    g.drawLine(offset, y, comp.getWidth() - offset, y)
  }

  def drawVLineCenter(g: G2D) {
    val x = comp.getWidth()  / 2 
    g.drawLine(x, offset, x, comp.getHeight() - offset)
  }

  def drawCenterLines(g: G2D){
    drawHLineCenter(g)
    drawVLineCenter(g)
  }

  def drawLineAngle(p: PointInt, radius: Double, angleDeg: Double) = {
    val ang = angleDeg / 180.0 * Math.PI
    val xmax = p._1 + radius * Math.cos(ang)
    val ymax = p._1 + radius * Math.sin(ang)
    this.drawLine(p, (xmax.toInt, ymax.toInt))
  }

  def drawString(p: PointInt, msg: String) = (g: G2D) => {
    val (x, y) = this.coordOriginToScreen(p)
    println(s"Draw String s = '${msg}'\t\t\tx = ${x} y = ${y} ")
    g.drawString(msg, x, y)
  }

  def drawString2(x: Int, y: Int, msg: String) = (g: G2D) => {
    val (xs, ys) = this.coordOriginToScreen((x, y))
    g.drawString(msg, xs, ys)
  }


  /** 
      Place a string with point coordinates at some (x, y) position.
      It is useful to debugging. 
    */
  def pointMark(p: PointInt, label: String = "") = 
    this.drawString((p._1 + 5, p._2 + 5), s"${label} (x = ${p._1}, y = ${p._2})")


  def pointMark2(x: Int, y: Int, label: String = "") =  
    this.pointMark((x, y), label)      


  def drawCircle(p: PointInt, radius: Int) = (g: G2D) => {
    val (xs, ys) = this.coordOriginToScreen(p._1 - radius, p._2 + radius)
    g.drawOval(xs, ys, 2 * radius, 2 * radius)
  }

  def drawCircle2(x: Int, y: Int, radius: Int) = 
    this.drawCircle((x, y), radius)

  def drawRect(p: PointInt, width: Int, height: Int) = (g: G2D) => {
    val (x, y) = this.coordOriginToScreen(p._1 - width / 2, p._2 + height / 2)
    g.drawRect(x, y, width, height)
  }

  def fillCircle(p: PointInt, radius: Int) = (g: G2D) => {
    val (xs, ys) = this.coordOriginToScreen(p._1 - radius, p._2 + radius)    
    g.fillOval(xs, ys, 2 * radius, 2 * radius)
  }

  def fillRect(p: PointInt, width: Int, height: Int) = (g: G2D) => {
    val (x, y) = this.coordOriginToScreen(p._1 - width / 2, p._2 + height / 2)
    g.fillRect(x, y, width, height)
  }

}


/** Set drawing coordinates at bottom of screen instead of the top */
def setCoordBottom(height: Int, offset: Int, g: G2D) = {
  //val at = new java.awt.geom.AffineTransform()
  //val at = g2d.getTransform()
  //at.setToScale(1.0, -1.0)
  //at.setToTranslation(offset, height - offset)
  //g.transform(at)

  g.translate(offset, height - offset)
  g.scale(1, -1)
}


class Canvas extends JPanel {
  private var offset   = 10
  private var bgColor  = java.awt.Color.WHITE
  private var fgColor  = java.awt.Color.BLACK

  private val drawCmdList =  ListBuffer[G2D => Unit]()

  def draw(draw: G2D => Unit) = {
    drawCmdList += draw
    this.repaint()
  }

  def drawList(draws: (G2D => Unit)*) = {
    drawCmdList appendAll draws
    this.repaint()
  }

  def clear() = {
    drawCmdList.clear()
    this.repaint()
  }

  private def init(){

  }


  override def paint(g: java.awt.Graphics){  

    // Set background color 
    g.setColor(bgColor)        
    g.fillRect(
      offset,
      offset,
      this.getSize().width  - 2 * offset - 1,
      this.getSize().height - 2 * offset -1
    )

    // Set foreground color 
    g.setColor(fgColor)    
    val g2d = g.asInstanceOf[java.awt.Graphics2D]

    drawCmdList foreach {cmd => cmd(g2d)}

  } // -------- End of paint() -------------- //

  def getScreenSize() = {
    val w = this.getSize().width  - 2 * offset
    val h = this.getSize().height - 2 * offset
    (w, h)
  }

  def setBgColor(col: java.awt.Color) = {
    bgColor = col
    this.repaint()
  }

  def setFgColor(col: java.awt.Color) = {
    fgColor = col
    this.repaint()
  }

  def coord2BottomScreen(x: Int, y: Int) = {
    val h   = this.getSize().height
    (x + offset, h - y - offset)
  }

}



val canvas = new Canvas() 

val frame = new JFrame("Canvas App")
frame.setSize(500, 400)
frame.add(canvas)
frame.setVisible(true)


val g= canvas.getGraphics()
val g2 = g.asInstanceOf[java.awt.Graphics2D]

val gfx = new DrawParams(canvas, offs = 10)
gfx.setOrigin(OriginBL)

val withBlue = DrawUtils.withColor(java.awt.Color.BLUE)
val withGreen = DrawUtils.withColor(java.awt.Color.GREEN)

canvas.drawList(
  gfx.pointMark2(0, 0),
  gfx.pointMark2(10, 10),
  gfx.pointMark((100, 200)),

  withGreen{ gfx.drawLine((0, 0), (100, 200)) },

  withBlue{ gfx.fillCircle((200, 300), 40)},

  DrawUtils.withColor(java.awt.Color.GREEN){ gfx.fillRect((100, 200), 50, 150) },

  withBlue{ gfx.drawCenterLines },

  gfx.drawRect((100, 200), 100, 200)  


)
  



//canvas.draw{ gw => gw.transform(at)  }

// canvas.draw{ gw => gw.drawOval(100, 100, 100, 100)}
// canvas.draw(drawString("Hello world", 10, 10))

// canvas.draw(gfx.drawLineTo(0, 0, 100, 200))

// canvas.draw(gfx.drawPointMark(0, 0))
// canvas.draw(gfx.drawPointMark(100, 200))
// canvas.draw(gfx.drawCircle(100, 200, 50))

// canvas.draw(gfx.fillCircle(100, 200, 15, java.awt.Color.RED))
