#!/bin/bash

#
# Finalize WAR
# add javadocs and site documentation
# insert my toolkit.properties file
#
# This script is generic. If it is named build.vm.sh it will use
# build.vm.stuff as the update content directory

BUILDDIR=`pwd`

BASEDIR=$(dirname $0)
if [ $BASEDIR='.' ];
then
	BASEDIR=$(pwd)
fi

SCRIPTNAME=$(basename $0 .sh)

cd $BUILDDIR
mvn clean package -DskipTests -Dmaven.test.skip=true -PRelease

cd xdstools2/target
WARNAME=$(basename *.war .war)



cd $BUILDDIR
mkdir xdstools2/target/$WARNAME/javadoc
bash $BASEDIR/genapidoc.sh xdstools2/target/$WARNAME/javadoc


# This will fail if plexus stuff is not up to date locally.  If so, run mvn site
# to force downloads.  This will never complete (in your lifetime) but once it has downloaded
# the packages it can be killed and this script re-run.
cd xdstools2
mvn -o site
