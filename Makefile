
CC	 := scalac 

libsrc := src/jswing.scala src/jswing.guis.scala

lib	 := bin/jswing.jar

demo := bin/jtest.jar 

lib:  $(lib)
demo: $(demo)

$(lib): src/jswing.scala src/jswing.guis.scala 
	$(CC) -nocompdaemon $(libsrc) -d $(lib)

load: lib
	scala -cp $(lib)

$(demo): lib src/jswingTest.scala
	$(CC) -cp $(lib)  src/jswingTest.scala -d $(demo)

run: demo
	scala -cp $(lib) $(demo)

clean:
	rm -rf bin/*.jar 

# Shutdown scala compilation daemon 
fsc-shutdown:
	fsc -shutdown

# Restart scala compilation daemon 
fsc-reset:
	fsc -reset 
