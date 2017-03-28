package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.PerResource
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.utilities.io.Io
import groovy.json.JsonSlurper
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.*

/**
 * Create Lucene index of a simulator
 */
class SimIndexer {
    File indexFile = null
    Indexer indexer
    SimId simId
    IndexSearcher indexSearcher

    SimIndexer(SimId _simId) {
        simId = _simId
    }

    def create() {
        ResDb resDb = new ResDb(simId)
        initIndexFile()
        indexer.createIndex()
        resDb.perResource(null, null, new ResourceHandler())
        indexer.finish()
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
        indexer = new Indexer(indexFile)
    }

    private class ResourceHandler implements PerResource {

        @Override
        void event(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir, File resourceFile) {
                if (!resourceFile.name.endsWith('json')) return
                def slurper = new JsonSlurper()
                def resource = slurper.parseText(resourceFile.text)
                String type = resource.resourceType
                String id = resource.id
                if (!id) return
                if (!type) return
                SimResource simResource = new SimResource(actorType, transactionType, eventDir.name, resourceFile.toString())
                ResourceIndex ri = new ResourceIndex()
                ri.add(new ResourceIndexItem('id', id))
                ri.add(new ResourceIndexItem('type', type))
                ri.path = simResource.filename
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
