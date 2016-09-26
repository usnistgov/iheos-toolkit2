#!/bin/sh

#
# install-ttt.sh
#

# location of ttt tomcat
ttt_tomcat=/var/lib/tomcat_ttt

if [ "$USER" != "ttt" ] ; then
	echo "install-ttt.sh can only be run by user ttt"
	exit 1
fi

# install WAR file
(cd ~bill; cp ttt-upload/ttt.war ${ttt_tomcat}/webapps)

# restart ttt tomcat to force unpacking/instance of new war
sudo service tomcat_ttt restart
