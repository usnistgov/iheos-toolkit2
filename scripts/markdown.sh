#!/bin/sh

# shell script wrapper for Markdown.pl

infile=$1
name=`basename $infile .md`
#echo "name is ${name}"

perl ../scripts/markdown.pl --html4tags ${name}.md > ${name}.html

