#!/bin/bash

#groovydoc --destdir ~/tmp/toolkit-documentation gov.nist.toolkit.toolkitApi toolkit-api/src/main/java/gov/nist/toolkit/tookitApi/*.java

output=$1
rm -rf $output
mkdir -p $output

javadoc -d $output \
`find toolkit-api -name '*.java'  -not -name '*XdsDocumentRegRep.java' -not -name '*XcaInitiatingGateway.java' -not -name '*XcaRespondingGateway.java'` \
`find toolkit-services-common -name '*.java' -not -name '*Resource.java' -not -name '*Adapter.java'`


