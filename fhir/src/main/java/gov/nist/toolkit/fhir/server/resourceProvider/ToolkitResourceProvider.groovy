package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.primitive.IdDt
import ca.uhn.fhir.parser.IParser
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.servlet.HttpRequestParser
import gov.nist.toolkit.fhir.support.ResourceIndex
import gov.nist.toolkit.fhir.support.SimContext
import gov.nist.toolkit.fhir.support.SimIndexManager
import gov.nist.toolkit.registrymetadata.UuidAllocator
import org.hl7.fhir.dstu3.model.DomainResource

import javax.servlet.http.HttpServletRequest

/**
 * Collection of utilities for linking HAPI ResourceProvider
 * classes to toolkit ResDb funtions.
 */
class ToolkitResourceProvider {
    FhirContext fhirContext  // expensive to create so it is built once
    Class<?> resourceType
    HttpServletRequest request
    SimContext simContext

    ToolkitResourceProvider(Class<?> resourceType, HttpServletRequest request) {
        this.resourceType = resourceType
        this.request = request

        // get simgleton copy - expensive to build
        fhirContext = ToolkitFhirContext.get()

        // linkage to ResDb simulator environment
        simContext = new SimContext(HttpRequestParser.simIdFromRequest(request))
    }

    String resourceTypeAsString() {
        getResourceType().simpleName
    }

    /**
     * simId is encoded in URL
     * @return
     */
    SimId getSimId() { HttpRequestParser.simIdFromRequest(request) }

    /**
     * add a resource instance to the store. Allocate a resource id.  We use UUIDs
     * because we don't yet have a way to allocate sequential integers.  UUIDs are
     * legal but unconventional in FHIR.
     *
     * @param theResource - resource to be stored
     * @return - id assigned to resource
     */
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
     * addResource builds index in memory.  This gets called
     * once to flush index to disk.
     * @return
     */
    def flushIndex() {
        simContext.flushIndex()
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

    /**
     * Lucene query returns list of paths for matching resources.
     * HAPI search method requires return of List resources from
     * search methods.  This translates.
     *
     * The linkage between IBaseResource and DomainResource is
     * complicated.  So instead of having conflicts show up in
     * every ResourceProvider, I munge it here.
     *
     * @param paths - list of ResDb paths to matching resources
     * @return - list of resource bodies
     */
    List<DomainResource> searchResults(List<String> paths) {
        List<DomainResource> items

        items = paths.collect { String path ->
            File f = new File(path)
            (DomainResource) jsonParser.parseResource(resourceType, new FileReader(f))
        }

        return (List<DomainResource>) items
    }
}
