#!/bin/sh

# #######################################
#
# Configuration
#
# #######################################

# value approved for use at NA Connectathon
pid='911^^^&1.3.6.1.4.1.21367.13.20.1000&ISO'

environment="na2013"

# Development machine
home=/Users/bill
externalCache=$home/tmp/na2013
#war=/Users/bill/tomcat1/webapps/xdstools2
war=..
servlet_jar=/Users/bill/tomcat1/common/lib/servlet-api.jar

# Deploy on nistred
#home=/home/user
#war=$home/tomcat1/webapps/xdstools2
#externalCache=$home/tmp/toolkit
#servlet_jar=$home/tomcat1/common/lib/servlet-api.jar

# Location for classes and libraries
webinf=$war/WEB-INF

# sets env var CLASSPATH
#source /Users/bill/tmp/class/xdstoolkit/lib/classpath

outdir=$externalCache/Dashboard


# #######################################
#
# End Configuration
#
# #######################################


# Build path from expanded war

path1=""
for jar in $webinf/lib/*.jar
do
	path1=$path1:$jar
done 

path1=$path1:$servlet_jar


classes=$webinf/classes
#gwtserv=$webinf/lib/gwt-servlet.jar
#xdstest2=$webinf/lib/xdstest2.jar
#common=$webinf/lib/xds-common.jar

# order is important $CLASSPATH has old copies of common and xdstest2
# this ordering causes the new versions to be found first
export CLASSPATH=$classes:$path1

echo $CLASSPATH
echo ""


while ( true ) 
do
	
	echo "running ..."
	
	java gov.nist.toolkit.xdstools2.scripts.DashboardDaemon $pid $war/ $outdir $environment $externalCache
	
	echo "... done"
	
	date
	
	echo "waiting ..."

	sleep 900     # 15 minutes

done
