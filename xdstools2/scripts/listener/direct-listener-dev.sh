#!/bin/sh

# This version is indended to be run on a development machine where:
#   port 12999 is used instead of 25
#   the webinf path is different
#   the log file path is different

#
# Start listener for Direct SMTP receiver
#

webinf=~/Documents/sf/toolkit/xdstools2/war/WEB-INF
external_cache=~/tmp/ttt
port=12999

log=./direct-listener.log

lib=$webinf/lib
classes=$webinf/classes


export CLASSPATH=${lib}/*:${classes}/log4j.properties:${classes}

runable=gov.nist.toolkit.directsim.Listener

echo "lib is ${lib}"

echo "Listening on port $port"  

keystore=$webinf/privcert/mykeystore.p12

java $runable $port $external_cache $keystore  > $log
