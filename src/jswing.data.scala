package jswing.data 


class ValueModel[A](value: => A){
  private var observers: Set[() => Unit] = Set()
  private var state = value

  override def toString() = this.state.toString()

  def trigger() =
    for (fn <- observers) { fn()}

  def set(a: A) = if (a != state){
    this.state = a
    this.trigger()
  }

  def get() = this.state

  def update(a: A) = this.set(a)

  def apply() =
    this.get()

  // Recalculate formula and update state if
  // it has changed notifying observers.
  //
  def compute() = {
    val s = value
    if (s != state){
      this.state = s
      this.trigger()
    }
  }

  def onChange(fn: () => Unit) = {
    observers += fn
    () => observers -= fn
  }

  def onChangeRun(fn: => Unit) =
    this.onChange(() => fn)

  def onChangeValue(fn: A => Unit) =
    this.onChange{() => fn(this.state)}
  

  def logChanges(tag: String) = this.onChange { () =>
    println(s"Value Cell '${tag}' changed to ${this.get}")
  }
}



object ValueModel {

  def bind[A, B](a: ValueModel[A])(action: => B) = {
    val z = new ValueModel(action)
    a onChange z.compute
    z
  }

  def bind2[A, B, C](a: ValueModel[A], b: ValueModel[B])(action: => C) =  {
    val z = new ValueModel(action)
    a onChange z.compute
    b onChange z.compute
    z
  }

  def bindDouble(cells: ValueModel[Double]*)(action: => Double) = {
    val r = new ValueModel(action)
    for (c <- cells) { c onChange r.compute }
    r
  }

  def bindInt(cells: ValueModel[Int]*)(action: => Int) = {
    val r = new ValueModel(action)
    for (c <- cells) { c onChange r.compute }
    r
  }


  def bindSpinner[A](valm: ValueModel[A], spinner: javax.swing.JSpinner) = {
    // Set initial widget value
    spinner.setValue(valm.get())
    // Subscribe widget to change event
    valm.onChange {() => spinner.setValue(valm.get())}

    val listener = new javax.swing.event.ChangeListener{
      def stateChanged(evt: javax.swing.event.ChangeEvent){
        valm.set( spinner.getValue().asInstanceOf[A])
      }
    }

    // Subscribe value model to widget evet
    spinner.addChangeListener(listener)
  }


  def bindFTextField[A](valm: ValueModel[A], entry: javax.swing.JFormattedTextField) = {
    // Set initial widget value
    entry.setValue(valm.get())
    // Subscribe widget to change event
    valm.onChange{ () => entry.setValue(valm.get()) }

    val listener = new java.beans.PropertyChangeListener{
      def propertyChange(evt: java.beans.PropertyChangeEvent){
        valm.set(entry.getValue.asInstanceOf[A])
      }
    }
    entry.addPropertyChangeListener("value", listener)
  }


  //def bindTextFieldAsOutput[A](valm: ValueModel[A], tfield: 


} // End of ValueModel 



class ListModel[A]{
  private var changeObservers: Set[() => Unit] = Set()
  private var appendObservers: Set[A => Unit] = Set()
  private val list = scala.collection.mutable.ListBuffer[A]()

  override def toString() = list.toString()

  private def triggerOnChange() =
    for (fn <- changeObservers) { fn()}

  private def triggerOnAppend(value: A) =
    for (fn <- appendObservers) { fn(value)}

  def getListBuffer() = list

  def foreach(fn: A => Unit) = list foreach fn

  def onChange(fn: () => Unit) = {
    changeObservers += fn
    () => changeObservers -= fn
  }

  def onAppend(fn: A=> Unit) = {
    appendObservers += fn
    () => appendObservers -= fn
  }

  def onChangeRun(fn: => Unit) =
    this.onChange(() => fn)

  def append(value: A) = {
    list.append(value)
    this.triggerOnChange()
    this.triggerOnAppend(value)
  }

  def setFrom(seq: Seq[A]) = {
    list.clear()
    list.appendAll(seq)
    this.triggerOnChange()
  }

  def setFromArray(seq: Array[A]) = {
    list.clear()
    list.appendAll(seq)
    this.triggerOnChange()
  }

  def update(seq: Seq[A]) = this.setFrom(seq)

  def apply(index: Int) = list(index)

  def clear() = {
    list.clear()
    this.triggerOnChange()
  }

  def last() = this.list.last

  def head() = this.list.head

  def logAppend() = this.onAppend{ elem =>
    println("Last element = " + elem)
  }

}
