import jswing.Dialog.DirChooser
import jswing.data.{ValueModel, ListModel}
import jswing.widgets.{Frame, Button, Label, ListBox, PictureBox, ComboBox}


// jswing.Event.printDimensions(frame)
object FileUtils {

  val imageExtensions = Array(".png", ".jpg", ".jpeg", ".gif", ".tiff", ".PNG", ".JPEG", ".JPG")

  def renameFileInDir(file: String, newName: String){
    val f = new java.io.File(file)
    f.renameTo(new java.io.File(f.getParent(), newName))
  }

  def getFileName(file: String) = {
    new java.io.File(file).getName()
  }

  def fileHasExtension(exts: Array[String]) = (file: String) =>{
    exts.exists(e => file.endsWith(e))
  }

  val isIMageFile = fileHasExtension(imageExtensions)

  def isDirectory(path: String) =
    new java.io.File(path).isDirectory()


  def getImageFiles(path: String) = {
     new java.io.File(path).listFiles()
       .filter(n => isIMageFile(n.getName()))
   }
}


val chooser = new DirChooser(title="Select a directory with images.")
  .withHome()

val buttonOpen   = new Button("Open", toolTip = "Open a directory containing images")
val buttonClose  = new Button("Close", toolTip = "Close current directory.")
val buttonRename = new Button("Rename", toolTip = "Rename selected file.")

//val dirComboBox  = new javax.swing.JTextField(50)
val dirComboBox = new ComboBox[String]()

val filesListBox = new ListBox[java.io.File]()


val pictureBox   = new PictureBox()
val fileDisplay  = new Label()

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
      dirComboBox,
      fileDisplay
    ),
    centerScroll  = pictureBox,
    leftScroll    = filesListBox
  )
)

//filesListBox.setWidth(200)

// ------ Data binding -------------------- //
//
val dirModel = new ValueModel[Option[String]](None)
val dirList  = new ListModel[String]()
val filesList = new ListModel[java.io.File]()
val currentFile = new ValueModel[Option[java.io.File]](None)

chooser.bindData(dirModel)

dirComboBox.bindData(dirList){x => x}
dirComboBox.bindDataSelectionOpt(dirModel)

filesListBox.bindData(filesList){_.getName()}
filesListBox.bindDataSelection(currentFile)

pictureBox.bindFileObj(currentFile)

fileDisplay.bindToData(currentFile){ fileOpt =>
  fileOpt match {
    case Some(file) => "File: " + file
    case None       => "File: "
  }
}

// dirComboBox.addItem("None", "None")

// log changes to user 
dirModel.logChanges("dirModel")
currentFile.logChanges("Current file")


//-------- Set up events ---------------- //

def showDirectory(path: String){
  jswing.JUtils.invokeLater{
    filesList() =  FileUtils.getImageFiles(path)
    println("Show directory " + path)
    filesListBox.setSelectedIndex(0)
  }  
}

dirModel onChangeValue( path =>  path match {
  case Some(p) if !FileUtils.isDirectory(p) => ()
  case Some(p)
      => {
        showDirectory(p)
        //dirComboBox.addItemUnique(p, p, selectLast = true)
        dirList.appendUnique(p)
        //dirComboBox.selectLabel(p)
      }
  case None    => filesList.clear()
})


buttonOpen.onClick { chooser.run() }

buttonClose.onClick { dirModel() = None }

