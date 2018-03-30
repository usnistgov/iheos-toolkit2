#!/usr/bin/env bash

# package.sh

# The parts are all build - now load them into a final WAR file

function usage() {
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

cd xdstools2/target

if [ ! -d site ]
then
	echo "site directory does not exist"
	exit -1
fi

WARNAME=$(basename *.war .war)
NEWWARNAME=`echo $WARNAME | sed 's/xdstools2-/xdstools/'`
VERSION=`echo xdstools2*.war | sed 's/xdstools2-//' | sed 's/.war//'`

echo "BUILDDIR is $BUILDDIR"
echo "WARNAME is $WARNAME"
echo "NEWWARNAME is $NEWWARNAME"
echo "VERSION is $VERSION"

rm $WARNAME.war

rm -rf $WARNAME/site
cp -rf site $WARNAME

rm -rf $NEWWARNAME
# cp -r $WARNAME $NEWWARNAME

cp $PROPERTIESFILE $WARNAME/WEB-INF/classes

cd $WARNAME
jar cf ../$NEWWARNAME.war *

# jar cf ../$NEWWARNAME.war *

# cd ${BUILDDIR}/build.release.stuff
# jar uf $BUILDDIR/xdstools2/target/$NEWWARNAME.war WEB-INF


