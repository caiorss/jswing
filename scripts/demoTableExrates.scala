import scala.xml._
import jswing.widgets.{Table, MTableModel}
import jswing.Event
import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global


type RowType = (String, Double)

/** Get exchange rates against U.S. dollar from European Central Bank */
def getExchangeRates() = {
  val xml = XML.load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
  val rates = (xml.child \\ "@rate").map(_.text.toDouble)
  val currencies = (xml.child \\ "@currency").map(_.text)
  val usdRate = currencies.zip(rates).find(t => t._1 == "USD").get._2
  currencies.zip(rates).map(t => (t._1, t._2 / usdRate))
}


def itemToCol(item: RowType, col: Int) = col match {
  case 0 => item._1.asInstanceOf[Object]
  case 1 => item._2.formatted("%.3f").asInstanceOf[Object]
  case _ => error("Error: Column number out of range")
}

val model = new MTableModel[RowType](
  columns   = Array("Currency", "Exchange Rate"),
  columnsFn = itemToCol
  //items     = getExchangeRates()
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
val panel = new javax.swing.JPanel()
val statusLabel = new javax.swing.JLabel("Status:")
val buttonUpdate = new javax.swing.JButton("Update")

//frame.setLayout(new javax.swing.BoxLayout(frame, javax.swing.BoxLayout.Y_AXIS))
panel.add(new javax.swing.JScrollPane(table))
panel.add(statusLabel)
panel.add(buttonUpdate)
frame.add(panel)

frame.setSize(500, 500)
frame.setResizable(false)
frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
frame.setVisible(true)


def runEvery(period: Int)(action: => Unit) = {
  val listener = new java.awt.event.ActionListener(){
    def actionPerformed(ev: java.awt.event.ActionEvent) = action
  }
  val timer = new javax.swing.Timer(period, listener)
  timer.start()
  timer
}

def updateRates() = { 
  val rates = Future { getExchangeRates() }
  statusLabel.setText("Update exchange rates ...")

  rates onSuccess { case r =>
    model.clear()
    model.addItems(r)
    statusLabel.setText("Last update " + new java.util.Date())
    println("Exchange rates updated")
  }

  rates onFailure { case ex =>
    println("Error: Couldn't fetch exchange rates")
    println("Error: " + ex)
    jswing.Dialog.showError("Error report.", "Error: Couldn't fetch exchange rates\nError: " + ex)
  }
}

Event.onButtonClick(buttonUpdate){ updateRates() }
updateRates()

/// Update exchange rate every 10 seconds 
// runEvery(10000){ updateRates() }
