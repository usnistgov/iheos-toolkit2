package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import org.apache.lucene.search.IndexSearcher

/**
 *
 */
public class SimContext {
    private SimId simId = null
    private Event event = null
    private ResDb resDb = null

   // IndexSearcher indexSearcher = null   // referenced by various search utilities

    SimContext(SimId _simId) {
        simId = _simId
        init()
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
        if (!new ResDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION /* this is a stretch */)
        event = new Event(resDb.getEventDir())
    }

    /**
     * This does not require locking - only the indexing
     * since it must update a shared resource - the index tables
     * @param resourceType
     * @param resourceContents
     */
    SimContext store(String resourceType, String resourceContents, String id) {
        resDb.storeNewResource(resourceType, resourceContents, id)
        return this
    }

    /**
     * Index the current event
     */
    void indexEvent() {
        SimIndexManager.getIndexer(simId).indexEvent(event)
    }
}
