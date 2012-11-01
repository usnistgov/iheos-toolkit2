#!/bin/sh

# Grab a particular release of the XDS Toolkit

# Usage:  grab-release.sh release.number
# Should be run from an empty directory.  Will fill the directory
# with the checked out packages

# I refer to the release.number as the build.number sometimes.

# This script grab-release.sh will pull a particular release, identified by 
# a build number, from SVN on SourceForge.  It includes several 
# packages that would be downloaded into a directory. One could then
# cd xdstools2 and run ant war to create an loadable war file.  But,
# it would not work.  XDS Toolkit GUI is built on the Google Web
# Toolkit which allows me to write my GUI code in Java and a tool
# compiles that Jave into JavaScript for auto-loading into the
# browser.  But, Google hasn't released an ant task for doing that
# compile.  The only way to do it (that I know of) is to use the 
# Eclipse plugin.  This Eclipse plugin must be used to generate
# the JavaScript before the war that gets generated has any
# real value.


if [ -z $1 ]
then
	echo "Usage: grab-release.sh release.number"
	exit -1
fi


tag="Build_${1}"

packages='xdstools2 xdstest2 xdstoolkit xds-registry-common2 testkit xdsref'

svnbase="https://iheos.svn.sourceforge.net/svnroot/iheos"
up="--username ${SVNUSERNAME} --password ${SVNPASSWORD}"

for package in $packages
do
	echo Checkout ${package}
	localname=$package
	if [ "$package" = 'xds-registry-common2' ]
	then
		localname="common"	
	fi
	echo svn co $svnbase/$package/tags/$tag $localname
	svn co $svnbase/$package/tags/$tag $localname $up
done 
