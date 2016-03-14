#!/bin/bash

# Generate a toolkit release for Steve - specifically for using API

# Basic build
mvn clean install

# location for bundle to be built
output=~/tmp/release

rm -f $output
mkdir -p $output

cp xdstools2/target/*.war $output

sh genapidoc.sh $output/javadoc

# package client libs
clientlibs=$output/clientlibs

mkdir $clientlibs

warlibs=xdstools2/target/xdstools2-2.202.0-SNAPSHOT/WEB-INF/lib
cp $warlibs/jersey-client*.jar $clientlibs
cp $warlibs/jersey-media-json-jackson*.jar $clientlibs
cp $warlibs/toolkit-api*.jar $clientlibs
cp $warlibs/toolkit-services-common*.jar $clientlibs
cp $warlibs/transaction-notification-service*.jar $clientlibs

