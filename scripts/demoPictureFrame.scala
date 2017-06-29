import jswing.guis._
import jswing.JUtils

def listFiles(path: String) = {
  (new java.io.File(path))
    .listFiles()
    .filter(_.isFile)
    .map(_.toString)
}

val lview = new ListView(
  title       = "File selector"
 ,visible     = true
 ,exitOnClose = true
)

val pframe = new PictureFrame(
  title   = "Picture Frame",
  visible = true,
  exitOnClose = true
)

val fchooser = new jswing.Dialog.DirChooser()


pframe.getPictureBox().onClick{
  val path = fchooser.run()
  path match {
    case Some(p) => {     
      lview.clear()
      lview.addElements(listFiles(p))
    }
    case _       => ()
  }
}

lview.onSelect{
  val file = lview.getSelectedValue()
  pframe.setImageFromFile(file, 400)
}







