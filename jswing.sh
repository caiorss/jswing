#!/usr/bin/env sh
#
#  Script to build fat-jars from scala scripts. 
#
#  Usage: 
#        ./$(basename $0) script/script1.scala 
#
# It will produce the jar file out/script1.jar 
#
#
# Script build-fat-jar available at https://github.com/caiorss/build-fat-jar
#
#------------------------------------------------------------

# Uncomment the line below to debug script 
# set -x

case "$1" in 

    ## Build jswing script
    -build-script)

        scala -cp bin/jswing.jar -save $2

        SCRIPT_JAR="${2%.*}.jar"
        OUTPUT_JAR=out/$(basename "${SCRIPT_JAR%.*}.jar")

        jar-tools.sh -scala-build-jar $OUTPUT_JAR \
                         $SCRIPT_JAR \
                         bin/jswing.jar \
                         /home/archbox/opt/scala-2.11.8/lib/scala-xml_2.11-1.0.4.jar

        ;;

    # Run a jswing demonstration script
    -run)
        scala -cp .:./icons:bin/jswing.jar -Drepl=true -save $2 ${@:3}
        ;;

    -examples)
        scala bin/jswing.jar -examples
       ;;
    
    # Build a GUI from the file gui1.xml and show it.
    -layout-file)
        scala bin/jswing.jar -layout-file $2
        ;;

    -layout-gui)
        scala bin/jswing.jar -layout-gui
        ;;

    *)
    
        cat <<EOF
jswing - Helper Script 

 +  Builds a fat-jar file from the scala script script/file.scala
 
    * ./$(basename $0) -build-script script/file.scala

 + Run a scala script that relies on jswing.jar 

    * ./$(basename $0) -run script/file.scala

 + Build a GUI from the file gui1.xml and show it.

    * ./$(basename $0) -layout-file script/file.scala

 + Open the Jswing XML layout GUI

    * ./$(basename $0) -layout-gui

 + Open the Jswing GUI to run examples in ./scripts/ directory.

    * ./$(basename $0) -examples

EOF

        ;;
esac 
        
