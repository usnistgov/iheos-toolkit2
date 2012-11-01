#!/bin/sh

#
# Start listener for Direct SMTP receiver
#

webinf=/home/bill/bin/listener
external_cache=/home/bill/tmp/ttt
port=12999
#port=12087

log=/home/bill/bin/direct-listener.log

lib=$webinf/lib
classes=$webinf/classes


export CLASSPATH=${lib}/*:${classes}/log4j.properties:${classes}

runable=gov.nist.toolkit.directsim.Listener

echo "lib is ${lib}"

echo "Listening on port $port"  

keystore=$webinf/privcert/mykeystore.p12

log=/home/bill/ttt/webapps/ROOT/direct/listener_log.txt

java $runable $port $external_cache $keystore  > $log
