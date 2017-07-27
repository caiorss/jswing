import jswing.Dialog.DirChooser

import jswing.widgets.{Frame, Button, ListBox, PictureBox, ComboBox}


val chooser = new DirChooser(title="Select a directory with images.")
  .withHome()

val buttonOpen  = new Button("Open")
val buttonClose = new Button("Close")

//val currentDir  = new javax.swing.JTextField(50)
val currentDir = new ComboBox[String]()
currentDir.addItem("None", "None")


val filesListBox = new ListBox[String]()
val pictureBox   = new PictureBox()


val frame = new Frame(
  title       = "Image Viewer",
  size        = (1162, 632),
  exitOnClose = true,
  visible     = true,

  pane  = new jswing.panel.BorderPanel(
    top =  new jswing.panel.FlowPanelLeft(
      buttonOpen,
      buttonClose,
      currentDir
    ),
    centerScroll  = pictureBox,
    leftScroll    = filesListBox
  )
)

// jswing.Event.printDimensions(frame)

val imageExtensions = Array(".png", ".jpg", ".jpeg", ".gif", ".tiff", ".PNG", ".JPEG", ".JPG")

def fileHasExtension(exts: Array[String]) = (file: String) =>
  exts.exists(e => file.endsWith(e))

val isIMageFile = fileHasExtension(imageExtensions)

def getImageFiles(path: String) = {
  new java.io.File(path).listFiles()
    .filter(n => isIMageFile(n.getName()))
}

def showDirectory(path: String){
  filesListBox.clear()
  currentDir.addItemUnique(path, path, selectLast = true)

  jswing.JUtils.invokeLater{
    val files = getImageFiles(path)
    files foreach {f =>
      filesListBox.addItem(f.getName(), f.getAbsolutePath())
    }

    filesListBox.setSelectedIndex(0)
  }  
}

def clear() {
  filesListBox.clear()
  pictureBox.clear()
  currentDir.setSelectedIndex(0)
  //currentDir.clear()
}

buttonOpen.onClick {
  chooser.run() foreach showDirectory
}

buttonClose.onClick { clear() }


currentDir.onSelectItem{ path =>
  path match {
    case "None" => clear()
    case  p     => showDirectory(p)
  }
}

filesListBox.onSelectItem { pictureBox.setImageFromFile }
