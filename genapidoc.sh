#!/bin/bash

#groovydoc --destdir ~/tmp/toolkit-documentation gov.nist.toolkit.toolkitApi toolkit-api/src/main/java/gov/nist/toolkit/tookitApi/*.java

output=$1
rm -rf $output
mkdir -p $output

javadoc -d $output \
`find toolkit-api -name '*.java'  -not -name '*XdsDocumentRegRep.java' -not -name '*XcaInitiatingGateway.java' -not -name '*XcaRespondingGateway.java' -not -name '*XdsDocumentConsumer.java' -not -name '*EngineSpi.java' -not -name '*BasicSimParameters.java'` \
`find toolkit-services-common -name '*.java' -not -name '*Resource.java' -not -name '*Adapter.java'` \
`find toolkit-services-common -name DocumentResource` \
`find actorfactory -name SimulatorProperties.java`


