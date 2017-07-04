import scala.xml._
import jswing.widgets.{Table, MTableModel}

/** Get exchange rates against U.S. dollar from European Central Bank */
def getExchangeRates() = {
  val xml = XML.load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
  val rates = (xml.child \\ "@rate").map(_.text.toDouble)
  val currencies = (xml.child \\ "@currency").map(_.text)
  val usdRate = currencies.zip(rates).find(t => t._1 == "USD").get._2

  currencies.zip(rates).map(t => (t._1, t._2 / usdRate))
}



def itemToCol(item: (String, Double), col: Int) = col match {
  case 0 => item._1.asInstanceOf[Object]
  case 1 => item._2.formatted("%.3f").asInstanceOf[Object]
  case _ => error("Error: Column number out of range")
}

val model = new MTableModel(
  columns   = Array("Currency", "Exchange Rate"),
  columnsFn = itemToCol,
  items     = getExchangeRates()
)

val table = new Table(
  model         = model,
  editable      = false,
  focusable     = false,
  cellSelection = false,
  showGrid      = false,
  headerColor   = java.awt.Color.cyan 
)

val frame = new javax.swing.JFrame("Exchange Rates")
frame.add(new javax.swing.JScrollPane(table))
frame.setSize(300, 400)
frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
frame.setVisible(true)
