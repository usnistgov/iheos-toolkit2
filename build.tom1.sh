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

cd ~/tk
mvn -o clean package -DskipTests -Dmaven.test.skip=true

cd xdstools2/target
WARNAME=$(basename *.war .war)

cd ~/tk
mkdir xdstools2/target/$WARNAME/javadoc
bash $BASEDIR/genapidoc.sh xdstools2/target/$WARNAME/javadoc

cd xdstools2
mvn -o site

cd target
rm -r $WARNAME/site
mv site $WARNAME

cd $WARNAME
jar cf ../xdstools2.war *

cd ${BASEDIR}/${SCRIPTNAME}.stuff
jar uf ~/tk/xdstools2/target/xdstools2.war WEB-INF

