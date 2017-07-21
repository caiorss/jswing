import javax.swing._
import scala.collection.mutable.ListBuffer

type G2D = java.awt.Graphics2D

type DrawCmd = java.awt.Graphics2D => Unit 

type G = java.awt.Graphics

type Point = (Double, Double)

type PointInt = (Int, Int)

case class DrawRange(pmin: (Double, Double), pmax: (Double, Double))

abstract sealed class OriginType
case class OriginXY(x: Int, y: Int)  extends OriginType
case       object OriginBL           extends OriginType
case       object OriginC            extends OriginType


object DrawUtils {


  def coordToScreen(origin: PointInt, point: PointInt) = { 
    (point._1 + origin._1, -1 * point._2 + origin._2)
  }



  def coordRangeToScreen(
    pmin:   Point,  // Point min (xmin, ymin) of real coordinates
    pmax:   Point,  // Point max (xmax, ymax) of real coordinates
    origin: (Int, Int),        // Origin position at the screen relative to the top
    size:   (Int, Int),        // Screen size - width x height
    offset: Int
  ) = {

    val (xrmin, yrmin) = pmin
    val (xrmax, yrmax) = pmax

    val w = size._1
    val h = size._2

    val (xo, yo) = origin

    val ax = (w.toDouble - 2 * offset) / (xrmax - xrmin)
    val ay = (h.toDouble - 2 * offset) / (yrmax - yrmin)


    (p: Point) => {

      val (xr, yr) = p

      // val x =  ax * (xr - xrmin) + xo
      // val y =  h + yo - (ay * (yr - yrmin))
      val xx = ax * (xr - xrmin)
      val yy = ay * (yr - yrmin)

      val x = xx  + xo
      val y =  yo - yy

      // println(s"===> size = ${size} pmin = ${pmin} pmax = ${pmax}")
      // println(s"===> xr = ${xr} yr = ${yr} / xx = ${xx} yy = ${yy} x = ${x} y = ${y}" )
      // println(s"===> h = ${h}  w = ${w}    / ax = ${ax} ay = ${ay} / xo = ${xo} yo = ${yo}")
      // println("-----------------------------------------------------")

      (x.toInt, y.toInt)
    }
  }




  def forAngleStep(step: Double, angleDraw: Double => G2D => Unit) = (g: G2D) => {
    var angle = 0.0
    while (angle <= 360){
      angleDraw(angle)(g)
      angle = angle + step
    }
  }

  def withColor(color: java.awt.Color) = (action: G2D => Unit) => 
    (g: G2D) => { 
      val col = g.getColor()
      g.setColor(color)
      action(g)
      g.setColor(col)
    }


  /**
      Find the minimum and maximum points of a function in a given range
      by iterating over all possible discrete points in this range.

      @param xmin - Minimum value x in the interval
      @param ymin - Maximum value x in the interval
      @param step - Step size that the interval will be iterated.
      @param fn   - Function that will be iterated in the interval.
      @return - Minimum and maximum value of the fuction.
    */
  def findYbounds(
    xmin: Double,
    xmax: Double,
    step: Double = 0.001,
    fn:   Double => Double
  ) = {

    assert(xmax >= xmin, "It should be xmax > xmin")
    assert(step > 0, "It should be step > 0")

    var ymin = Double.PositiveInfinity
    var ymax = Double.NegativeInfinity
    var x = xmin
    var y = 0.0

    while (x <= xmax) {
      y = fn(x)

      if (y < ymin) { ymin = y}
      if (y > ymax) { ymax = y}
      x = x + step
    }
    (ymin, ymax)

  } // ---- End of findYbounds ------ /


} // ----- End of module DrawUtils --------------- //



class DrawCtx(comp: java.awt.Component, offs: Int = 0){
  // Border offset from left and top
  private var offset = offs

  // Origin of chart - Relative to the bottom left of drawing screen  
  private var origin: OriginType = OriginBL

  // Range to plot
  private var pmin = (-50.0, -50.0)
  private var pmax = (50.0, 50.0)
  private var step = 0.1

  private var pointMarkSize: Int = 3
  private var pointMarkColor = java.awt.Color.BLUE

  // def setGraphics(graphics: java.awt.Graphics) = { g = graphics}
  def setOffset(offs: Int){
    offset = offs
  }

  def getOrigin() = origin

  def setOrigin(o: OriginType) = {
    origin = o
  }

  def setOriginCoord(x: Int, y: Int) = {
    origin = OriginXY(x, y)
  }

  /** Set origin of screen coordinate system at its bottom left */
  def setOriginBottomLeft() = {
    origin = OriginBL
  }

  /** Set origin of screen coordinate system at its center */
  def setOriginCenter() = {
    origin = OriginC
  }

  def setRange(xmin: Double, ymin: Double, xmax: Double, ymax: Double) {
    pmin = (xmin, ymin)
    pmax = (xmax,  ymax)
  }

  // Get coordinate of bottom left screen
  /**
     Get coordinates of bottom left screen in screen coordinates (upper left) corner. */
  def getCoordBottomLeft() = {
    val w   = comp.getWidth()
    val h   = comp.getSize().height 
    (offset, h -  offset)
  }

  /**
      Get coordinates of center of screen in screen coordinates (upper left) corner.
    */
  def getCoordCenter() = {
    val x = comp.getWidth()  / 2 
    val y = comp.getHeight() / 2  
    (x, y)
  }


