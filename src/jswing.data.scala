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

}
