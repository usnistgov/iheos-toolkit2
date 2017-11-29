package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpResponse
import org.apache.http.message.BasicHttpResponse
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Resource

class SQResponseToFhirReadResponseTransform extends SQResponseToFhirSearchResponseTransform {
    static private final Logger logger = Logger.getLogger(SQResponseToFhirReadResponseTransform);

    @Override
    HttpResponse run(SimProxyBase base, BasicHttpResponse response) {
        logger.info('Running SQResponseToFhirReadResponseTransform')
        super.run(base, response)
    }

        @Override
    def addToReturn(def fullUrl, Resource resource) {
        if (!(returnThing instanceof Bundle))
            throw new SimProxyTransformException("SQResponseToFhirReadResponseTransform: attempting to return additional Resources from READ Operation. Content coming from Stored Query result")
        returnThing = resource
    }

    @Override
    boolean returnErrorIfNoContent() { return true }


}
