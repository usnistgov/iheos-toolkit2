package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.PerResource
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.resourceIndexer.IResourceIndexer
import gov.nist.toolkit.utilities.io.Io
import groovy.json.JsonSlurper
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.*

/**
 * Create Lucene index of a simulator
 * Supported resource types have an indexer class
 * in gov.nist.toolkit.fhir.resourceIndexer
 * that implement the interface IResourceIndexer
 */
class SimIndexer {
    File indexFile = null
    ResDbIndexer indexer
    SimId simId
    IndexSearcher indexSearcher

    final static String INDEXER_PACKAGE = 'gov.nist.toolkit.fhir.resourceIndexer.'

    SimIndexer(SimId _simId) {
        simId = _simId
    }

    /**
     * Index FHIR sim
     */
    def buildIndex() {
        ResDb resDb = new ResDb(simId)
        initIndexFile()
        indexer.createIndex()
        resDb.perResource(null, null, new ResourceHandler())
        indexer.finish()
    }

    /**
     * Build indexes for all FHIR sims
     * @return
     */
    static int buildAllIndexes() {
        List<SimId> simIds = new ResDb().getAllSimIds()
        simIds.each { SimId simId ->
            new SimIndexer(simId).buildIndex()
        }
        return simIds.size()
    }

    SimIndexer open() {
        initIndexFile()
        indexSearcher = indexer.openIndex(indexFile)
        return this
    }

    private initIndexFile() {
        if (!new ResDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        indexFile = ResDb.getIndexFile(simId)
        indexer = new ResDbIndexer(indexFile)
    }

    /**
     * Callback for FHIR sim tree walker that indexes single resource.
     * the resource type is extracted from the resource itself.  This type string
     * is used to look up the class that indexes that resource type.  INDEXER_PACKAGE houses
     * the indexer classes for each resource type.
     */
    private class ResourceHandler implements PerResource {

        @Override
        void resource(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile) {
            if (!resourceFile.name.endsWith('json')) return
            def slurper = new JsonSlurper()
            def resource = slurper.parseText(resourceFile.text)
            String type = resource.resourceType   // resource name, like Patient
            if (!type) return
            SimResource simResource = new SimResource(actorType, transactionType, eventDir.name, resourceFile.toString())

            // this part need specialization depending on resource type
            def dy_instance = this.getClass().classLoader.loadClass(INDEXER_PACKAGE + type)?.newInstance()
            IResourceIndexer indexer1
            if (dy_instance instanceof IResourceIndexer) {
                indexer1 = dy_instance
            } else {
                throw new Exception("Cannot index resource of type ${type}")
            }

            // build resource type specific index
            ResourceIndex ri = indexer1.build(resource, simResource)

            // add in path to the resource
            ri.path = simResource.filename

            // add the single resource index to the overall sim index
            indexer.addResource(ri)
        }
    }

    /**
     *
     * @param resourceType  must be non-null
     * @param id   may be null (ignored)
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

        TopDocs docs  = indexSearcher.search(query, 1000)
        List<String> paths = docs.scoreDocs.collect { ScoreDoc scoreDoc ->
            Document doc = indexSearcher.doc(scoreDoc.doc)
            doc.get('path')
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
