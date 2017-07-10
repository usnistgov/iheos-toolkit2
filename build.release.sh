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

SCRIPTNAME=$(basename $0 .sh)

cd /media/aberge/DATA/workspace-sequoia/toolkit
/usr/local/maven/bin/mvn clean package -DskipTests -Dmaven.test.skip=true

cd xdstools2/target
WARNAME=$(basename *.war .war)

cd /media/aberge/DATA/workspace-sequoia/toolkit
mkdir xdstools2/target/$WARNAME/javadoc
bash $BASEDIR/genapidoc.sh xdstools2/target/$WARNAME/javadoc

echo "generating site"
cd xdstools2
/usr/local/maven/bin/mvn -o site

echo "replacing site directory"
cd target
rm -r $WARNAME/site
mv site $WARNAME

echo "reassembling xdstools4"
cd $WARNAME
jar cf ../xdstools4.war *

echo "changing directory to " ${BASEDIR}/${SCRIPTNAME}.stuff
cd ${BASEDIR}/${SCRIPTNAME}.stuff
jar uf /media/aberge/DATA/workspace-sequoia/toolkit/xdstools2/target/xdstools4.war WEB-INF

