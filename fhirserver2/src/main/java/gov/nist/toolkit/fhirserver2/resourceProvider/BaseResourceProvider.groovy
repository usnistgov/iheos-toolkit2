package gov.nist.toolkit.fhirserver2.resourceProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.parser.IParser
import gov.nist.toolkit.fhir.support.SimContext
import gov.nist.toolkit.fhir.support.SimIndexManager
import gov.nist.toolkit.fhirserver2.context.ToolkitFhirContext
import gov.nist.toolkit.fhirserver2.servlet.HttpRequestParser
import gov.nist.toolkit.registrymetadata.UuidAllocator
import org.hl7.fhir.dstu3.model.DomainResource

import javax.servlet.http.HttpServletRequest
/**
 * Toolkit *ResourceProviders should extend this class.
 */
abstract class BaseResourceProvider {
    FhirContext fhirContext
    HttpServletRequest request

    abstract Class<?> getResourceType()

    String resourceTypeAsString() {
        getResourceType().simpleName
    }

    BaseResourceProvider() {
        fhirContext = ToolkitFhirContext.get()
    }

    def saveRequest(HttpServletRequest _request) {
        request = _request
    }

    IdDt setResource(DomainResource resource) {
        String resourceType = resource.getResourceType().name()
        String id = UuidAllocator.allocateNaked()
        IdDt idDt = new IdDt(resourceType, id, "1")
        resource.setId(idDt)

        String str = fhirContext.newJsonParser().encodeResourceToString(resource)

        new SimContext(HttpRequestParser.simIdFromRequest(request))
                .store(resourceType, str, id)
                .indexEvent();

        return idDt
    }

    /**
     * display Lucene index
     * @return
     */
    def displayIndex() {
        SimIndexManager.getIndexer(HttpRequestParser.simIdFromRequest(request)).dump();

    }

    IParser getJsonParser() {
        fhirContext.newJsonParser()
    }
}
