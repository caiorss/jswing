
object XmlLayout {

    def getAttr(mdata: scala.xml.MetaData) =
        (name: String) => mdata.get(name).map(_.text)


    def getNodeChild(node: scala.xml.Node) = {
      node.child.filterNot(_.label == "#PCDATA")
    }

    def setBoolProp(value: Option[String], boolFn: Boolean => Unit) = 
        value match {
          case Some("true")  => boolFn(true)
          case Some("false") => boolFn(false)
          case None          => ()
          case _             => error("Error: Invalid boolean value")
        }


    def setCompPosJFrame(frame: javax.swing.JFrame,  comp: javax.swing.JComponent, pos: String) =
        pos match {
          // For border layout
          case "north"  => frame.add(comp, java.awt.BorderLayout.NORTH)
          case "south"  => frame.add(comp, java.awt.BorderLayout.SOUTH)
          case "west"   => frame.add(comp, java.awt.BorderLayout.WEST)
          case "east"   => frame.add(comp, java.awt.BorderLayout.EAST)
          case "center" => frame.add(comp, java.awt.BorderLayout.CENTER)            
          case _        => error("Error: Invalid layout position")
        }


    def getColor(color: String) =
        Option(
          color match {
            case "blue"    => java.awt.Color.blue
            case "cyan"    => java.awt.Color.cyan
            case "red"     => java.awt.Color.red
            case "green"   => java.awt.Color.green
            case "yellow"  => java.awt.Color.yellow
            case "white"   => java.awt.Color.white
            case "pink"    => java.awt.Color.pink
            case "black"   => java.awt.Color.black
            case "magenta" => java.awt.Color.magenta
            case "orange"  => java.awt.Color.orange
            case "gray"    => java.awt.Color.gray
            case  _        => null
      })


    def setColorFn(color: Option[String], colorFn: java.awt.Color => Unit) = {
      color flatMap getColor foreach colorFn
    }


    def makeJFrame(
      node: scala.xml.Node,
      cont: scala.xml.Node => javax.swing.JComponent
    ) = {      

      val attr  = getAttr(node.attributes)
      val frame = new javax.swing.JFrame()

      attr("title") foreach frame.setTitle
      attr("name")  foreach frame.setName
      setColorFn(attr("bgColor"), frame.setBackground)
      setColorFn(attr("fgColor"), frame.setForeground)
      setBoolProp(attr("resizable"), frame.setResizable)

      attr("exitOnClose") match  {
        case Some("true")
            => frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
        case Some("false") => ()
        case _             => error("Invalid boolean value")
      }

      val h = attr("height").map(_.toInt)
      val w = attr("width").map(_.toInt)

      (h, w) match {
        case (Some(x), Some(y)) => frame.setSize(x, y)
        case _                  => () 
      }

      attr("layout") match {
        case Some("flow")   => frame.setLayout(new java.awt.FlowLayout())
        case Some("border") => frame.setLayout(new java.awt.BorderLayout())
        //case Some("box")    => frame.setLayout(new javax.swing.BoxLayout(frame, javax.swing.BoxLayout.Y_AXIS))  
        case None           => ()
        case _              => error("Error: Invalid layout format")
      }

      getNodeChild(node) foreach {n =>
        println("Adding component ---> " + n)

        // Build JComponent widget
        val comp = cont(n)

        // Set layout position
        n.attributes.get("pos").map(_.text) match {
          case None      => frame.add(comp)      
          case Some(pos) => setCompPosJFrame(frame, comp, pos) 
        }
      }

      setBoolProp(attr("visible"), frame.setVisible)
      frame
    } // End of function makeJframe



    /** Set JComponent widgets common properties */
    def setJCompProp(node: scala.xml.Node, comp: javax.swing.JComponent) = {
      val attr = getAttr(node.attributes)
      attr("name") foreach comp.setName
      attr("tooltip") foreach comp.setToolTipText
      setColorFn(attr("bgColor"), comp.setBackground)
      setColorFn(attr("fgColor"), comp.setForeground)  
      setBoolProp(attr("visible"), comp.setVisible)
      setBoolProp(attr("enabled"), comp.setEnabled)  
    }



    def makeJLabel(node: scala.xml.Node) = {
      val attr = getAttr(node.attributes)
      val comp = new javax.swing.JLabel()
      setJCompProp(node, comp)    
      attr("text") foreach comp.setText
      comp
    }

    def makeJButton(node: scala.xml.Node) = {
      val attr = getAttr(node.attributes)
      val comp = new javax.swing.JButton()
      setJCompProp(node, comp)      
      attr("text") foreach comp.setText
      attr("name") foreach comp.setName
      comp
    }

