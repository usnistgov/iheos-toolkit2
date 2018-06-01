#!/usr/bin/env bash

# build-release.sh

# Do the whole build including installing the toolkit.properties file

function usage() {
	echo "Usage: $SCRIPTNAME configuration-name [httpsUI]"
	echo "Where configuration-name is a directory under properties/"
	echo "that holds a toolkit.properties file to incorporate."
	echo "httpsUI uses a profile activated web.xml configured to redirect http UI requests to https."
}


SCRIPTNAME=$(basename $0 .sh)
BUILDDIR=`pwd`

if [ $# -eq 0 ]
then
	usage
	exit -1
fi
PROPERTIESNAME=$1
PROPERTIESFILE=$BUILDDIR/properties/$PROPERTIESNAME/toolkit.properties

HTTPSUI=$2

if [ ! -f $PROPERTIESFILE ]
then
	echo "Properties file $PROPERTIESFILE does not exist"
	exit -1
fi

if [ ! -z $HTTPSUI ]
then
    if [ $HTTPSUI = "httpsUI" ]
    then
        echo "httpsUI option is on."
#	    Note: If HTTPS UI web.xml configuration is desired you must use an additional -PhttpsUI maven profile.
        mvn clean install -P Bill -PhttpsUI -D skipTests
    else
        mvn clean install -P Bill -D skipTests
    fi
else
    mvn clean install -P Bill -D skipTests
fi

bash pre-release.sh

bash package-release.sh $PROPERTIESNAME

