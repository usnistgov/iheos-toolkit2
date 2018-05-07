#!/bin/bash

#
# Finalize WAR
# add javadocs and site documentation
# insert my toolkit.properties file
#
# This script is generic. If it is named build.vm.sh it will use
# build.vm.stuff as the update content directory

BASEDIR=$(dirname $0)
if [ $BASEDIR='.' ];
then
	BASEDIR=$(pwd)
fi

echo "BASEDIR is $BASEDIR"

SCRIPTNAME=$(basename $0 .sh)

cd $BASEDIR
mvn -o clean package -DskipTests -Dmaven.test.skip=true

cd $BASEDIR/xdstools2/target
WARNAME=$(basename *.war .war)

rm -rf $BASEDIR/xdstools2/target/$WARNAME/javadoc
#mkdir $BASEDIR/xdstools2/target/$WARNAME/javadoc
cd $BASEDIR
bash $BASEDIR/genapidoc.sh $BASEDIR/xdstools2/target/$WARNAME/javadoc

cd $BASEDIR/xdstools2

// warning - if -o is present then it runs much much faster
// but the first time you run it on a machine -o must be removed
// so that the necessary plugins can be downloaded
mvn -o site

cd $BASEDIR/xdstools2/target
rm -rf $WARNAME/site
mv site $WARNAME

cd $BASEDIR/xdstools2/target/$WARNAME
rm ../$WARNAME.war
jar cf ../$WARNAME.war *

cd ${BASEDIR}/${SCRIPTNAME}.stuff
jar uf $BASEDIR/xdstools2/target/${WARNAME}.war WEB-INF
