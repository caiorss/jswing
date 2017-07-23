import javax.swing._
import scala.collection.mutable.ListBuffer

type G2D = java.awt.Graphics2D

/** Drawing Command executed by class Canvas paint method. */
type DrawCmd = java.awt.Graphics2D => Unit

type G = java.awt.Graphics

/** 2D Point */
type Point = (Double, Double)

type PointInt = (Int, Int)

case class DrawRange(pmin: (Double, Double), pmax: (Double, Double))

/** Strategy to set the origin of Canvas coordinate system */
abstract sealed class OriginType
case class OriginXY(x: Double, y: Double)  extends OriginType
case       object OriginBL                 extends OriginType
case       object OriginC                  extends OriginType


/** General drawing helper functions. */
object DrawUtils {

  def coordToScreen(origin: Point, point: Point) : Point = {
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

  def withContext(
    strokeSize: Double,
    color: java.awt.Color = null
  )(action: G2D => Unit) = (g: G2D) => {

    // save context
    val st  = g.getStroke()
    val col = g.getColor()
    val s = new java.awt.BasicStroke(strokeSize.toFloat)
      .asInstanceOf[java.awt.Stroke]

    g.setColor(color)
    g.setStroke(s)
    action(g)
    // restore context
    g.setColor(col)
    g.setStroke(st)
  }

  def withContextA(
    g: G2D,
    strokeSize: Double,
    color: java.awt.Color = null
  )(action:  => Unit) = {
    // save context
    val st  = g.getStroke()
    val col = g.getColor()
    val s = new java.awt.BasicStroke(strokeSize.toFloat)
      .asInstanceOf[java.awt.Stroke]

    g.setColor(color)
    g.setStroke(s)
    action
    // restore context
    g.setColor(col)
    g.setStroke(st)
  }

  def withRoationA(g: G2D, x: Int, y: Int, angle: Double)(action: => Unit) =  {
    val angleRad = Math.toRadians(angle)
    val t = g.getTransform()
    g.rotate(angleRad, x, y)
    action
    g.setTransform(t)
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

  /** Get font size in pixels */
  def getTextSize(g: G2D, text: String): (Int, Int) = {
    val font = g.getFont()
    val fm   = font.getStringBounds(text, g.getFontRenderContext())
    val w    = fm.getWidth()
    val h    = fm.getHeight()
    (w.toInt, h.toInt)
  }

} // ----- End of module DrawUtils --------------- //



class DrawCtx(comp: java.awt.Component, offs: Int = 0){
  // Border offset from left and top
  private var offset = offs

  private var marginL = 30  // Left  margin
  private var marginR = 40  // Right margin
  private var marginT = 20  // Top margin
  private var marginB = 20  // Bottom margin

  // Origin of chart - Relative to the bottom left of drawing screen  
  private var origin: OriginType = OriginBL

  // Range to plot
  private var pmin = (-50.0, -50.0)
  private var pmax = (50.0, 50.0)
  private var step = 0.1

  private var pointMarkSize: Int = 3
  private var pointMarkColor = java.awt.Color.BLUE


  def setMargins(left: Int, right: Int, top: Int, bottom: Int) = {
    marginL = left
    marginR = right
    marginT = top
    marginB = bottom
  }

  def setMarginLeft(size: Int)    = { marginL = size }
  def setMarginRight(size: Int)   = { marginR = size }
  def setMarginTop(size: Int)     = { marginT = size }
  def setMarginBottom(size: Int)  = { marginB = size }


  // def setGraphics(graphics: java.awt.Graphics) = { g = graphics}
  def setOffset(offs: Int){
    offset = offs
  }

  def getOffset() = offset

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
     Get coordinates of bottom left screen in screen coordinates (upper left) corner.
   */
  def getCoordBottomLeft() = {
    val w   = comp.getWidth().toDouble
    val h   = comp.getSize().height.toDouble
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

  /** Get origin coordinate relative to top left corner of screen */
  def getOriginCoord() = origin match {

    // Origin specified by user
    case OriginXY(xo, yo)
        => (xo, yo)

    // Origin at screen's bottom left.
    case OriginBL
        => {
          val xo = 0.0
          val yo = comp.getHeight().toDouble
          (xo, yo)
        }

    // Origin at screen's center
    case OriginC
        => {
          val xo = comp.getWidth().toDouble  / 2
          val yo = comp.getHeight().toDouble / 2
          (xo, yo)
        }
  }


  /**  Converts coordinate centered at some origin (x - positive from left to right, y - positive upward)
       to screen's coordinates with origin at top left and  (x - positive from left to right and y
       positive downward.
    */
  def coordOriginToScreen(p: Point) = {
    val o = this.getOriginCoord()
    DrawUtils.coordToScreen(o, p)
  }
  
  /** Converts coordinate with origin at bottom left to screen's coordinate */
  def coordBottomToScreen(p: PointInt): PointInt =  {
    val w   = comp.getSize().width
    val h   = comp.getSize().height 
    (p._1 + offset, -1 * p._2 + h - offset)
  }

  def coordRealToScreen(p: Point) = {
    val (xRmin, yRmin) = this.pmin
    val (xRmax, yRmax) = this.pmax

    val w = comp.getWidth()
    val h = comp.getHeight()

    val sx = (w - this.marginL - this.marginR).toDouble / ( xRmax - xRmin)
    val kx = - sx * xRmin + marginL

    val sy = - (h - this.marginT - this.marginB).toDouble / (yRmax - yRmin)
    val ky = h - sy * yRmin - marginB

    val x = sx * p._1 + kx
    val y = sy * p._2 + ky
    (x, y)
  }

  def coordScreenToReal(p: PointInt) = {
    val (xRmin, yRmin) = this.pmin
    val (xRmax, yRmax) = this.pmax

    val w = comp.getWidth()
    val h = comp.getHeight()

    val sx = (w - this.marginL - this.marginR).toDouble / ( xRmax - xRmin)
    val kx = - sx * xRmin + marginL

    val sy = - (h - this.marginT - this.marginB).toDouble / (yRmax - yRmin)
    val ky = h - sy * yRmin - marginB

    val x = (p._1 - kx) / sx
    val y = (p._2 - ky) / sy
    (x, y)
  }


  def coordRangeToScreen(p: Point) = this.coordRealToScreen(p)
  // def coordRangeToScreen = (p: Point) => {
  //   val origin = this.getCoordBottomLeft()
  //   val size   = (comp.getWidth(), comp.getHeight())
  //   DrawUtils.coordRangeToScreen(this.pmin, this.pmax, origin, size, offset)(p._1, p._2)
  // }



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
  

  def plotString(
    p: Point,
    msg: String,
    offsetXY: (Int, Int) = (0, 0)
  ) = (g: G2D) => {

    val (x, y) = this.coordRangeToScreen(p)
    g.drawString(msg, x + offsetXY._1, y + offsetXY._2)
  }

  /**
     Plot a string at point (X, Y) in user coordinates
     rotating around this point by an angle in degrees.
    */
  def plotStringAngle(
    p: Point,
    text: String,
    angle: Double,
    offsetXY: (Int, Int) = (0, 0)
  ) = (g: G2D) => {
    val angleRad = Math.toRadians(angle)
    // Save context
    val t = g.getTransform()
    // Get screen coordinates
    val (x, y) = this.coordRangeToScreen(p)
    val xx = x + offsetXY._1
    val yy = y + offsetXY._2
    // Rotate around the (x, y) point
    g.rotate(angleRad, xx, yy)
    g.drawString(text, xx.toFloat, yy.toFloat)
    // Restore context
    g.setTransform(t)
  }

  def plotPoint(x: Double, y: Double) = (g: G2D) => {
    val radius = this.pointMarkSize
    val (xx, yy) = this.coordRangeToScreen((x, y))
    g.drawString(s"(${x}, ${y})", xx.toFloat, yy.toFloat)

    val col = g.getColor()
    g.setColor(this.pointMarkColor)
    //g.fillOval(xx - radius, yy - radius, 2 * radius, 2 * radius)
    g.fillOval(
      (xx - radius).toInt,
      (yy - radius).toInt,
      (2 * radius).toInt,
      (2 * radius).toInt
    )
    g.setColor(col)
  }

  def plotPointList(plist: Seq[(Double, Double)]) = (g: G2D) => {
    val radius = this.pointMarkSize
    for ((x, y) <- plist) {
      val (xx, yy) = this.coordRangeToScreen((x, y))
      g.drawString(s"(${x}, ${y})", xx.toFloat, yy.toFloat)
      val col = g.getColor()
      g.setColor(this.pointMarkColor)

      g.fillOval(
        (xx - radius).toInt,
        (yy - radius).toInt,
        (2 * radius).toInt,
        (2 * radius).toInt
      )

      g.setColor(col)
    }
  }

  def plotHLine(y: Double) = (g: G2D) => {
    val (_, yy) = this.coordRangeToScreen((0.0, y))

    val line = new java.awt.geom.Line2D.Double(
      marginB, yy, comp.getWidth() - marginB - marginR, yy
    )
    g.draw(line)
    //g.drawLine(offset, yy, comp.getWidth() - offset, yy)
  }

  def plotVLine(x: Double) = (g: G2D) => {
    val (xx, _) = this.coordRangeToScreen((x, 0.0))
    val line = new java.awt.geom.Line2D.Double(
      xx, marginT, xx, comp.getHeight() - marginB
    )
    g.draw(line)
    //g.drawLine(xx, offset, xx, comp.getHeight() - offset)
  }

  def plotHVLine(x: Double, y: Double) = (g: G2D) => {
    plotVLine(x: Double)(g)
    plotHLine(y: Double)(g)
  }


  def plotAxisLines = (g: G2D) => {
    val (xmin, ymin) = this.pmin
    val (xmax, ymax) = this.pmax

    val ticks = 10

    var y    = ymin
    var ystep = (ymax - ymin) / ticks.toDouble


    while (y <= ymax) {
        DrawUtils.withContextA(g, 0.5, java.awt.Color.GRAY){
          this.plotHLine(y)(g)
        }

        this.plotString((xmin, y), "%.2f".format(y), offsetXY = (-40, 0))(g)
        y = y + ystep
      }

    var x    = xmin
    var xstep = (xmax - xmin) / ticks.toDouble


    while (x <= xmax) {
      DrawUtils.withContextA(g, 0.5, java.awt.Color.GRAY){
        this.plotVLine(x)(g)
      }

      //this.plotString((x, ymin), "%.2f".format(x))(g)

      this.plotStringAngle((x, ymin), 90.0, "%.2f".format(x), offsetXY = (-20, 10))(g)

      // DrawUtils.withRoationA(g, 90.0){
      //   this.plotString((x, ymin), "%.2f".format(x))(g)
      // }

        x = x + xstep
    }


    // Draw axis at bottom left
    //
    val xo = offset
    val yo = comp.getHeight() - offset

    DrawUtils.withContextA(g, 1.5, java.awt.Color.BLUE){
      // draw horizontal line
      g.drawLine(offset, yo, comp.getWidth() - offset, yo)

      // draw vertical line
      g.drawLine(offset, offset, offset, yo)
    }
  }


  def drawPlotArea(g: G2D) {
    // @TODO: Change hardcoded color
    //val col = g.getColor()
    g.setColor(java.awt.Color.GREEN)
    g.fillRect(0, 0, comp.getWidth(), comp.getHeight())

    g.setColor(java.awt.Color.WHITE)
    g.fillRect(
      this.marginR,  // x from top
      this.marginT,  // y
      comp.getWidth() - this.marginR - this.marginL,
      comp.getHeight() - this.marginB - this.marginT
    )
    g.setColor(java.awt.Color.BLACK)
  }


  def drawLine(pMin: Point,pMax: Point) = (g: G2D) => {
    val (xmin, ymin) = pMin
    val (xmax, ymax) = pMax
    val (xsMin, ysMin) = this.coordOriginToScreen(xmin, ymin)
    val (xsMax, ysMax) = this.coordOriginToScreen(xmax, ymax)

    val shape = new java.awt.geom.Line2D.Double(xsMin, ysMin, xsMax, ysMax)
    g.draw(shape)
    //g.drawLine(xsMin.toFloat, ysMin.toFloat, xsMax.toFloat, ysMax.toFloat)
  }

  def drawLine2(xmin: Int, ymin: Int, xmax: Int, ymax: Int) = (g: G2D) => {
    val (xsMin, ysMin) = this.coordOriginToScreen(xmin, ymin)
    val (xsMax, ysMax) = this.coordOriginToScreen(xmax, ymax)
    val shape = new java.awt.geom.Line2D.Double(xsMin, ysMin, xsMax, ysMax)
    g.draw(shape)
    //g.drawLine(xsMin, ysMin, xsMax, ysMax)
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

  def drawLineAngle(p: Point, radius: Double, angleDeg: Double) = {
    val ang = angleDeg / 180.0 * Math.PI
    val xmax = p._1 + radius * Math.cos(ang)
    val ymax = p._1 + radius * Math.sin(ang)
    this.drawLine(p, (xmax, ymax))
  }

  def drawString(p: Point, msg: String) = (g: G2D) => {
    val (x, y) = this.coordOriginToScreen(p)
    g.drawString(msg, x.toFloat, y.toFloat)
  }

  def drawString2(x: Double, y: Double, msg: String) = (g: G2D) => {
    val (xs, ys) = this.coordOriginToScreen((x, y))
    g.drawString(msg, xs.toFloat, ys.toFloat)
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
  private var bgColor  = java.awt.Color.WHITE
  private var fgColor  = java.awt.Color.BLACK

  private var plotColor = java.awt.Color.BLACK

  private var autoRepaint = true
  private val ctx      = new DrawCtx(this, 20)

  private val drawCmdList =  ListBuffer[G2D => Unit]()


  private def init(){

  }

  def setAutoRepaint(flag: Boolean){
    autoRepaint = flag
  }

  def getDrawCtx() = ctx

  /** Refresh screen if the private autoRepaint parameter is true. */
  def refresh() {
    if (this.autoRepaint) this.repaint()
  }

  def drawList(draws: (G2D => Unit)*) = {
    drawCmdList appendAll draws
    this.refresh()
  }

  def draw(draw: G2D => Unit) = {
    drawCmdList += draw
    this.refresh()
  }

  /** Clear canvas */
  def clear() = {
    drawCmdList.clear()
    this.repaint()
  }

  def undo() = if (drawCmdList.size > 0) {
    drawCmdList.remove(drawCmdList.size - 1)
    this.refresh()
  }

  /** Set default plot color */
  def setPlotColor(color: java.awt.Color){
    plotColor = color
  }

  def setOffset(offset: Int) = {
    this.ctx.setOffset(offset)
    this.refresh()
  }

  def setRange(xmin: Double, ymin: Double, xmax: Double, ymax: Double) {
    ctx.setRange(xmin, ymin, xmax, ymax)
    this.repaint()
  }


  override def paint(g: java.awt.Graphics){
    val offset = this.ctx.getOffset()

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

    g2d.setRenderingHint(
      java.awt.RenderingHints.KEY_ANTIALIASING,
      java.awt.RenderingHints.VALUE_ANTIALIAS_ON
    )


    drawCmdList foreach {cmd => cmd(g2d)}

  } // -------- End of paint() -------------- //


  def setBgColor(col: java.awt.Color) = {
    bgColor = col
    this.repaint()
  }

  def setFgColor(col: java.awt.Color) = {
    fgColor = col
    this.repaint()
  }


  def plotFun(
    fn: Double => Double,
    color: java.awt.Color = null
  ){
    val col = if (color == null) plotColor else color
    val cmd = DrawUtils.withColor(col){ ctx.plotFun(fn) }
    this.draw(cmd)
  }

  def plotFunRange(
    fn: Double => Double,
    xmin: Double = -10.0,
    xmax: Double = 10.0,
    step: Double = 0.1,
    autoRange: Boolean = true,
    color: java.awt.Color = null
  ) = {
    val col = if (color == null) plotColor else color
    val cmd = DrawUtils.withColor(col){
      ctx.plotFunRange(fn, xmin, xmax, step, autoRange)
    }
    this.draw(cmd)
    this.refresh()
  }

  def plotPoint(x: Double, y: Double){
    this.draw(ctx.plotPoint(x, y))
  }

  def plotPointList(plist: Seq[Point]){
    this.draw(ctx.plotPointList(plist))
  }

  def plotHLine(y: Double){
    this.draw(ctx.plotHLine(y))
  }

  def plotVLine(x: Double){
    this.draw(ctx.plotVLine(x))
  }

  def plotHVLine(x: Double, y: Double){
    this.draw(ctx.plotHVLine(x, y))
  }

  def getImage() = {
    val image = new java.awt.image.BufferedImage(
      this.getWidth(),
      this.getHeight(),
      java.awt.image.BufferedImage.TYPE_INT_RGB
    )
    this.paint(image.getGraphics())
    image
  }

  def saveImage(file: String) = {
    val img = this.getImage()
    javax.imageio.ImageIO.write(img, "png", new java.io.File(file))
  }
  
  
} // ----- End of class Canvas -------- //


