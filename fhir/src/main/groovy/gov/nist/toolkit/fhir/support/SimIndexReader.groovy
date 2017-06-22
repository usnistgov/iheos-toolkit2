package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.simcommon.client.NoSimException
import gov.nist.toolkit.simcommon.client.SimId
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.*

/**
 * This does READONLY things on a sim index - like search
 */
class SimIndexReader {
    File indexFile = null
    ResDbIndexer indexer = null
    SimId simId
    IndexSearcher indexSearcher

    SimIndexReader(SimId _simId) {
        simId = _simId
        initIndexFile()
        indexSearcher = indexer.openIndexForSearching(indexFile)
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

}
