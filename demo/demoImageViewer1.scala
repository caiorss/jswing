import jswing.Dialog.FileChooser
import jswing.widgets.{Frame, PictureBox, Button}
import jswing.panel.{BorderPanel, FlowPanelLeft}

val chooser = new FileChooser(title="Select an image")
  .withHome()
  .withImageFilter()

val btnOpen  = new Button("Open")
val btnClose = new Button("Close")
val viewer    = new PictureBox()

val panel = new BorderPanel(
  top          = new FlowPanelLeft(btnOpen, btnClose),
  centerScroll = viewer
)

val frame = new Frame(
  title       = "Image viewer",
  size        = (500, 400),
  resizable  = false,
  exitOnClose = true,
  visible     = true,
  pane        = panel
)

btnClose.onClick{
  viewer.clear()
}

btnOpen.onClick{
  chooser.run() foreach (img => viewer.setImageFile(img))
  jswing.JUtils.saveScreenShotArgs(viewer, "images/demoImageViewer1.png")(args)
}

    
