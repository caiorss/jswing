import jswing.Dialog.DirChooser

import jswing.widgets.{Frame, Button, ListBox, PictureBox, ComboBox}


val dirModel = new jswing.ValueModel[Option[String]](None)

val chooser = new DirChooser(title="Select a directory with images.")
  .withHome()

val buttonOpen   = new Button("Open", toolTip = "Open a directory containing images")
val buttonClose  = new Button("Close", toolTip = "Close current directory.")
val buttonRename = new Button("Rename", toolTip = "Rename selected file.")

//val dirComboBox  = new javax.swing.JTextField(50)
val dirComboBox = new ComboBox[String]()
dirComboBox.addItem("None", "None")


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
      buttonRename,
      dirComboBox
    ),
    centerScroll  = pictureBox,
    leftScroll    = filesListBox
  )
)

// jswing.Event.printDimensions(frame)

val imageExtensions = Array(".png", ".jpg", ".jpeg", ".gif", ".tiff", ".PNG", ".JPEG", ".JPG")

def renameFileInDir(file: String, newName: String){
  val f = new java.io.File(file)
  f.renameTo(new java.io.File(f.getParent(), newName))
}

def getFileName(file: String) = {
  new java.io.File(file).getName()
}

def fileHasExtension(exts: Array[String]) = (file: String) =>
  exts.exists(e => file.endsWith(e))

val isIMageFile = fileHasExtension(imageExtensions)

def isDirectory(path: String) =
  new java.io.File(path).isDirectory()


def getImageFiles(path: String) = {
  new java.io.File(path).listFiles()
    .filter(n => isIMageFile(n.getName()))
}

def showDirectory(path: String){
  filesListBox.clear()  

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
  dirComboBox.setSelectedIndex(0)
  //dirComboBox.clear()
}

//-------- Set up events ---------------- //

dirModel logChanges ("Current path = ")


dirModel onChangeValue( path =>  path match {

  case Some(p) if !isDirectory(p) => ()

  case Some(p)
      => {
        showDirectory(p)
        dirComboBox.addItemUnique(p, p, selectLast = true)
        dirComboBox.selectLabel(p)
        chooser.setDirectory(p)
      }
  case None    => clear()
})


buttonOpen.onClick {
  chooser.run() foreach { file => dirModel() = Some(file) }
}

buttonClose.onClick {
  dirModel() = None
}

val dispComboBox = dirComboBox.onSelectItem{ path =>
  println("ComboBox event")
  path match {
    case "None" => dirModel() = None
    case  p     => dirModel() = Some(p)
  }
}

// dispComboBox.setEnabled(false)

filesListBox.onSelectItem { pictureBox.setImageFromFile }


buttonRename.onClick {
  for {
    file <- filesListBox.getSelectedItemValue()
    name <- jswing.Dialog.prompt("New name", getFileName(file))
  } renameFileInDir(file, name)

  dirModel.trigger()
}

