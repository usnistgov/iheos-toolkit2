#!/bin/bash

# Generate a toolkit release for Steve - specifically for using API

# Basic build
(cd ../..; mvn clean install)

# location for bundle to be built
output=~/tmp/release

mkdir -p $output

cp ../target/*.war $output

javadoc @options @packages