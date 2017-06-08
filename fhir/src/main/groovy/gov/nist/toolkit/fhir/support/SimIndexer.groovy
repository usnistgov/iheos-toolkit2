package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.PerResource
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.resourceIndexer.IResourceIndexer
import gov.nist.toolkit.utilities.io.Io
import groovy.json.JsonSlurper
import groovy.transform.Synchronized
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.*

/**
 * Create Lucene index of a simulator
 * Supported index types have an indexer class
 * in gov.nist.toolkit.fhir.resourceIndexer
 * that implement the interface IResourceIndexer
 */
class SimIndexer {
    File indexFile = null
    ResDbIndexer indexer = null
    SimId simId
    IndexSearcher indexSearcher

    final static String INDEXER_PACKAGE = 'gov.nist.toolkit.fhir.resourceIndexer.'

    SimIndexer(SimId _simId) {
        simId = _simId
    }

    /**
     * Index a single FHIR sim
     * Not synchronized - do not call while toolkit is running
     */
    def buildIndex() {
        ResDb resDb = new ResDb(simId)
        initIndexFile()
        indexer.openIndexForWriting()
        resDb.perResource(null, null, new ResourceIndexer())
        indexer.finish()
    }

    @Synchronized
    def indexOneEvent(File eventDir) {
        ResDb resDb = new ResDb(simId)
        initIndexFile()
        indexer.openIndexForWriting()
        ResourceIndexer resourceIndexer = new ResourceIndexer();
        for (File resourceFile : eventDir.listFiles()) {
            if (resourceFile.name == 'date.ser')
                continue
            resourceIndexer.index(simId, null, null, eventDir, resourceFile)
        }
        indexer.finish()
    }

    /**
     * Build indexes for all FHIR sims
     * Not synchronized - do not call while toolkit is running
     * @return
     */
    static int buildAllIndexes() {
        List<SimId> simIds = new ResDb().getAllSimIds()
        simIds.each { SimId simId ->
            new SimIndexer(simId).buildIndex()
        }
        return simIds.size()
    }

    /**
     * open for search
     * @return
     */
    SimIndexer open() {
        initIndexFile()
        indexSearcher = indexer.openIndexForSearching(indexFile)
        return this
    }

    def close() {
        if (indexer) indexer.close()
        indexer = null
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

    /**
     * Callback for FHIR sim tree walker that indexes single Resource.
     * the index type is extracted from the index itself.  This type string
     * is used to look up the class that indexes that index type.  INDEXER_PACKAGE houses
     * the indexer classes for each resource type.
     */
    private class ResourceIndexer implements PerResource {

        /**
         *
         * @param simId - required
         * @param actorType - if null will default to ResDb.BASE_TYPE
         * @param transactionType - if null will default to ResDb.STORE_TRANSACTION
         * @param eventDir
         * @param resourceFile
         * Indexer must already be open
         */
        @Override
        void index(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile) {
            if (!resourceFile.name.endsWith('json')) return
            def slurper = new JsonSlurper()
            def resource = slurper.parseText(resourceFile.text)
            String type = resource.resourceType   // index name, like Patient
            if (!type) return
            SimResource simResource = new SimResource(actorType, transactionType, eventDir.name, resourceFile.toString())

            // this part need specialization depending on index type
            // The variable being built here, indexer1, is a custom indexer for a resource
            // So, to add a new resource the indexer must be built.  INDEXER_PACKAGE is where these
            // are stored.  An indexer does the dirty work with Lucene so searches can be done later.
            def dy_instance = this.getClass().classLoader.loadClass(INDEXER_PACKAGE + type)?.newInstance()
            IResourceIndexer indexer1
            if (dy_instance instanceof IResourceIndexer) {
                indexer1 = dy_instance
            } else {
                throw new Exception("Cannot index index of type ${type}")
            }

            // build index type specific index
            ResourceIndex ri = indexer1.build(resource, simResource)

            // add in path to the index
            ri.path = simResource.filename

            // add the single index index to the overall sim index
            indexer.addResource(ri)
        }
    }

    /**
     *
     * @param resourceType  must be non-null
     * @param id   may be null (ignored) - if null then all resources of this type will be returned
     * @return
     */
    List<String> lookupByTypeAndId(String resourceType, String id) {
        if (!indexFile)
            throw new Exception('SimIndexer : indexFile not specified')
        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term1 = new Term('type', resourceType)
        TermQuery termQuery1 = new TermQuery(term1)
        builder.add(termQuery1, BooleanClause.Occur.MUST)

        if (id) {
            Term term2 = new Term('id', id)
            TermQuery termQuery2 = new TermQuery(term2)
            builder.add(termQuery2, BooleanClause.Occur.MUST)
        }

        BooleanQuery query = builder.build()

        // TopDocs is defined by Lucene as the container for
        // search results
        TopDocs docs  = indexSearcher.search(query, 1000)
        List<String> paths = docs.scoreDocs.collect { ScoreDoc scoreDoc ->
            Document doc = indexSearcher.doc(scoreDoc.doc)
            doc.get('path')  // this is the path attribute defined in ResourceIndex
                             // the location in the ResDb
        }
        return paths
    }

    static delete(SimId simId) {
        if (!new ResDb(simId).isSim())
            return
        File index = ResDb.getIndexFile(simId)
        Io.delete(index)
    }
}
