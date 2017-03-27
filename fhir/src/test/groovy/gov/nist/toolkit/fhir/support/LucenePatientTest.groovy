package gov.nist.toolkit.fhir.support

import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import spock.lang.Specification

/**
 *
 */
class LucenePatientTest extends Specification {
    File indexDir
    Indexer index

    def setup() {
        File outLocation = new File(this.getClass().getResource('/output/finder.txt').toURI().path).parentFile
        indexDir = new File(outLocation, 'lucine.index')
        index = new Indexer(indexDir)
    }

    def 'create index and search'() {

        when: 'create index'
        boolean isOpen = index.createIndex(indexDir)

        then:
        isOpen

        when:
        index.addResource(new ResourceIndexItem('pid', '1', '/dev/null1'))
        index.addResource(new ResourceIndexItem('hid', '2', '/dev/null2'))
        index.addResource(new ResourceIndexItem('hid', '3', '/dev/null3'))
        index.finish()

        then:
        true

        when:  'initialize for search'
        IndexSearcher indexSearcher = index.openIndex(indexDir)
        Term term1 = new Term('field', 'pid')
        Term term2 = new Term('field', 'hid')

        Query query1 = new TermQuery(term1)
        Query query2 = new TermQuery(term2)

        TopDocs docs1 = indexSearcher.search(query1, 10)
        TopDocs docs2 = indexSearcher.search(query2, 10)

        then:
        docs1.totalHits == 1
        docs2.totalHits == 2

    }

}
