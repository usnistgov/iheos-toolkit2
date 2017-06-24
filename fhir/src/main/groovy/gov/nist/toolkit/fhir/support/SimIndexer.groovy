package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.simcommon.client.NoSimException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.utilities.io.Io
import groovy.transform.TypeChecked
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
/**
 * Create Lucene index of a simulator
 * Supported index types have an indexer class
 * in gov.nist.toolkit.fhir.resourceIndexer
 * that implement the interface IResourceIndexer
 *
 * This class should only be referenced through SimContext since that is where the locking happens
 * Only one instance of this class should ever exist for a single simulator.  SimIndexManager
 * provides that
 */
@TypeChecked
class SimIndexer {
    File indexFile = null  // the directory within the Sim for holding the index
    ResDbIndexer indexer = null
    SimId simId

    // home of the resource index builders
    // There should be an indexer class for each resource of interest
    // named for that resource - for Patient it would be Patient.java or Patient.groovy
    final static String INDEXER_PACKAGE = 'gov.nist.toolkit.fhir.resourceIndexer.'

    /**
     * should only be called by SimIndexManager - that class provides synchronization
     * @param _simId
     */
    protected SimIndexer(SimId _simId) {
        simId = _simId
        initIndexFile()
    }

    def flushIndex(ResourceIndexSet resourceIndexSet) {
        indexer.openIndexForWriting()  // locks Lucene index
        indexer.addResource(resourceIndexSet)
        indexer.commit()    // commit and clear Lucene index lock
    }

//    private indexDir(File dir, ResourceIndexer resourceIndexer, def fileTypes) {
//        dir.listFiles().each { File f ->
//            if (isIndexableFile(f, fileTypes))
//                resourceIndexer.index(simId, null, null, dir, f)
//            else if (f.isDirectory())
//                indexDir(f, resourceIndexer, fileTypes)
//        }
//    }

//    static boolean isIndexableFile(File f, def fileTypes) {
//        fileTypes.find { f.name.endsWith(it)}
//    }

    /**
     * Link the ResDbIndexer to the Lucene directory inside the simulator
     * @return
     */
    private initIndexFile() {
        if (!new SimDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        indexFile = SimDb.getIndexFile(simId)
        indexer = new ResDbIndexer(indexFile)
    }

    def close() {
        if (indexer) indexer.close()
        indexer = null
    }

    /**
     * display contents of ResDb index
     * @return
     */
    def dump() {
        if (!indexFile)
            indexFile = SimDb.getIndexFile(simId)
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexFile.toPath()))
        (0..indexReader.numDocs()-1).each {
            println "Document ${it}"
            Document d = indexReader.document(it)
            println "${d.fields.find { it.name() == ResDbIndexer.PATH_FIELD}.stringValue()}"
            d.fields.each { field ->
                if (field.name() != ResDbIndexer.PATH_FIELD)
                println "   ${field.name()}: ${field.stringValue()}"
            }
        }
        indexReader.close()
    }

    /**
     * Index a single FHIR sim
     * Not synchronized - do not call while toolkit is running
     */
//    @Obsolete
//    def buildIndex() {
//        SimDb resDb = new SimDb(simId)
//        initIndexFile()
//        indexer.openIndexForWriting()
//        resDb.perResource(null, null, new ResourceIndexer())
//        indexer.commit()
//    }



    /**
     * Build indexes for all FHIR sims
     * Not synchronized - do not call while toolkit is running
     * @return
     */
//    @Obsolete
//    static int buildAllIndexes() {
//        List<SimId> simIds = new SimDb().getAllSimIds()
//        simIds.each { SimId simId ->
//            new SimIndexer(simId).buildIndex()
//        }
//        return simIds.size()
//    }

    IndexSearcher getIndexSearcher() {
        indexer.openIndexForSearching(indexFile)
    }

//    /**
//     * Callback for FHIR sim tree walker that indexes single Resource.
//     * the index type is extracted from the index itself.  This type string
//     * is used to look up the class that indexes that index type.  INDEXER_PACKAGE houses
//     * the indexer classes for each resource type.
//     */
//    private class ResourceIndexer implements PerResource {
//
//        /**
//         *
//         * @param simId - required
//         * @param actorType - if null will default to ResDb.BASE_TYPE
//         * @param transactionType - if null will default to ResDb.STORE_TRANSACTION
//         * @param eventDir
//         * @param resourceFile
//         * Indexer must already be open
//         */
//        @Override
//        void index(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile) {
//            if (!resourceFile.name.endsWith('json')) return
//            def slurper = new JsonSlurper()
//            def resource = slurper.parseText(resourceFile.text)
//            String resourceType = resource.resourceType   // index name, like Patient
//            if (!resourceType) return
//            String indexerClassName = "${resourceType}Indexer"
//            SimResource simResource = new SimResource(actorType, transactionType, eventDir.name, resourceFile.toString())
//
//            // this part need specialization depending on index type
//            // The variable being built here, indexer1, is a custom indexer for a resource
//            // So, to add a new resource the indexer must be built.  INDEXER_PACKAGE is where these
//            // are stored.  An indexer does the dirty work with Lucene so searches can be done later.
//            def dy_instance = this.getClass().classLoader.loadClass(INDEXER_PACKAGE + indexerClassName)?.newInstance()
//            IResourceIndexer indexer1
//            if (dy_instance instanceof IResourceIndexer) {
//                indexer1 = dy_instance
//            } else {
//                throw new Exception("Cannot index index of type ${resourceType}")
//            }
//
//            // build index type specific index
//            ResourceIndex ri = indexer1.build(resource, simResource)
//
//            // add in path to the index
//            ri.path = simResource.filename
//
//            // add the single index index to the overall sim index
//            indexer.addResource(ri)
//        }
//    }

    /**
     * delete Lucene index for this simId
     * @param simId
     * @return
     */
    static delete(SimId simId) {
        if (!new SimDb(simId).isSim())
            return
        File index = SimDb.getIndexFile(simId)
        Io.delete(index)
    }
}
