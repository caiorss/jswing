#  Instructions: 
#
# * Load the library jswing.jar in the REPL.
#
#   $ make load 
#
# * Compile the library jswing.jar 
#
#  $ make lib
#
# * Build documentation 
#
#  $ make doc
#
# * Clean project
#  
# $ make clean 
#
# * Clean documentation
#
# $ make clean-doc
#
#-------------- Project Settings  ------------#

# Scala home path 
SCALA_HOME := $(HOME)/opt/scala-2.11.8

# Scala compiler 
CC	       := scalac 

# Library sources 
libsrc := src/jswing.scala src/jswing.widgets.scala src/jswing.guis.scala
# Compiled library name.
lib	 := bin/jswing.jar

#------------------  ---------------------------#

# Path to scala library 
scalalib := bin/scala-library.jar

demo := bin/jtest.jar 


lib:  $(lib)
demo: $(demo)
scalalib: $(scalalib)

$(lib): src/jswing.scala src/jswing.guis.scala 
	$(CC) $(libsrc) -d $(lib)

load: lib
	scala -cp $(lib)

doc: $(libsrc)
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
	rm -v -rf bin/*.jar 

clean-doc:
	rm -v -rf docs/*

# Shutdown scala compilation daemon 
fsc-shutdown:
	fsc -shutdown

# Restart scala compilation daemon 
fsc-reset:
	fsc -reset 
