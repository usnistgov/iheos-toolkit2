package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.support.SimContext
import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.*

/**
 * Generic search by Resource Type and ID
 */
class SearchByTypeAndId {
    IndexSearcher indexSearcher

    SearchByTypeAndId(SimContext simContext) {
        indexSearcher = simContext.indexSearcher
    }

    List<String> run(String resourceType, String id) {
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
