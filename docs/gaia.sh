#!/bin/sh
if [ "$1" = "" ]
then
  echo "Usage:
  ./gaia.sh en|fr"
  exit
fi
java -classpath bin/gaia.jar Gaia --batch:gaia-batch-$1
