#!/bin/sh

check_args() {
 if [ $# != 4 ] ; then
  echo "Arguments: <cache> <base URL> <ids session> <idc session>"
  echo "       cache:       Folder of the Toolkit external cache"
  echo "       base URL:    URL of the toolkit; e.g., http://localhost:8080/toolkit"
  echo "       ids session: Test session created for testing IDS path; should be epsilon"
  echo "       idc session: Test session created for testing IDC path; should be theta"
  exit 1
 fi
 if [ ! -e $1 ] ; then
  echo "You specified $1 as the Toolkit external cache, but that folder does not exist."
  exit 1
 fi
 if [ ! -e $1/ImageCache ] ; then
  echo "You specified $1 as the Toolkit external cache."
  echo "The image cache should appear as $1/ImageCache, but that folder does not exist."
  exit 1
 fi
}



check_args $*

IMAGE_CACHE=$1/ImageCache
BASE_URL=$2
SESSION_IDS=$3
SESSION_IDC=$4

URL_IDS="$BASE_URL/httpsim/$SESSION_IDS""__simulator_ids/ids/wado.ret.ids"
URL_IDC="$BASE_URL/httpsim/$SESSION_IDC""__ids/ids/wado.ret.ids"

perl ids-normal-cases.pl \
	$IMAGE_CACHE/sim/ids-repository $URL_IDS > /tmp/imaging-document-source-path.log
if [ $? != 0 ] ; then
 echo "Failed to run Imaging Document Source normal path"
 echo "This script failed:"
 echo "    perl ids-normal-cases.pl $IMAGE_CACHE/sim/ids-repository $URL_IDS"
 exit 1
fi


perl ids-normal-cases.pl \
	$IMAGE_CACHE/sim/idc-dataset-a $URL_IDC > /tmp/imaging-document-consumer-path.log
if [ $? != 0 ] ; then
 echo "Failed to run Imaging Document Consumer normal path"
 echo "This script failed:"
 echo "    perl ids-normal-cases.pl $IMAGE_CACHE/sim/idc-dataset-a $URL_IDC"
 exit 1
fi


perl ids-exception-cases.pl \
	$URL_IDC > /tmp/exception-cases.log
if [ $? != 0 ] ; then
 echo "Failed to run Exception cases"
 echo "This script failed:"
 echo "    perl ids-exception-cases.pl $URL_IDC"
 exit 1
fi

