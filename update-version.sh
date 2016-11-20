#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Usage: ./update-version.sh VERSION_NUMBER"
    echo "    VERSION_NUMBER must look like 4.3.1 or 4.3.1-SNAPSHOT"
    exit
fi

# This is the root of the project so it must be run from here
cd tk-deps

mvn -X versions:set -DnewVersion=$1 -DallowSnapshots=true

cd ..
