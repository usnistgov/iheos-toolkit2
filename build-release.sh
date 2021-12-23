#!/usr/bin/env bash

# build-release.sh

# Do the whole build including installing the toolkit.properties file

usage()
{
	echo "Usage: $SCRIPTNAME configuration-name"
	echo "Where configuration-name is a directory under properties/"
	echo "that holds a toolkit.properties file to incorporate."
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

if [ ! -f $PROPERTIESFILE ]
then
	echo "Properties file $PROPERTIESFILE does not exist"
	exit -1
fi

#mvn clean install -P Bill -D skipTests
mvn clean install -P SunilUbuntu -D skipTests

bash pre-release.sh

echo "Running Patch log4j script."
bash patch-log4j.sh

bash package-release.sh $PROPERTIESNAME

