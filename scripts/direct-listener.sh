#!/bin/sh

#
# Start listener for Direct SMTP receiver
#

webinf=/home/lab/Documents/sf/toolkit/xdstools2/war/WEB-INF
external_cache=/home/lab/tmp/ttt
#port=12999
port=12087

keystore=/home/lab/Documents/sf/toolkit/xdstools2/cert/mykey.pk12


lib=$webinf/lib
classes=$webinf/classes


export CLASSPATH=${lib}/*:${classes}/log4j.properties:${classes}

runable=gov.nist.toolkit.directsim.Listener

echo "lib is ${lib}"

echo "Listening on port $port"  


java $runable $port $external_cache  

