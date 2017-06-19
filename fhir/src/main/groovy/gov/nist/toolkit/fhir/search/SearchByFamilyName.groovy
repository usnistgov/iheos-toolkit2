package gov.nist.toolkit.fhir.search

import gov.nist.toolkit.fhir.support.SimContext
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.Patient
/**
 *
 */
class SearchByFamilyName extends BaseQuery {

    SearchByFamilyName(SimContext simContext) {
        super(simContext)
    }

    List<String> run(String familyName) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term1 = new Term(Patient.SP_FAMILY, familyName)
        TermQuery termQuery1 = new TermQuery(term1)
        builder.add(termQuery1, BooleanClause.Occur.MUST)

        return execute(builder)
    }
}