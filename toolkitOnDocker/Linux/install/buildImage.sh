#!/bin/bash
# Building an Image
#
# Usage might require SUDO on Linux
# 
# NOTE: TK_VER must match the Toolkit GitHub Release version number.
#
# Upgrade feature uses the /etc/toolkit/toolkit.properties lookup which was introduced in 7.1.2. Earlier versions will have a problem withthe toolkit.properties file! If an earlier release is desired, using the Install script may be a better choice.

toolkitHostIp=129.6.59.139 

# Optional. If empty, default to latest. Otherwise use the version number such as #.#.#.
tkVer=$1

# The file must be present in the current directory because Dockerfile requires that files needed at build-time must be present in the build context.
# The path must be an absolute path otherwise the file will be mapped as a directory.
# https://stackoverflow.com/questions/42248198/how-to-mount-a-single-file-in-a-volume
toolkitPropertyParentPath=${PWD}

toolkitPropertyFileName=undefined

if [ -z "$2" ]
  then
   toolkitPropertyFileName=toolkit.properties
  else
   toolkitPropertyFileName=$2
fi

if [ "$1" = "--help" ]
 then
        echo "usage: ${0} [toolkitReleaseNumber, <toolkit.properties>]"
	echo " Default: latest, toolkit.properties"
        exit 0
fi

tkEcDir=`grep External_Cache ${toolkitPropertyFileName} | cut -d= -f2`
tkPort=`grep Toolkit_Port ${toolkitPropertyFileName} | cut -d'=' -f2` 
tkTlsPort=`grep Toolkit_TLS_Port ${toolkitPropertyFileName} | cut -d'=' -f2`
tkProxyPort=`grep Proxy_Port ${toolkitPropertyFileName} | cut -d= -f2`
tkPifPortRangeBegin=`grep Listener_Port_Range ${toolkitPropertyFileName} | cut -d= -f2 | cut -d, -f1 | tr -d '[:space:]'`
tkPifPortRangeEnd=`grep Listener_Port_Range ${toolkitPropertyFileName} | cut -d= -f2 | cut -d, -f2 | tr -d '[:space:]'`
# Example: xdstoolkit.test
tkHostName=`grep Toolkit_Host ${toolkitPropertyFileName} | cut -d= -f2`


buildImage() {
if [ -z $1 ]
 then
docker build \
--build-arg TOOLKIT_PROPERTY_FILE=$toolkitPropertyFileName \
-t usnistgov-xdstoolkit . 
else 
docker build \
--build-arg TOOLKIT_PROPERTY_FILE=$toolkitPropertyFileName \
--build-arg TK_VER=$tkVer \
-t usnistgov-xdstoolkit:$tkVer . 
fi
}

# Create
# Assumes Host IP is configured properly.
# Assumes docker network create xdstoolkitNet 
# was already created and availble.

createContainer() {
docker create \
 --hostname $tkHostName \
 --name $tkHostName \
 --add-host=host.xdstoolkit.test:$toolkitHostIp \
 --network xdstoolkitNet \
 -i \
 -t \
 -p$tkPort:$tkPort \
 -p$tkTlsPort:$tkTlsPort \
 -p$tkProxyPort:$tkProxyPort \
 -p$tkPifPortRangeBegin-$tkPifPortRangeEnd:$tkPifPortRangeBegin-$tkPifPortRangeEnd \
 -v $tkEcDir:$tkEcDir \
 -v $toolkitPropertyParentPath/$toolkitPropertyFileName:/etc/toolkit/toolkit.properties \
  usnistgov-xdstoolkit:$tkVer
# 
 if [ $? -eq 0 ]
 then
  echo
  echo To Start without STDIN/STDOUT \(No screen output and runs in the background\): sudo docker start $tkHostName
  echo When screen output is desired at a later time attach to the local standard input, output, and error streams to a running container using: sudo docker attach $tkHostName
  echo
  echo To Start with STDIN/STDOUT \(Catalina.out is displayed to screen\): sudo docker start -a $tkHostName
  echo
  echo To connect to this Toolkit running on Tomcat use the URL: http://$tkHostName:$tkPort/xdstools$tkVer/
 else
  echo Error.
fi
}


if [ -z "$tkVer" ]
  then
 	tkVer="latest"
	echo "Using latest release from:"
	echo "https://github.com/usnistgov/iheos-toolkit2/releases"
	buildImage 
  else
	buildImage $tkVer
fi

if [ $? -eq 0 ]
	then
	createContainer
fi





