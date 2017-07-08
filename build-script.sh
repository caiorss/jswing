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

scala -cp bin/jswing.jar -save $1

SCRIPT_JAR="${1%.*}.jar"
OUTPUT_JAR=out/$(basename "${SCRIPT_JAR%.*}.jar")

build-fat-jar.sh -scala $OUTPUT_JAR \
                 $SCRIPT_JAR \
                 bin/jswing.jar \
                 /home/archbox/opt/scala-2.11.8/lib/scala-xml_2.11-1.0.4.jar
