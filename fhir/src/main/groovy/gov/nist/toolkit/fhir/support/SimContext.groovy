package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.resourceIndexer.IResourceIndexer
import gov.nist.toolkit.simcommon.client.NoSimException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import org.apache.lucene.search.IndexSearcher
import org.hl7.fhir.dstu3.model.DomainResource

/**
 *
 */
public class SimContext {
    private SimId simId = null
    private Event event = null
    private SimDb simDb = null
    // all the resources added in a transaction (aka a SimContext instance)
    ResourceIndexSet resourceIndexSet = new ResourceIndexSet()

   // IndexSearcher indexSearcher = null   // referenced by various search utilities

    /**
     * this is usable only for tests
     * production code must use other constructor or two events will
     * be created for each transaction
     * @param _simId
     */
    SimContext(SimId _simId) {
        simId = _simId
        init()
    }

    SimContext(SimDb _simDb) {
        simDb = _simDb
        simId = simDb.simId
        event = new Event(simDb.getEventDir())
    }

    SimId getSimId() {
        return simId
    }

    IndexSearcher getIndexSearcher() {
        SimIndexManager.getIndexer(simId).indexSearcher
    }

    /**
     * Link the ResDbIndexer to the Lucene directory inside the simulator
     * @return
     */
    private init() {
        if (!simDb) {  // this triggers if SimContext(SimId) was used - testing only
            if (!new SimDb(simId).isSim())
                throw new NoSimException("Sim ${simId} does not exist")
            simDb = new SimDb(simId, SimDb.BASE_TYPE, SimDb.ANY_TRANSACTION /* this is a stretch */)
            event = new Event(simDb.getEventDir())
        }
    }

    /**
     * This does not require locking - only the indexing
     * since it must update a shared resource - the index tables
     * @param resourceType - resource type name
     * @param theResource - resource object
     * @param resourceString - resource as JSON string
     */
    File store(String resourceType, DomainResource theResource, String id) {
        File resourceFile = simDb.storeNewResource(resourceType,
                ToolkitFhirContext.get().newJsonParser().encodeResourceToString(theResource),
                id)
        return resourceFile
    }

    /**
     * build index for one resource
     * @param resourceType - resource type name
     * @param theResource - resource object
     * @param resourceString - resource object as string
     * @param id - resource id
     */
    ResourceIndex index(String resourceType, DomainResource theResource, String id) {
        String indexerClassName = "${resourceType}Indexer"

        // this part need specialization depending on index type
        // The variable being built here, indexer1, is a custom indexer for a resource
        // So, to add a new resource the indexer must be built.  INDEXER_PACKAGE is where these
        // are stored.  An indexer does the dirty work with Lucene so searches can be done later.
        def dy_instance = this.getClass().classLoader.loadClass(SimIndexer.INDEXER_PACKAGE + indexerClassName)?.newInstance()
        IResourceIndexer indexer
        if (dy_instance instanceof IResourceIndexer) {
            indexer = dy_instance
        } else {
            throw new Exception("Cannot index index of type ${resourceType}")
        }

        // build index type specific index
        ResourceIndex ri = indexer.build(theResource, id)
        resourceIndexSet.add(ri)
        return ri
    }

    /**
     * Index the current event
     */
    void flushIndex() {
        SimIndexManager.getIndexer(simId).flushIndex(resourceIndexSet)
    }
}
