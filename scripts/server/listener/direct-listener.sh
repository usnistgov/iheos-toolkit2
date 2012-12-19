#!/bin/sh

#
# direct-listener.sh
# Start listener for Direct SMTP receiver
#
# ttt.war must be installed under ttt_tomcat before this is run
# This listener is run from the same copy of class and jar files
#
# This listener is started using the script listener.sh 
# Screen handles the screen detach issues.

# start:     	start-listner.sh
# stop:		stop-listener.sh

# location of ttt tomcat
ttt_tomcat=/var/lib/tomcat_ttt

if [ "$USER" != "ttt" ] ; then
	echo "direct-listener.sh can only be run by user ttt"
	exit 1
fi

ttt=${ttt_tomcat}/webapps/ttt

if [ ! -e ${ttt} ] ; then
	echo "TTT servlet not installed"
	exit 1
fi

webinf=${ttt}/WEB-INF

# this must be owned by user ttt
external_cache=/home/bill/tmp/ttt

if [ ! -e ${external_cache} ] ; then
	echo "TTT External Cache does not exist"
	exit 1
fi

if [ ! -O ${external_cache} ] ; then
	echo "Account ttt does not own the external cache"
	exit 1
fi

# SMTP arrives on port 25 as usual but a machine firewall redirect
# rule redirects the connections to port 12999 for two reasons:
# 1 - a regular SMTP engine is used internally on the machine for other projects
# 2 - root access is not required to start/stop the Listener 
port=12999

lib=$webinf/lib
classes=$webinf/classes


export CLASSPATH=${lib}/*:${classes}/log4j.properties:${classes}

runable=gov.nist.toolkit.directsim.Listener

echo "lib is ${lib}"

echo "Listening on port $port"  

# this is the decryption private key
keystore=$webinf/privcert/mykeystore.p12

log=/home/bill/bin/listener/listener_log.txt

echo " "
echo " "
echo "Restarting listener at " `date` >> $log
echo " "
echo " "

java $runable $port $external_cache $keystore  >> $log &

echo $! > /home/bill/bin/listener/direct-listener.pid

