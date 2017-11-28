package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Resource

class SQResponseToFhirReadResponseTransform extends SQResponseToFhirSearchResponseTransform {
    static private final Logger logger = Logger.getLogger(SQResponseToFhirReadResponseTransform);


    @Override
    def addToReturn(def fullUrl, Resource resource) {
        if (!(returnThing instanceof Bundle))
            throw new SimProxyTransformException("SQResponseToFhirReadResponseTransform: attempting to return additional Resources from READ Operation. Content coming from Stored Query result")
        returnThing = resource
    }

    @Override
    boolean returnErrorIfNoContent() { return true }


}
