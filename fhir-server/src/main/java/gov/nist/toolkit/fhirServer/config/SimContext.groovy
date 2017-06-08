package gov.nist.toolkit.fhirServer.config

import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.fhir.support.ResDb
import gov.nist.toolkit.fhir.support.ResDbIndexer
import gov.nist.toolkit.fhir.support.SimIndexer
import org.apache.lucene.search.IndexSearcher

/**
 *
 */
public class SimContext {
    private SimId simId = null

    private SimIndexer simIndexer = null // not initialized until needed
    private IndexSearcher indexSearcher = null
    File indexFile = null
    ResDbIndexer indexer = null


    SimContext(SimId _simId) {
        simId = _simId
    }

    SimId getSimId() {
        return simId
    }

    /**
     * Link the ResDbIndexer to the Lucene directory inside the simulator
     * @return
     */
    private initIndexFile() {
        if (!new ResDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        indexFile = ResDb.getIndexFile(simId)
        indexer = new ResDbIndexer(indexFile)
    }

    SimIndexer getSimIndexer() {
        if (!simIndexer) {
            simIndexer = new SimIndexer(simId)
        }
        return simIndexer
    }

    void close() {
        if (!simIndexer) return
        simIndexer.close()
        simIndexer = null
    }
}
