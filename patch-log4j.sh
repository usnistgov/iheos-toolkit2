#!/bin/bash


# Patch log4j 1.x based on instructions from https://www.slf4j.org/log4shell.html
# Addresses CVE 2019-17571
# https://nvd.nist.gov/vuln/detail/CVE-2019-17571
#
# log4j 1.x is not affected by CVE 2021-44228.

BUILDDIR=`pwd`

cd xdstools2/target
WARNAME=$(basename *.war .war)
LOG4J_JAR="log4j-1.2.17.jar"
LOG4J_PATCHED_JAR="log4j-1.2.17-patched.jar"

cd $BUILDDIR/xdstools2/target/$WARNAME/WEB-INF/lib

if [ -f "$LOG4J_JAR" ]; then
  echo "$LOG4J_JAR exists. Patching..."
  ls -l $LOG4J_JAR
  zip -d $LOG4J_JAR "/org/apache/log4j/net/*"
  mv $LOG4J_JAR $LOG4J_PATCHED_JAR
  ls -l $LOG4J_PATCHED_JAR
  echo "Done."
else
  echo "$LOG4J_JAR does not exist. Checking if already patched..."
  if [ -f "$LOG4J_PATCHED_JAR" ]; then
     echo ls -l $LOG4J_PATCHED_JAR
     echo "Already patched."
  else
      echo "Neither the patched file does not exist nor the unpatched file exists."
  fi
fi

# end
