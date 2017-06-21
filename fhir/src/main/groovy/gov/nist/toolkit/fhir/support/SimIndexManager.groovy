package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.SimId

/**
 *
 */
class SimIndexManager {
    /*
     * Each SimIndexer manages a single Lucene IndexWriter instance for
     * each simulator.
     */
    static private Map<SimId, SimIndexer> indexers = [ : ]

    static SimIndexer getIndexer(SimId simId) {
        SimIndexer si = indexers[simId]
        if (!si) {
            si = new SimIndexer(simId)
            indexers[simId] = si
        }
        return si
    }

    /**
     * close all indexers
     */
    static def close() {
        indexers.each { SimId simId, SimIndexer indexer ->
            indexer.close()
        }
        indexers.clear()
    }

}