    def makeJTextArea(node: scala.xml.Node) = {
      val attr = getAttr(node.attributes)
      val comp = new javax.swing.JTextArea()
      setJCompProp(node, comp)      
      attr("text") foreach comp.setText
      comp
    }

    def makeJTextField(node: scala.xml.Node) = {
      val attr = getAttr(node.attributes)
      val comp = new javax.swing.JTextField()
      attr("text") foreach comp.setText
      attr("name") foreach comp.setName
      attr("tooltip") foreach comp.setToolTipText
      setColorFn(attr("bgColor"), comp.setBackground)
      setColorFn(attr("fgColor"), comp.setForeground)  
      setBoolProp(attr("visible"), comp.setVisible)
      setBoolProp(attr("enabled"), comp.setEnabled)
      setBoolProp(attr("editable"), comp.setEditable)
      comp
    }


    def makeJScrollPane(node: scala.xml.Node, cont: scala.xml.Node => javax.swing.JComponent) = {  
      new javax.swing.JScrollPane(cont(node.child.head))
    }

    def makeContainer(
      node: scala.xml.Node,  
      comp: javax.swing.JComponent, 
      cont: scala.xml.Node => javax.swing.JComponent
    ) = {
      val attr  = getAttr(node.attributes)

      setColorFn(attr("bgColor"), comp.setBackground)
      setColorFn(attr("fgColor"), comp.setForeground)

      attr("layout") match {
        case Some("flow")   => comp.setLayout(new java.awt.FlowLayout())
        case Some("border") => comp.setLayout(new java.awt.BorderLayout())
        //case Some("box")    => frame.setLayout(new javax.swing.BoxLayout(frame, javax.swing.BoxLayout.Y_AXIS))  
        case None           => ()
        case _              => error("Error: Invalid layout format")
      }

      getNodeChild(node) foreach {c => comp.add(cont(c))}

      comp 
    } // End of makeContainer


    def createComponent(node: scala.xml.Node): javax.swing.JComponent = {
      println("--> Creating node = " + node)
      node.label match {
        //case "jframe"  => makeJFrame(node, createComponent)
        case "jbutton"    => makeJButton(node)
        case "jlabel"     => makeJLabel(node)
        case "jtextField" => new javax.swing.JTextField(10)
        case "jtextArea"  => makeJTextArea(node)
        case "jpanel"     => makeContainer(node, new javax.swing.JPanel(), createComponent)
        case "jscroll"    => makeJScrollPane(node, createComponent)
        case _            => error("Invalid java jswing component: " + node.label )  
      }
    }


    def getAllComponents(
      con:  java.awt.Container,
      hmap: scala.collection.mutable.Map[String, java.awt.Component]
    ) {
      def transverse(con: java.awt.Container): Unit = {
        con.getComponents() foreach { c =>
          val name = c.getName()
          if (name != null) { hmap += name -> c }
          transverse(c.asInstanceOf[java.awt.Container])
        }
      }
      transverse(con)
      if (con.getName != null) { hmap += con.getName -> con}
    }

}

object Builder {

  def makeFromXML(node: scala.xml.Node) = {
    if (node.label != "jswing")
      error("Error: invalid jswing xml layout Expected jswing tag.")

    val hmap =  scala.collection.mutable.Map[String, java.awt.Component]()

    val frames =  XmlLayout.getNodeChild(node) map { n =>
      XmlLayout.makeJFrame(n,  XmlLayout.createComponent)
    }

    frames foreach (fr =>  XmlLayout.getAllComponents(fr, hmap))
    hmap
  }


  def makeFromFile(file: String) = {
    val xml = scala.xml.XML.loadFile(file)
    makeFromXML(xml)
  }


  def makeFromString(xmlstr: String) = {
    val xml = scala.xml.XML.loadString(xmlstr)
    makeFromXML(xml)
  }

  def getJButton(
    hmap: scala.collection.mutable.Map[String, java.awt.Component],
    name: String
  ) =  {
    hmap(name).asInstanceOf[javax.swing.JButton]
  }

  def getJLabel(
    hmap: scala.collection.mutable.Map[String, java.awt.Component],
    name: String
  ) =  hmap(name).asInstanceOf[javax.swing.JLabel]

  def getJFrame(
    hmap: scala.collection.mutable.Map[String, java.awt.Component],
    name: String
  ) =  {
    hmap(name).asInstanceOf[javax.swing.JFrame]
  }

}



}


