
CC	 := scalac 
SCALA_HOME := $(HOME)/opt/scala-2.11.8

libsrc := src/jswing.scala src/jswing.guis.scala

lib	 := bin/jswing.jar

scalalib := bin/scala-library.jar

demo := bin/jtest.jar 


lib:  $(lib)
demo: $(demo)
scalalib: $(scalalib)

$(lib): src/jswing.scala src/jswing.guis.scala 
	$(CC) $(libsrc) -d $(lib)

load: lib
	scala -cp $(lib)

$(demo): lib src/jswingTest.scala
	$(CC) -cp $(lib)  src/jswingTest.scala -d $(demo)

run: demo
	scala -cp $(lib) $(demo)

doc:
	scaladoc $(libsrc) -doc-title "Jswing - Java Swing Wrapper" -doc-version "1.0" -d ./docs 

$(scalalib): $(SCALA_HOME)/lib/scala-library.jar
	echo $(SCALA_HOME)/lib/scala-library.jar
	cp -v $(SCALA_HOME)/lib/scala-library.jar bin/

imageApp := bin/pframe.jar
imageApp: $(imageApp)

$(imageApp): lib scalalib bin/pframe.jar
	scala -cp $(lib) -save scripts/demoPictureFrame.scala 
	mv scripts/demoPictureFrame.jar bin/pframe.jar 

run-imageApp: 
	java -cp bin/scala-library.jar:bin/jswing.jar:bin/pframe.jar Main

clean:
	rm -rf bin/*.jar 

clean-doc:
	rm -rf doc/*

# Shutdown scala compilation daemon 
fsc-shutdown:
	fsc -shutdown

# Restart scala compilation daemon 
fsc-reset:
	fsc -reset 
