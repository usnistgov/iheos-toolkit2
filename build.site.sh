#!/usr/bin/env bash

#
# Build web site
#

BASEDIR=$(pwd)

SCRIPTNAME=$(basename $0 .sh)

WARNAME=xdstools2-2.202.0-SNAPSHOT

cd $BASEDIR/xdstools2
mvn site -Ddependency.locations.enabled=false

