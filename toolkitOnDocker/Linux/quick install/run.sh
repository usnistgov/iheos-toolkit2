#!/bin/sh
#
# Usage may require SUDO
#
# Running
#
# TODO
# Other ports need to be published/mapped!

tkPort=$1
tkVer=$2
ecVol=$3

runDocker() {
	local tkVer=$1
	local ecVol=$2
	#echo " Web address http://localhost:$tkPort/xdstools$tkVer/"
	if [ "$ecVol" = "cleanTemp" ]
	 then
          echo "Using Temporary external cache"
          echo "Temporary external cache will be gone after container is stopped."
	  docker run --hostname xdstoolkit.test --name xdstoolkit_$tkVer -it -p$tkPort:$tkPort usnistgov-xdstoolkit:$tkVer
	else 
          # Persistent volume: User specified identifer
          echo "Using volume: $ecVol" 
	  docker run --hostname xdstoolkit.test --name xdstoolkit_$tkVer -it -p$tkPort:$tkPort --mount source=$ecVol,target=/opt/ecdir usnistgov-xdstoolkit:$tkVer
	fi
}

if [ "$1" = "--help" ]
 then
	echo "usage: ${0} <http-port> <#.#.#|latest> [ExternalCacheVolumeName|cleanTemp]" 
	exit 0
fi

if [ -z "$tkPort" ]
  then
  	echo "Port number is required."
	exit 0
fi

if [ -z "$tkVer" ]
  then
   if [ -z "$ecVol" ]
   then
	runDocker "latest" "ecLatest"	
   else
	runDocker "latest" $ecVol
   fi
  else
   if [ -z "$ecVol" ]
   then
	runDocker $tkVer ec$tkVer
   else
	runDocker $tkVer $ecVol
   fi
fi




