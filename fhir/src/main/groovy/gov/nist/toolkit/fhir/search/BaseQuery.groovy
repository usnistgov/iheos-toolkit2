package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.support.SimContext
import org.apache.lucene.document.Document
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs

/**
 *
 */
class BaseQuery {
    IndexSearcher indexSearcher

    BaseQuery(SimContext simContext) {
        indexSearcher = simContext.indexSearcher
    }


    List<String> execute(BooleanQuery.Builder builder) {
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

    // get all
    List<String> execute() {
        TopDocs docs  = indexSearcher.search(new MatchAllDocsQuery(), 1000)
        List<String> paths = docs.scoreDocs.collect { ScoreDoc scoreDoc ->
            Document doc = indexSearcher.doc(scoreDoc.doc)
            doc.get('path')  // this is the path attribute defined in ResourceIndex
            // the location in the ResDb
        }
        return paths
    }
}
