import jswing.JUtils
import jswing.guis.ListView

def listFiles(path: String) = {
  (new java.io.File(path))
    .listFiles()
    .map(_.toString)
}

val lview = new ListView(
  title       = "File selector"
 ,visible     = true
 ,exitOnClose = true
)

JUtils.invokeLater{ lview.addElements(listFiles("/")) }

JUtils.saveScreenShotArgs(lview, "images/demoListView.png")(args)
