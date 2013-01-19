#!/bin/sh


# Tag a release of XDS Toolkit in svn on SourceForge
#
# This must be run from the top level directory of this package as:
#     sh scripts/tag-release.sh
# so that the build number gets picked up correctly
#
# The build number in war/build.num is used to construct the 
# SVN tag Build_${build.num}

packages='xdstools2 xdstest2 evs  xds-registry-common2 testkit xdsref'

buildnum=`awk '/build.number/ {split($0, a, "="); print a[2]}' < war/build.num`

echo "buildnum is $buildnum"

tag="Build_${buildnum}"

echo "tag is ${tag}"

svnbase="https://iheos.svn.sourceforge.net/svnroot/iheos"
up="--username ${SVNUSERNAME} --password ${SVNPASSWORD}"

for package in $packages
do
	echo Tagging ${package}
	echo svn copy $svnbase/$package/trunk $svnbase/$package/tags/$tag -m "Build Release" ${up}
	svn copy $svnbase/$package/trunk $svnbase/$package/tags/$tag -m "Build Release" ${up}
done
