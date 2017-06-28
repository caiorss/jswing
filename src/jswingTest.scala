import jswing.JUtils

object Main extends {
  def listFiles(path: String) = {
    (new java.io.File(path))
      .listFiles()
      .map(_.toString)
  }

  def main(args: Array[String]){
    val lview = new jswing.ListView(
      title       = "File selector"
     ,visible     = true
     ,exitOnClose = true
    )

    val dispose =  lview.onSelect{
      println("Yout selected file = " + lview.getSelectedValue())
    }

    //lview.addElements(listFiles("/etc"))

    //val path = args(0)
    val path = jswing.Dialog.chooseDir()

    path match {
      case Some(p) => JUtils.invokeLater(() => lview.addElements(listFiles(p)))
      case None    => System.exit(1)
    }
  }

} // End of objec Main 




