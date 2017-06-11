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

    private SimIndexer simIndexer = null // not initialized until needed
    IndexSearcher indexSearcher = null
    private File indexFile = null
    private ResDbIndexer indexer = null

    SimContext(SimId _simId) {
        simId = _simId
        init()
    }

    SimId getSimId() {
        return simId
    }

    /**
     * Link the ResDbIndexer to the Lucene directory inside the simulator
     * @return
     */
    private init() {
        if (!new ResDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION /* this is a stretch */)
        event = new Event(new File(resDb.getEvent()))
        indexFile = ResDb.getIndexFile(simId)
        indexer = new ResDbIndexer(indexFile)
    }

    IndexSearcher withSearch() {
       return lock().open().indexSearcher
    }

    /**
     * doesn't really mean lock yet... maybe later
     * don't know how to manage the locking yet
     */
    SimIndexer lock() {
        if (!simIndexer) {
            simIndexer = new SimIndexer(simId)
        }
        return simIndexer
    }

    void unlock() {
        if (!simIndexer) return
        simIndexer.close()  // this marks the end of queries during the processing of the current event
        simIndexer = null
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
        lock().indexOneEvent(event)
    }
}
