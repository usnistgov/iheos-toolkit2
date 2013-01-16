#!/bin/bash

#
# email.sh propertyFile from password to
#

wd=`pwd`
war=$wd/..
lib=$war/WEB-INF/lib
classes=$war/WEB-INF/classes

echo "classes is $classes"

class=gov.nist.toolkit.email.java.test.EmailerMain

export CLASSPATH=${lib}/*:${classes}/log4j.properties:${classes}

echo java $class gmail-files/gmail-properties.txt $*

java $class gmail-files/gmail-properties.txt $*