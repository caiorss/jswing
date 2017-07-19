import jswing.guis._
import jswing.JUtils

def hasExtension(extList: Seq[String]) = (file: String) => {
  extList exists (file.endsWith)
}

def listFiles(extList: String*) = {
  val fileFilter = hasExtension(extList) 

  (path: String) => {
    new java.io.File(path)
      .listFiles()
      .filter(p => (p.isFile && fileFilter(p.getName())))
      .map(_.toString)
  }
}


def multivar(s: String*){
  println(s)
}

val listImageFiles = listFiles(".png", ".jpeg", ".jpg", ".tiff", ".tif", ".bmp", ".gif")

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

val fchooser = (new jswing.Dialog.DirChooser()).withHome()


pframe.getPictureBox().onClick{
  val path = fchooser.run()
  path match {
    case Some(p: String) => {     
      lview.clear()
      lview.addElements(listImageFiles(p))
    }
    case _       => ()
  }
}

lview.onSelect{
  val file = lview.getSelectedValue()
  pframe.setImageFromFile(file, 400)
}







