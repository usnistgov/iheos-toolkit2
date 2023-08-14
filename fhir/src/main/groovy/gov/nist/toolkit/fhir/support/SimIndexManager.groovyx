package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.index.SiTypeWrapper
import gov.nist.toolkit.simcommon.server.index.SimIndex


/**
 *
 */
class SimIndexManager {
    /*
     * Each SimIndexer manages a single Lucene IndexWriter instance for
     * each simulator.
     */

    static SimIndexer getIndexer(SimId simId) {
       SiTypeWrapper typeWrapper = null
        if (SimIndex.getIndexMap().contains(simId)) {
            typeWrapper = SimIndex.getIndexMap().get(simId)

            if (typeWrapper.getClassName()!=null) {
               def instance = this.getClass().classLoader.loadClass(typeWrapper.getClassName())?.newInstance()
                if (instance instanceof SimIndexer) {
                   return (SimIndexer)typeWrapper.getIndexer()
                } else
                    throw new RuntimeException(typeWrapper.getClassName() + " not recognized.")
            }
        } else {
            typeWrapper = new SiTypeWrapper()
            typeWrapper.setClassName("gov.nist.toolkit.fhir.support.SimIndexer")
            SimIndexer simIndexer = new SimIndexer(simId)
            typeWrapper.setIndexer(simIndexer)
            SimIndex.getIndexMap().put(simId,typeWrapper)
            return simIndexer
        }
    }

    /**
     * close all indexers
     */
    static def close() {
        SimIndex.getIndexMap().each { SimId simId, Object o ->
            if (o instanceof SimIndexer)
                ((SimIndexer)o).close()
        }
        SimIndex.getIndexMap().clear()
    }

}
