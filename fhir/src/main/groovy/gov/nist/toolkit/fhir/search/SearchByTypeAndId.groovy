package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.support.SimContext
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
/**
 * Generic search by Resource Type and ID
 */
class SearchByTypeAndId extends BaseQuery {

    SearchByTypeAndId(SimContext simContext) {
        super(simContext)
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

        return execute(builder)

    }


}
