
CC := scalac 

lib := bin/jswing.jar

demo := bin/jtest.jar 

lib:  $(lib)
demo: $(demo)

$(lib): src/jswing.scala
	$(CC) src/jswing.scala -d $(lib)

$(demo): lib src/jswingTest.scala
	$(CC) -cp $(lib) src/jswingTest.scala -d $(demo)

run: demo
	scala -cp $(lib) $(demo)
