#!/bin/bash
# Building an Image
#
# Usage may require SUDO 
# 
# NOTE: TK_VER must match the Toolkit GitHub Release version number.
#

tkVer=$1
tkPort=8082
tkProxyPort=7282
tkPifPortRangeBegin=5050
tkPifPortRangeEnd=5060

buildImage() {
if [ -z $1 ]
 then
 docker build \
--build-arg TK_PORT=$tkPort \
--build-arg TK_PROXYPORT=$tkProxyPort \
--build-arg TK_PIFPORTRANGEBEGIN=$tkPifPortRangeBegin \
--build-arg TK_PIFPORTRANGEEND=$tkPifPortRangeEnd \
-t usnistgov-xdstoolkit . 
else 
 docker build \
--build-arg TK_VER=$tkVer \
--build-arg TK_PORT=$tkPort \
--build-arg TK_PROXYPORT=$tkProxyPort \
--build-arg TK_PIFPORTRANGEBEGIN=$tkPifPortRangeBegin \
--build-arg TK_PIFPORTRANGEEND=$tkPifPortRangeEnd \
-t usnistgov-xdstoolkit:$tkVer . 
fi
}

if [ -z "$tkVer" ]
  then
	echo "Using latest release from:"
	echo "https://github.com/usnistgov/iheos-toolkit2/releases"
	buildImage 
  else
	buildImage $tkVer
fi