  def getSize() = {
    val w = comp.getSize().width  - 2 * offset
    val h = comp.getSize().height - 2 * offset
    (w, h)
  }

  def getHeight() = comp.getHeight()

  def getWidth()  = comp.getWidth()


  def getOriginCoord() = origin match {
    case OriginXY(xo, yo)
        => (xo, yo)

    case OriginBL
        => {
          val xo = offset
          val yo = comp.getHeight() - offset
          (xo, yo)
        }
    case OriginC
        => {
          val xo = comp.getWidth()  / 2
          val yo = comp.getHeight() / 2
          (xo, yo)
        }
  }


  /**
       Converts coordinate centered at some origin (x - positive from left to right, y - positive upward)
       to screen's coordinates with origin at top left and  (x - positive from left to right and y
       positive downward.

    */
  def coordOriginToScreen(p: PointInt): PointInt = {
    val o = this.getOriginCoord()
    DrawUtils.coordToScreen(o, p)
  }
  
  /** Converts coordinate with origin at bottom left to screen's coordinate */
  def coordBottomToScreen(p: PointInt): PointInt =  {
    val w   = comp.getSize().width
    val h   = comp.getSize().height 
    (p._1 + offset, -1 * p._2 + h - offset)
  }

  def coordRangeToScreen = (p: Point) => {
    val origin = this.getCoordBottomLeft()
    val size   = (comp.getWidth(), comp.getHeight())
    DrawUtils.coordRangeToScreen(this.pmin, this.pmax, origin, size, offset)(p._1, p._2)
  }

  def coordRangeToScreen2(pmin: Point, pmax: Point) = {
    val origin = this.getCoordBottomLeft()
    val size   = (comp.getWidth(), comp.getHeight())
    DrawUtils.coordRangeToScreen(pmin, pmax, origin, size, offset)
  }


  /** Plot a function setting the range automatically. */
  def plotFunRange(
    fn: Double => Double,
    xmin: Double = -10.0,
    xmax: Double = 10.0,
    step: Double = 0.1,
    autoRange: Boolean = false
  ) = {

    assert(step > 0,    "It should be step > 0")
    assert(xmax > xmin, "It should be xmax > xmin")

    val (ymin, ymax) = DrawUtils.findYbounds(xmin, xmax, step, fn)

    if (autoRange) this.setRange(xmin, ymin, xmax , ymax)

    (g: G2D) => {
      var x  = xmin
      var p1 = this.coordRangeToScreen((x, fn(x)))
      var p2 = this.coordRangeToScreen((x + step, fn(x + step)))

      while (x < xmax) {
        //println(s"p1 = ${p1} p2 = ${p2}")

        g.drawLine(p1._1, p1._2, p2._1, p2._2)
        x = x + step
        //println(s"x = ${x} y = ${fn(x)}")

        p1 = p2
        p2 = this.coordRangeToScreen((x + step, fn(x + step)))
      }
    }
  }


  def plotFun(fn: Double => Double) = {
    val (xmin, ymin) = this.pmin
    val (xmax, ymax) = this.pmax
    plotFunRange(fn, xmin, xmax, this.step, false)
  }
  


  def plotString(p: Point, msg: String) = (g: G2D) => {
    val (x, y) = this.coordRangeToScreen(p)
    g.drawString(msg, x, y)
  }

  def plotPoint(x: Double, y: Double) = (g: G2D) => {
    val radius = this.pointMarkSize
    val (xx, yy) = this.coordRangeToScreen((x, y))
    g.drawString(s"(${x}, ${y})", xx, yy)

    val col = g.getColor()
    g.setColor(this.pointMarkColor)
    g.fillOval(xx - radius, yy - radius, 2 * radius, 2 * radius)
    g.setColor(col)
  }

  def plotHLine(y: Double) = (g: G2D) => {
    val (_, yy) = this.coordRangeToScreen((0.0, y))
    g.drawLine(offset, yy, comp.getWidth() - offset, yy)
  }

  def plotVLine(x: Double) = (g: G2D) => {
    val (xx, _) = this.coordRangeToScreen((x, 0.0))
    g.drawLine(xx, offset, xx, comp.getHeight() - offset)
  }

  def plotHVLine(x: Double, y: Double) = (g: G2D) => {
    plotVLine(x: Double)(g)
    plotHLine(y: Double)(g)
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
    //println(s"Draw String s = '${msg}'\t\t\tx = ${x} y = ${y} ")
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



class Canvas extends JPanel {
  private var offset   = 10
  private var bgColor  = java.awt.Color.WHITE
  private var fgColor  = java.awt.Color.BLACK

  private var plotColor = java.awt.Color.BLACK

  private var autoRepaint = true
  private val ctx      = new DrawCtx(this, offset)

  private val drawCmdList =  ListBuffer[G2D => Unit]()


  private def init(){

  }

  def setAutoRepaint(flag: Boolean){
    autoRepaint = flag
  }

  def getDrawContext() = ctx

  /** Refresh screen if the private autoRepaint parameter is true. */
  def refresh() {
    if (this.autoRepaint) this.repaint()
  }

  def drawList(draws: (G2D => Unit)*) = {
    drawCmdList appendAll draws
    this.repaint()
  }

  def draw(draw: G2D => Unit) = {
    drawCmdList += draw
    this.repaint()
  }

  def clear() = {
    drawCmdList.clear()
    this.repaint()
  }


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
  
} // ----- End of class Canvas -------- //


