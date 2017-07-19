import jswing.Dialog.FileChooser

val chooser = new FileChooser(title="Select an image")
  .withHome()
  .withImageFilter()


val viwer = new jswing.guis.PictureFrame(
  visible     = true
 ,exitOnClose = true   
)

viwer.onClick{
  chooser.run() foreach (img => viwer.setImageFromFile(img, 300))
  jswing.JUtils.saveScreenShotArgs(viwer, "images/demoImageViewer1.png")(args)
}

    
