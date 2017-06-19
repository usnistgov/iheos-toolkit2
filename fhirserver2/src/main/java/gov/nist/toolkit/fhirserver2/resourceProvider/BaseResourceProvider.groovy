package gov.nist.toolkit.fhirserver2.resourceProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.parser.IParser
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.SimContext
import gov.nist.toolkit.fhir.support.SimIndexManager
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
    SimContext simContext

    abstract Class<?> getResourceType()

    String resourceTypeAsString() {
        getResourceType().simpleName
    }

    BaseResourceProvider() {
        fhirContext = ToolkitFhirContext.get()
    }

    def saveRequest(HttpServletRequest _request) {
        request = _request

        simContext = new SimContext(HttpRequestParser.simIdFromRequest(request))
    }

    SimId getSimId() { HttpRequestParser.simIdFromRequest(request) }

    IdDt addResource(DomainResource theResource) {
        String resourceType = theResource.getResourceType().name()
        String id = UuidAllocator.allocateNaked()
        IdDt idDt = new IdDt(resourceType, id, "1")
        theResource.setId(idDt)

        if (!simContext) simContext = new SimContext(simId)

        File resourceFile = simContext.store(resourceType, theResource, id)
        ResourceIndex resourceIndex = simContext.index(resourceType, theResource, id)
        resourceIndex.path = resourceFile.path

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

    List searchResults(List<String> paths) {
        def items

        items = paths.collect { String path ->
            File f = new File(path)
            jsonParser.parseResource(resourceType, new FileReader(f))
        }

        return items
    }
}
